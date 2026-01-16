package com.mess.mes_backend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mess.mes_backend.common.enums.TaskStatus;
import com.mess.mes_backend.component.ProcessCacheManager;
import com.mess.mes_backend.dto.TaskCompleteReq;
import com.mess.mes_backend.entity.ProcessLinkTpl;
import com.mess.mes_backend.entity.ProjectInstance;
import com.mess.mes_backend.entity.TaskInstance;
import com.mess.mes_backend.entity.WorkRecord;
import com.mess.mes_backend.mapper.ProjectInstanceMapper;
import com.mess.mes_backend.mapper.TaskInstanceMapper;
import com.mess.mes_backend.mapper.WorkRecordMapper;
import com.mess.mes_backend.server.WebSocketServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskService {

    @Autowired
    private TaskInstanceMapper taskMapper;
    
    @Autowired
    private ProjectInstanceMapper projectMapper; // 新增：为了查 modelId

    @Autowired
    private WorkRecordMapper workRecordMapper;

    @Autowired
    private ProcessCacheManager processCacheManager; // 新增：缓存管家

    public List<TaskInstance> getTasksByProject(Long projectId) {
        return taskMapper.selectList(new QueryWrapper<TaskInstance>().eq("project_id", projectId));
    }

    @Transactional(rollbackFor = Exception.class)
    public void completeTask(Long taskId, Long operatorId) {
        // 1. 悲观锁获取任务
        TaskInstance currentTask = taskMapper.selectByIdForUpdate(taskId);
        if (currentTask == null) throw new RuntimeException("任务不存在");
        if (currentTask.getStatus() == TaskStatus.COMPLETED) return; // 幂等性保护

        // 2. 更新状态
        currentTask.setStatus(TaskStatus.COMPLETED);
        currentTask.setOperatorId(operatorId);
        currentTask.setEndTime(LocalDateTime.now());
        taskMapper.updateById(currentTask);

        // 3. 记录日志
        WorkRecord record = new WorkRecord();
        record.setTaskId(taskId);
        record.setUserId(operatorId);
        record.setUserName("Worker-" + operatorId);
        record.setActionType(3);
        record.setCreateTime(LocalDateTime.now());
        workRecordMapper.insert(record);

        // 4. 自动解锁 (使用 Redis 加速)
        unlockNextTasks(currentTask);
    }

    private void unlockNextTasks(TaskInstance currentTask) {
        // 🚀 优化：先查项目拿到 modelId，再从 Redis 获取全量 DAG 图
        ProjectInstance project = projectMapper.selectById(currentTask.getProjectId());
        Long modelId = project.getModelId();
        
        // 此处读取 Redis，速度极快
        List<ProcessLinkTpl> allLinks = processCacheManager.getProcessLinks(modelId);

        // 内存过滤：找出当前节点的所有下级 (代替了数据库查询)
        List<Long> nextNodeIds = allLinks.stream()
                .filter(link -> link.getPreProcessId().equals(currentTask.getNodeTplId()))
                .map(ProcessLinkTpl::getNextProcessId)
                .collect(Collectors.toList());

        if (nextNodeIds.isEmpty()) return;

        // 查出对应的下级任务实例
        QueryWrapper<TaskInstance> nextTaskQuery = new QueryWrapper<>();
        nextTaskQuery.eq("project_id", currentTask.getProjectId())
                     .in("node_tpl_id", nextNodeIds);
        List<TaskInstance> nextTasks = taskMapper.selectList(nextTaskQuery);

        for (TaskInstance nextTask : nextTasks) {
            // 🚀 优化：把 allLinks 传进去，避免内部再次查库
            if (checkAllPrevTasksDone(nextTask, allLinks)) {
                nextTask.setStatus(TaskStatus.PENDING);
                taskMapper.updateById(nextTask);

                // WebSocket 推送
                String msg = String.format("{\"event\":\"TASK_UNLOCKED\", \"projectId\":%d, \"taskName\":\"%s\"}", 
                                           nextTask.getProjectId(), nextTask.getTaskName());
                WebSocketServer.sendToAll(msg);
            }
        }
    }

    /**
     * 重载方法：使用内存中的 Links 进行判断
     */
    private boolean checkAllPrevTasksDone(TaskInstance task, List<ProcessLinkTpl> allLinks) {
        // 内存过滤：找出指向当前节点的所有连线
        List<Long> prevNodeTplIds = allLinks.stream()
                .filter(link -> link.getNextProcessId().equals(task.getNodeTplId()))
                .map(ProcessLinkTpl::getPreProcessId)
                .collect(Collectors.toList());

        if (prevNodeTplIds.isEmpty()) return true;

        // 这一步查任务状态，必须查数据库（因为状态是动态变的，不适合缓存）
        QueryWrapper<TaskInstance> prevTasksQuery = new QueryWrapper<>();
        prevTasksQuery.eq("project_id", task.getProjectId())
                      .in("node_tpl_id", prevNodeTplIds);
        List<TaskInstance> prevTasks = taskMapper.selectList(prevTasksQuery);

        for (TaskInstance pt : prevTasks) {
            if (pt.getStatus() != TaskStatus.COMPLETED) {
                return false;
            }
        }
        return true;
    }
}
