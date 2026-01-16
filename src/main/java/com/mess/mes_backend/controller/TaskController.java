package com.mess.mes_backend.controller;

import com.mess.mes_backend.entity.TaskInstance;
import com.mess.mes_backend.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/task")
@CrossOrigin
public class TaskController {

    @Autowired
    private TaskService taskService;

    // 1. List all tasks for a project
    @GetMapping("/list")
    public List<TaskInstance> listTasks(@RequestParam Long projectId) {
        return taskService.getTasksByProject(projectId);
    }

    // 2. Complete a task
    // Body: { "taskId": 1, "operatorId": 1001 }
    @PostMapping("/complete")
    public String completeTask(@RequestBody Map<String, Long> params) {
        Long taskId = params.get("taskId");
        Long operatorId = params.get("operatorId");
        taskService.completeTask(taskId, operatorId);
        return "Task Completed";
    }
}
