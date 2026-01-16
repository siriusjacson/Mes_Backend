package com.mess.mes_backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("t_process_relation")
public class ProcessRelation {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long modelId;
    private Long preProcessId;
    private Long nextProcessId;
}
