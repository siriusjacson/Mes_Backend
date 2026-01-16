package com.mess.mes_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProjectCreateReq {
    @NotBlank(message = "项目编号不能为空")
    private String projectNo;
    
    @NotBlank(message = "设备序列号不能为空")
    private String deviceSn;
    
    @NotNull(message = "模型ID不能为空")
    private Long modelId;
}
