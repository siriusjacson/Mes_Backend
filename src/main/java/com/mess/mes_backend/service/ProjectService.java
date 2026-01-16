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
import com.mess.mes_backend.common.enums.TaskStatus;

@Service
public class ProjectService extends ServiceImpl<ProjectInstanceMapper, ProjectInstance> {

    @Autowired
    private ProcessNodeTplMapper nodeTplMapper;

    @Autowired
    private ProcessLinkTplMapper linkTplMapper;

    @Autowired
    private TaskInstanceMapper taskInstanceMapper;

    /**
     * 从模板实例化项目
     */
    @Transactional(rollbackFor = Exception.class)
    public Long createProject(String projectNo, String deviceSn, Long modelId) {
        // 1. 创建项目实例
        ProjectInstance project = new ProjectInstance();
        project.setProjectNo(projectNo);
        project.setDeviceSn(deviceSn);
        project.setModelId(modelId);
        project.setStatus(0); // 未开始
        this.save(project);

        Long projectId = project.getId();

        // 2. 查询该模型的所有流程模板
        List<ProcessNodeTpl> nodes = nodeTplMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<ProcessNodeTpl>()
                        .eq(ProcessNodeTpl::getModelId, modelId)
        );

        if (nodes.isEmpty()) {
            throw new RuntimeException("未找到模型的流程模板: " + modelId);
        }

        // 3. 查询所有连接以查找拓扑结构
        List<ProcessLinkTpl> links = linkTplMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<ProcessLinkTpl>()
                        .eq(ProcessLinkTpl::getModelId, modelId)
        );

        // 识别作为“目的地”（有入边）的节点
        Set<Long> nodesWithIncomingEdges = links.stream()
                .map(ProcessLinkTpl::getNextProcessId)
                .collect(Collectors.toSet());

        // 4. 实例化任务
        for (ProcessNodeTpl node : nodes) {
            TaskInstance task = new TaskInstance();
            task.setProjectId(projectId);
            task.setNodeTplId(node.getId());
            task.setTaskName(node.getProcessName()); // 快照名称

// Snapshot name

// ...
            // 如果节点不在 nodesWithIncomingEdges 中，则是根节点 -> 待办 (1)
            // 否则 -> 锁定 (0)
            if (!nodesWithIncomingEdges.contains(node.getId())) {
                task.setStatus(TaskStatus.PENDING); // 待办
            } else {
                task.setStatus(TaskStatus.LOCKED); // 锁定
            }

            taskInstanceMapper.insert(task);
        }

        return projectId;
    }
}
