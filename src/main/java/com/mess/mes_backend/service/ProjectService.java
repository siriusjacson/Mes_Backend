package com.mess.mes_backend.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mess.mes_backend.entity.*;
import com.mess.mes_backend.mapper.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ProjectService extends ServiceImpl<ProjectInstanceMapper, ProjectInstance> {

    @Autowired
    private ProcessNodeTplMapper nodeTplMapper;

    @Autowired
    private ProcessLinkTplMapper linkTplMapper;

    @Autowired
    private TaskInstanceMapper taskInstanceMapper;

    /**
     * Instantiate a Project from Template
     */
    @Transactional(rollbackFor = Exception.class)
    public Long createProject(String projectNo, String deviceSn, Long modelId) {
        // 1. Create Project Instance
        ProjectInstance project = new ProjectInstance();
        project.setProjectNo(projectNo);
        project.setDeviceSn(deviceSn);
        project.setModelId(modelId);
        project.setStatus(0); // Not Started
        this.save(project);

        Long projectId = project.getId();

        // 2. Query all process templates for this model
        List<ProcessNodeTpl> nodes = nodeTplMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<ProcessNodeTpl>()
                        .eq(ProcessNodeTpl::getModelId, modelId)
        );

        if (nodes.isEmpty()) {
            throw new RuntimeException("No process template found for model: " + modelId);
        }

        // 3. Query all links to find topology
        List<ProcessLinkTpl> links = linkTplMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<ProcessLinkTpl>()
                        .eq(ProcessLinkTpl::getModelId, modelId)
        );

        // Identify nodes that are "destinations" (have incoming edges)
        Set<Long> nodesWithIncomingEdges = links.stream()
                .map(ProcessLinkTpl::getNextProcessId)
                .collect(Collectors.toSet());

        // 4. Instantiate Tasks
        for (ProcessNodeTpl node : nodes) {
            TaskInstance task = new TaskInstance();
            task.setProjectId(projectId);
            task.setNodeTplId(node.getId());
            task.setTaskName(node.getProcessName()); // Snapshot name

            // If a node is NOT in nodesWithIncomingEdges, it's a Root Node -> Pending (1)
            // Otherwise -> Locked (0)
            if (!nodesWithIncomingEdges.contains(node.getId())) {
                task.setStatus(1); // Pending
            } else {
                task.setStatus(0); // Locked
            }

            taskInstanceMapper.insert(task);
        }

        return projectId;
    }
}
