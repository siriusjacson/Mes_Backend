package com.mess.mes_backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("mes_project_instance")
public class ProjectInstance {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String projectNo;
    private String deviceSn;
    private Long modelId;
    private Integer status; // 0:NotStarted 1:Running 2:Completed
}
