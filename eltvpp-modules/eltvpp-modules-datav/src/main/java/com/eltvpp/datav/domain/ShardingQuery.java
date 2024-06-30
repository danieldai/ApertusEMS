package com.eltvpp.datav.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.eltvpp.common.core.annotation.Excel;
import com.eltvpp.common.core.web.domain.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.Map;

/**
 * 数据表查询条件
 *
 * @author: XIAOTONG.CAO
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ShardingQuery extends BaseEntity {
    /** 变量名称 */
    @ApiModelProperty("变量名称")
    @Excel(name = "变量名称")
    private String variableName;

    /** 存盘时间 */
    @ApiModelProperty("存盘时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "存盘时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date saveTime;

    /** 存盘时间 */
    @ApiModelProperty("存盘时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "存盘时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date minTime;

    /**
     * 日期天
     */
    @ApiModelProperty("日期天")
    @Excel(name = "日期天")
    private Integer daySign;

    /**
     * 数值类型（1实时 2平均值 3最小值 4最大值）
     * */
    @ApiModelProperty("数值类型（1实时 2平均值 3最小值 4最大值）")
    @Excel(name = "数值类型（1实时 2平均值 3最小值 4最大值）")
    private Integer changeType;

    @ApiModelProperty("日期维度（日 70 、月 80、年 90）")
    @Excel(name = "日期维度")
    private Integer dateDim;

    /**
     * 是否电费
     */
    @ApiModelProperty("是否电费")
    @Excel(name = "是否电费")
    private Boolean isCharge = false;

    public ShardingQuery(){

    }

    public ShardingQuery(String variableName,Integer storageType, Integer changeType, Map<String, Object> params,Integer dateDim){
        this.variableName = variableName;
        this.changeType = changeType;
        if(storageType == 2){
            this.changeType = 5;
        }
        this.setParams(params);
        this.dateDim = dateDim;
    }

    public ShardingQuery(String variableName,Integer storageType, Integer changeType, Map<String, Object> params){
        this.variableName = variableName;
        this.changeType = changeType;
        if(storageType == 2){
            this.changeType = 5;
        }
        this.setParams(params);
    }
}
