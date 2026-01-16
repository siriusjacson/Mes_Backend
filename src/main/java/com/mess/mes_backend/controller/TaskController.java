package com.mess.mes_backend.controller;

import com.mess.mes_backend.entity.TaskInstance;
import com.mess.mes_backend.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import com.mess.mes_backend.common.Result;
import com.mess.mes_backend.dto.TaskCompleteReq;

@RestController
@RequestMapping("/api/task")
@CrossOrigin
public class TaskController {

    @Autowired
    private TaskService taskService;



// ...
    // 1. List all tasks for a project
    @GetMapping("/list")
    public Result<List<TaskInstance>> listTasks(@RequestParam Long projectId) {
        return Result.success(taskService.getTasksByProject(projectId));
    }



// ...
    // 2. Complete a task
    // Body: { "taskId": 1, "operatorId": 1001 }
    @PostMapping("/complete")
    public Result<String> completeTask(@RequestBody TaskCompleteReq req) {
        taskService.completeTask(req.getTaskId(), req.getOperatorId());
        return Result.success("Task Completed");
    }
}
