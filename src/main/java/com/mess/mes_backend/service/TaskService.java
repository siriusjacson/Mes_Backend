package com.mess.mes_backend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mess.mes_backend.entity.ProcessLinkTpl;
import com.mess.mes_backend.entity.TaskInstance;
import com.mess.mes_backend.mapper.ProcessLinkTplMapper;
import com.mess.mes_backend.mapper.TaskInstanceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskService {

    @Autowired
    private TaskInstanceMapper taskMapper;

    @Autowired
    private ProcessLinkTplMapper linkMapper;

    @Transactional(rollbackFor = Exception.class)
    public void completeTask(Long taskId) {
        // 1. Update current task status to Completed (3)
        TaskInstance currentTask = taskMapper.selectById(taskId);
        if (currentTask == null) return;
        
        currentTask.setStatus(3);
        taskMapper.updateById(currentTask);

        // 2. Find next node templates based on current task's node template
        // We need to find the links where prev_node_id == currentTask.nodeTplId
        QueryWrapper<ProcessLinkTpl> linkQuery = new QueryWrapper<>();
        linkQuery.eq("prev_node_id", currentTask.getNodeTplId());
        // Note: we assume the link also respects model_id or project context, 
        // but for simplicity following the snippet we use node IDs.
        List<ProcessLinkTpl> links = linkMapper.selectList(linkQuery);
        
        if (links.isEmpty()) {
            return; 
        }

        List<Long> nextNodeTplIds = links.stream()
                .map(ProcessLinkTpl::getNextNodeId)
                .collect(Collectors.toList());

        // 3. Find the actual Task Instances for these next nodes within the SAME PROJECT
        QueryWrapper<TaskInstance> nextTaskQuery = new QueryWrapper<>();
        nextTaskQuery.eq("project_id", currentTask.getProjectId())
                     .in("node_tpl_id", nextNodeTplIds);
        
        List<TaskInstance> nextTasks = taskMapper.selectList(nextTaskQuery);

        // 4. Check if predecessors are done for each next task
        for (TaskInstance nextTask : nextTasks) {
            if (checkAllPrevTasksDone(nextTask)) {
                nextTask.setStatus(1); // Unlock: Pending
                taskMapper.updateById(nextTask);
                // System.out.println("Task Unlocked: " + nextTask.getId());
            }
        }
    }

    private boolean checkAllPrevTasksDone(TaskInstance task) {
        // Find all previous nodes for this task's node
        QueryWrapper<ProcessLinkTpl> prevLinkQuery = new QueryWrapper<>();
        prevLinkQuery.eq("next_node_id", task.getNodeTplId());
        List<ProcessLinkTpl> incomingLinks = linkMapper.selectList(prevLinkQuery);

        if (incomingLinks.isEmpty()) return true; // No predecessors

        List<Long> prevNodeTplIds = incomingLinks.stream()
                .map(ProcessLinkTpl::getPrevNodeId)
                .collect(Collectors.toList());

        // Check if ALL these previous nodes have Completed (3) status in the current project
        QueryWrapper<TaskInstance> prevTasksQuery = new QueryWrapper<>();
        prevTasksQuery.eq("project_id", task.getProjectId())
                      .in("node_tpl_id", prevNodeTplIds);
        
        List<TaskInstance> prevTasks = taskMapper.selectList(prevTasksQuery);
        
        // Logical check: 
        // We expect exactly one task instance per node template in a project (simplification).
        // If we found them, check if they are all status 3.
        for (TaskInstance pt : prevTasks) {
            if (pt.getStatus() != 3) {
                return false;
            }
        }
        
        // Also ensure we actually found all predecessors (if strictly required)
        // For now assuming if they exist they are checked.
        return true;
    }
}
