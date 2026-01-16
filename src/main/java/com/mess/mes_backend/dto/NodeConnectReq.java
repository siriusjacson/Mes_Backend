package com.mess.mes_backend.dto;

import lombok.Data;

@Data
public class NodeConnectReq {
    private Long preId;
    private Long nextId;
    private Long modelId;
}
