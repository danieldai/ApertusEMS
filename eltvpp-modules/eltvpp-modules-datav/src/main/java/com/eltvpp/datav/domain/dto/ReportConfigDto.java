package com.eltvpp.datav.domain.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 表格配置实体
 */
@Data
public class ReportConfigDto implements Serializable {
    /**
     * 设备ID
     */
    private Integer deviceId;

    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 变量ID
     */
    private String deviceVarId;

    /**
     * 变量名称
     */
    private String deviceVarName;

    /**
     * 子表
     */
    private List<ReportConfigDto> childList;
}
