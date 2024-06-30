package com.eltvpp.system.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.eltvpp.common.core.utils.poi.ExcelUtil;
import com.eltvpp.common.core.web.controller.BaseController;
import com.eltvpp.common.core.web.domain.AjaxResult;
import com.eltvpp.common.core.web.page.TableDataInfo;
import com.eltvpp.common.log.enums.BusinessType;
import com.eltvpp.common.security.annotation.RequiresPermissions;
import com.eltvpp.common.log.annotation.Log;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.eltvpp.system.domain.MonitorDeviceVarMap;
import com.eltvpp.system.service.IMonitorDeviceVarMapService;

/**
 * 监控设备变量索引地图
 * 
 * @author JUNFU.WANG
 * @date 2023-10-07
 */
@Api(tags = "J 监控设备变量索引地图表")
@RestController
@RequestMapping("/var-map")
public class MonitorDeviceVarMapController extends BaseController
{
    @Autowired
    private IMonitorDeviceVarMapService monitorDeviceVarMapService;

    /**
     * 查询监控设备变量索引地图列表
     */
    @ApiOperation("查询监控设备变量索引地图列表")
    @RequiresPermissions("system:var-map:list")
    @GetMapping("/list")
    public TableDataInfo list(MonitorDeviceVarMap monitorDeviceVarMap)
    {
        startPage();
        List<MonitorDeviceVarMap> list = monitorDeviceVarMapService.selectMonitorDeviceVarMapList(monitorDeviceVarMap);
        return getDataTable(list);
    }

    /**
     * 查询监控设备变量索引地图列表（不分页）
     */
    @ApiOperation("查询监控设备变量索引地图列表（不分页）")
    @RequiresPermissions("system:var-map:list")
    @GetMapping("/listAll")
    public AjaxResult listAll(MonitorDeviceVarMap monitorDeviceVarMap) {
        List<MonitorDeviceVarMap> list = monitorDeviceVarMapService.selectMonitorDeviceVarMapList(monitorDeviceVarMap);
        return success(list);
    }

    /**
     * 导出监控设备变量索引地图列表
     */
    @ApiOperation("导出监控设备变量索引地图列表")
    @RequiresPermissions("system:var-map:export")
    @Log(title = "监控设备变量索引地图", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, MonitorDeviceVarMap monitorDeviceVarMap)
    {
        List<MonitorDeviceVarMap> list = monitorDeviceVarMapService.selectMonitorDeviceVarMapList(monitorDeviceVarMap);
        ExcelUtil<MonitorDeviceVarMap> util = new ExcelUtil<MonitorDeviceVarMap>(MonitorDeviceVarMap.class);
        util.exportExcel(response, list, "监控设备变量索引地图数据");
    }

    /**
     * 获取监控设备变量索引地图详细信息
     */
    @ApiOperation("获取监控设备变量索引地图详细信息")
    @RequiresPermissions("system:var-map:query")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(monitorDeviceVarMapService.selectMonitorDeviceVarMapById(id));
    }
}
