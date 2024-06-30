package com.eltvpp.system.controller;

import com.eltvpp.common.core.web.controller.BaseController;
import com.eltvpp.common.security.annotation.InnerAuth;
import com.eltvpp.system.domain.vo.time.TimesVo;
import com.eltvpp.system.service.ISpecialDataProvideService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 大屏专用数据（集合）
 *
 * @Author: JUNFU.WANG
 * @Date: 2024/6/22 10:17
 */
@Api(tags = "D 大屏专用数据")
@RestController
@RequestMapping("/common-provide")
public class SpecialDataProvideController extends BaseController {

    @Autowired
    private ISpecialDataProvideService specialDataProvideService;

    /**
     * 通用>>获取数据接口
     */
    @ApiOperation("通用>>获取数据接口")
    @InnerAuth
    @GetMapping("/inner/tableList")
    public Object GetSpecialData(@RequestParam Map<String, Object> param) {
        Object dataType = param.get("dataType");
        Object tableType = param.get("tableType");
        Object headType = param.get("headType");
        Object staticType = param.get("staticType");

        int _dataType = dataType == null ? 1 : Integer.parseInt(dataType.toString());
        int _tableType = tableType == null ? 1 : Integer.parseInt(tableType.toString());
        int _headType = headType == null ? 1 : Integer.parseInt(headType.toString());
        int _staticType = staticType == null ? 1 : Integer.parseInt(staticType.toString());

        // 获取表格数据
        if (_dataType == 1) {
            Object dayTime = param.get("dayTime");
            Object monthTime = param.get("monthTime");
            Object yearTime = param.get("yearTime");

            TimesVo timesVo = new TimesVo();
            timesVo.setDayTime(dayTime == null ? null : dayTime.toString());
            timesVo.setMonthTime(monthTime == null ? null : monthTime.toString());
            timesVo.setYearTime(yearTime == null ? null : yearTime.toString());

            if (_tableType == 1) {
                return specialDataProvideService.getAlarmTriggerList(_headType, _staticType, timesVo);
            }
            if (_tableType == 2) {
                return specialDataProvideService.getDevopsOrderList(_headType, _staticType, timesVo);
            }
        }

        // 获取统计图表数据
        if (_dataType == 2) {
            Object dayTime = param.get("dayTime");
            Object monthTime = param.get("monthTime");
            Object yearTime = param.get("yearTime");

            TimesVo timesVo = new TimesVo();
            timesVo.setDayTime(dayTime == null ? null : dayTime.toString());
            timesVo.setMonthTime(monthTime == null ? null : monthTime.toString());
            timesVo.setYearTime(yearTime == null ? null : yearTime.toString());

            if (_tableType == 1) {
                return specialDataProvideService.getAlarmTriggerStatic(_staticType, timesVo);
            }
            if (_tableType == 2) {
                return specialDataProvideService.getDevopsOrderStatic(_staticType, timesVo);
            }
        }

        return null;
    }
}
