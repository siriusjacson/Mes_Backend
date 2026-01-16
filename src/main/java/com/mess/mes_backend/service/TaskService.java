package com.mess.mes_backend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mess.mes_backend.entity.ProcessLinkTpl;
import com.mess.mes_backend.entity.TaskInstance;
import com.mess.mes_backend.entity.WorkRecord;
import com.mess.mes_backend.mapper.ProcessLinkTplMapper;
import com.mess.mes_backend.mapper.TaskInstanceMapper;
import com.mess.mes_backend.mapper.WorkRecordMapper;
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
    private ProcessLinkTplMapper linkMapper;

    @Autowired
    private WorkRecordMapper workRecordMapper; // 新增：注入日志Mapper

    public List<TaskInstance> getTasksByProject(Long projectId) {
        QueryWrapper<TaskInstance> query = new QueryWrapper<>();
        query.eq("project_id", projectId);
        return taskMapper.selectList(query);
    }

    @Transactional(rollbackFor = Exception.class)
    public void completeTask(Long taskId, Long operatorId) {
        // 1. 获取当前任务
        TaskInstance currentTask = taskMapper.selectById(taskId);
        if (currentTask == null) return;

        // 2. 更新任务状态
        currentTask.setStatus(3); // 3: Completed
        currentTask.setOperatorId(operatorId);
        currentTask.setEndTime(LocalDateTime.now());
        taskMapper.updateById(currentTask);

        // 3. 【新增】记录操作日志 (WorkRecord)
        WorkRecord record = new WorkRecord();
        record.setTaskId(taskId);
        record.setUserId(operatorId);
        record.setUserName("Worker-" + operatorId); // 此处简化，实际应查用户表
        record.setActionType(3); // 3: Complete
        record.setCreateTime(LocalDateTime.now());
        workRecordMapper.insert(record);

        // 4. 自动解锁下一道工序 (DAG 逻辑)
        unlockNextTasks(currentTask);
    }

    private void unlockNextTasks(TaskInstance currentTask) {
        // 找出当前节点的所有“下级节点” ID
        QueryWrapper<ProcessLinkTpl> linkQuery = new QueryWrapper<>();
        linkQuery.eq("pre_process_id", currentTask.getNodeTplId());

        List<ProcessLinkTpl> links = linkMapper.selectList(linkQuery);
        if (links.isEmpty()) return;

        List<Long> nextNodeTplIds = links.stream()
                .map(ProcessLinkTpl::getNextProcessId)
                .collect(Collectors.toList());

        // 找出这些下级节点对应的“任务实例”
        QueryWrapper<TaskInstance> nextTaskQuery = new QueryWrapper<>();
        nextTaskQuery.eq("project_id", currentTask.getProjectId())
                .in("node_tpl_id", nextNodeTplIds);

        List<TaskInstance> nextTasks = taskMapper.selectList(nextTaskQuery);

        // 检查每个下级任务的前置条件是否都满足
        for (TaskInstance nextTask : nextTasks) {
            if (checkAllPrevTasksDone(nextTask)) {
                nextTask.setStatus(1); // 1: Pending (解锁，变为可执行)
                taskMapper.updateById(nextTask);
            }
        }
    }

    private boolean checkAllPrevTasksDone(TaskInstance task) {
        // 找出指向当前任务的所有“前置节点”
        QueryWrapper<ProcessLinkTpl> prevLinkQuery = new QueryWrapper<>();
        prevLinkQuery.eq("next_process_id", task.getNodeTplId());
        List<ProcessLinkTpl> incomingLinks = linkMapper.selectList(prevLinkQuery);

        if (incomingLinks.isEmpty()) return true; // 没有前置，直接通过

        List<Long> prevNodeTplIds = incomingLinks.stream()
                .map(ProcessLinkTpl::getPreProcessId)
                .collect(Collectors.toList());

        // 检查这些前置节点对应的任务实例，是否全部都是状态 3 (Completed)
        QueryWrapper<TaskInstance> prevTasksQuery = new QueryWrapper<>();
        prevTasksQuery.eq("project_id", task.getProjectId())
                .in("node_tpl_id", prevNodeTplIds);

        List<TaskInstance> prevTasks = taskMapper.selectList(prevTasksQuery);

        for (TaskInstance pt : prevTasks) {
            if (pt.getStatus() != 3) {
                return false; // 只要有一个没做完，就不能解锁
            }
        }
        return true;
    }
}
