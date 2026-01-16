package com.mess.mes_backend.controller;

import com.mess.mes_backend.entity.DeviceModel;
import com.mess.mes_backend.mapper.DeviceModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/model")
@CrossOrigin
public class DeviceModelController {

    @Autowired
    private DeviceModelMapper deviceModelMapper;

    @GetMapping("/list")
    public List<DeviceModel> list() {
        return deviceModelMapper.selectList(null);
    }

    @PostMapping("/add")
    public String add(@RequestBody DeviceModel model) {
        deviceModelMapper.insert(model);
        return "新增成功！ID: " + model.getId();
    }
}
