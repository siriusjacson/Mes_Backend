package com.mess.mes_backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import com.mess.mes_backend.common.enums.TaskStatus;
import java.time.LocalDateTime;

@Data
@TableName("mes_task_instance")
public class TaskInstance {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long projectId;
    private Long nodeTplId;
    private String taskName;
    private TaskStatus status; // 0:Locked 1:Pending 2:Running 3:Completed
    private Long operatorId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
