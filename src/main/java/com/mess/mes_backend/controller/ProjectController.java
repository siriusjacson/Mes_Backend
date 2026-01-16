package com.mess.mes_backend.controller;

import com.mess.mes_backend.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import com.mess.mes_backend.dto.ProjectCreateReq;
import org.springframework.validation.annotation.Validated;

@RestController
@RequestMapping("/api/project")
@CrossOrigin
public class ProjectController {

    @Autowired
    private ProjectService projectService;





    // 创建项目
    @PostMapping("/create")
    public com.mess.mes_backend.common.Result<Long> createProject(@RequestBody @Validated ProjectCreateReq req) {
        return com.mess.mes_backend.common.Result.success(projectService.createProject(req.getProjectNo(), req.getDeviceSn(), req.getModelId()));
    }
}
