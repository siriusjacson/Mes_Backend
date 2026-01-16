package com.mess.mes_backend.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mess.mes_backend.entity.ProcessLinkTpl;
import com.mess.mes_backend.entity.ProcessNodeTpl;
import com.mess.mes_backend.mapper.ProcessLinkTplMapper;
import com.mess.mes_backend.mapper.ProcessNodeTplMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProcessService extends ServiceImpl<ProcessNodeTplMapper, ProcessNodeTpl> {

    @Autowired
    private ProcessLinkTplMapper relationMapper;

    /**
     * Create a process node
     */
    public ProcessNodeTpl createNode(ProcessNodeTpl node) {
        this.save(node);
        return node;
    }

    /**
     * Connect two process nodes (A -> B)
     */
    @Transactional
    public String connectNodes(Long preId, Long nextId, Long modelId) {
        ProcessLinkTpl link = new ProcessLinkTpl();
        link.setPreProcessId(preId);
        link.setNextProcessId(nextId);
        link.setModelId(modelId);
        
        relationMapper.insert(link);
        return "Connection successful";
    }

    /**
     * Get all process nodes for a model
     */
    public List<ProcessNodeTpl> getNodesByModel(Long modelId) {
        return this.lambdaQuery().eq(ProcessNodeTpl::getModelId, modelId).list();
    }
}
