package com.mess.mes_backend.controller;

import com.mess.mes_backend.entity.TaskInstance;
import com.mess.mes_backend.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import com.mess.mes_backend.common.Result;
import com.mess.mes_backend.dto.TaskCompleteReq;
import org.springframework.validation.annotation.Validated;

@RestController
@RequestMapping("/api/task")
@CrossOrigin
public class TaskController {

    @Autowired
    private TaskService taskService;



// ...
    // 1. 列出项目的所有任务
    @GetMapping("/list")
    public Result<List<TaskInstance>> listTasks(@RequestParam Long projectId) {
        return Result.success(taskService.getTasksByProject(projectId));
    }



// ...
    // 2. 完成任务
    // Body: { "taskId": 1, "operatorId": 1001 }

    @PostMapping("/complete")
    public Result<String> completeTask(@RequestBody @Validated TaskCompleteReq req) {
        taskService.completeTask(req.getTaskId(), req.getOperatorId());
        return Result.success("任务完成");
    }
}
