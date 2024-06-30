package com.eltvpp.datav.controller;

import com.alibaba.fastjson.JSON;
import com.eltvpp.datav.domain.dto.ChartConfigDto;
import com.eltvpp.datav.domain.vo.ChartQueryVo;
import com.eltvpp.datav.enums.DatavDateDimSecondEnum;
import com.eltvpp.datav.enums.SysChartTypeEnum;
import com.eltvpp.datav.service.CommonChartService;
import com.eltvpp.common.core.constant.SecurityConstants;
import com.eltvpp.common.core.utils.DateUtils;
import com.eltvpp.common.core.utils.StringUtils;
import com.eltvpp.common.core.web.controller.BaseController;
import com.eltvpp.common.core.web.domain.AjaxResult;
import com.eltvpp.system.api.RemoteDashboardConfigCardService;
import com.eltvpp.system.api.domain.DashboardConfigCard;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 通用图表接口
 * @author XIAOTONG.CAO
 * @date: 2024-06-12 10:00
 */
@Api(tags = ">>> 通用图表接口 <<<")
@RestController
@RequestMapping("/common/chart")
public class CommonChartController extends BaseController {

    @Autowired(required = false)
    private RemoteDashboardConfigCardService remoteDashboardConfigCardService;

    @Autowired
    private CommonChartService commonChartService;

    /**
     * 获取>> 通用图表数据
     * @param vo 查询参数
     * */
    @ApiOperation("获取>>通用图表数据")
    @GetMapping("/getChart")
    public AjaxResult getChartData(@Validated ChartQueryVo vo) {
        //获取配置数据
        DashboardConfigCard config = remoteDashboardConfigCardService.getInfo(vo.getConfigId(),vo.getCardKey(), SecurityConstants.INNER);
        if(StringUtils.isNull(config)){
            return error("图表配置错误");
        }
        //通用变量需要前端传入设备编码
        //指定变量
        if(!SysChartTypeEnum.SpecialVarList.contains(config.getChartType()) && config.getIndicator() == 2 && StringUtils.isEmpty(vo.getDeviceSn())){
            return error("请选择设备");
        }
        if(StringUtils.isNull(config.getIsFullDate())){
            config.setIsFullDate(0);
        }
        if(config.getSecondDateDim() == null || config.getSecondDateDim()<=0){
            config.setSecondDateDim(DatavDateDimSecondEnum.HOUR_1.getKey());
        }
        vo.setDashboardConfigCard(config);
        vo.setChartConfig(JSON.parseObject(config.getCardConfig(), ChartConfigDto.class));
        //如果时间都为空 则默认取今日的数据
        if(StringUtils.isEmpty(vo.getDayTime()) && StringUtils.isEmpty(vo.getMonthTime()) && StringUtils.isEmpty(vo.getYearTime())){
            vo.setDayTime(DateUtils.getDate());
        }
        //单值图表默认是全日期
        if(SysChartTypeEnum.SINGLE_VALUE.getKey().equals(config.getChartType())){
            config.setIsFullDate(1);
        }
        //处理图表配置
        return success(commonChartService.handleChartData(vo));
    }
}
