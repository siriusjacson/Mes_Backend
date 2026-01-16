package com.mess.mes_backend.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mess.mes_backend.entity.ProcessRelation;
import com.mess.mes_backend.entity.ProcessTemplate;
import com.mess.mes_backend.mapper.ProcessRelationMapper;
import com.mess.mes_backend.mapper.ProcessTemplateMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProcessService extends ServiceImpl<ProcessTemplateMapper, ProcessTemplate> {

    @Autowired
    private ProcessRelationMapper relationMapper;

    /**
     * Create a process node
     */
    public ProcessTemplate createNode(ProcessTemplate node) {
        this.save(node); // Call parent save method
        return node;
    }

    /**
     * Connect two process nodes (A -> B)
     */
    @Transactional
    public String connectNodes(Long preId, Long nextId, Long modelId) {
        // Here we should add validation (e.g., same model? cycle detection?), skipping for now as per instructions
        
        ProcessRelation relation = new ProcessRelation();
        relation.setPreProcessId(preId);
        relation.setNextProcessId(nextId);
        relation.setModelId(modelId);
        
        relationMapper.insert(relation);
        return "Connection successful";
    }

    /**
     * Get all process nodes for a model
     */
    public List<ProcessTemplate> getNodesByModel(Long modelId) {
        return this.lambdaQuery().eq(ProcessTemplate::getModelId, modelId).list();
    }
}
