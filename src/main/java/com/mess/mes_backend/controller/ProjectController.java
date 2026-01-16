package com.mess.mes_backend.controller;

import com.mess.mes_backend.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import com.mess.mes_backend.dto.ProjectCreateReq;

@RestController
@RequestMapping("/api/project")
@CrossOrigin
public class ProjectController {

    @Autowired
    private ProjectService projectService;



    // Create Project
    @PostMapping("/create")
    public com.mess.mes_backend.common.Result<Long> createProject(@RequestBody ProjectCreateReq req) {
        return com.mess.mes_backend.common.Result.success(projectService.createProject(req.getProjectNo(), req.getDeviceSn(), req.getModelId()));
    }
}
