package com.mess.mes_backend.controller;

import com.mess.mes_backend.entity.ProcessTemplate;
import com.mess.mes_backend.service.ProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/process")
@CrossOrigin
public class ProcessController {

    @Autowired
    private ProcessService processService;

    // 1. Add a process node for a device model
    @PostMapping("/add-node")
    public ProcessTemplate addNode(@RequestBody ProcessTemplate node) {
        return processService.createNode(node);
    }

    // 2. Connect nodes (Input: preId, nextId, modelId)
    // Postman Body Example: { "preId": 1, "nextId": 2, "modelId": 1 }
    @PostMapping("/connect")
    public String connect(@RequestBody Map<String, Long> params) {
        Long preId = params.get("preId");
        Long nextId = params.get("nextId");
        Long modelId = params.get("modelId");
        return processService.connectNodes(preId, nextId, modelId);
    }
    
    // 3. List all nodes for a model
    @GetMapping("/list/{modelId}")
    public List<ProcessTemplate> list(@PathVariable Long modelId) {
        return processService.getNodesByModel(modelId);
    }
}
