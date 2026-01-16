package com.mess.mes_backend.controller;

import com.mess.mes_backend.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/project")
@CrossOrigin
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    // Create Project
    @PostMapping("/create")
    public com.mess.mes_backend.common.Result<Long> createProject(@RequestBody Map<String, Object> params) {
        String projectNo = (String) params.get("projectNo");
        String deviceSn = (String) params.get("deviceSn");
        Long modelId = ((Number) params.get("modelId")).longValue();
        
        return com.mess.mes_backend.common.Result.success(projectService.createProject(projectNo, deviceSn, modelId));
    }
}
