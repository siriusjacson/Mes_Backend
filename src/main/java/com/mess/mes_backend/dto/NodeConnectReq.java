package com.mess.mes_backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class NodeConnectReq {
    @NotNull(message = "前置节点ID不能为空")
    private Long preId;
    
    @NotNull(message = "后置节点ID不能为空")
    private Long nextId;
    
    @NotNull(message = "模型ID不能为空")
    private Long modelId;
}
