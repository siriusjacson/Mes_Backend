package com.mess.mes_backend.controller;

import com.mess.mes_backend.entity.ProcessNodeTpl;
import com.mess.mes_backend.service.ProcessService;
import com.mess.mes_backend.common.Result;
import com.mess.mes_backend.dto.NodeConnectReq;
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



    // 1. Add node
    @PostMapping("/add-node")
    public Result<ProcessNodeTpl> addNode(@RequestBody ProcessNodeTpl node) {
        return Result.success(processService.createNode(node));
    }



    // 2. Connect nodes
    @PostMapping("/connect")
    public Result<String> connect(@RequestBody NodeConnectReq req) {
        return Result.success(processService.connectNodes(req.getPreId(), req.getNextId(), req.getModelId()));
    }
    
    // 3. List nodes
    @GetMapping("/list/{modelId}")
    public Result<List<ProcessNodeTpl>> list(@PathVariable Long modelId) {
        return Result.success(processService.getNodesByModel(modelId));
    }
}
