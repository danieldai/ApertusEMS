package com.eltvpp.system.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.eltvpp.common.core.constant.UserConstants;
import com.eltvpp.common.core.utils.ChineseToPinyinUtils;
import com.eltvpp.common.core.utils.GenRandomUtils;
import com.eltvpp.system.service.ISysDeptService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.eltvpp.common.log.annotation.Log;
import com.eltvpp.common.log.enums.BusinessType;
import com.eltvpp.common.security.annotation.RequiresPermissions;
import com.eltvpp.system.domain.SysCommonEnterprise;
import com.eltvpp.system.service.ISysCommonEnterpriseService;
import com.eltvpp.common.core.web.controller.BaseController;
import com.eltvpp.common.core.web.domain.AjaxResult;
import com.eltvpp.common.core.utils.poi.ExcelUtil;
import com.eltvpp.common.core.web.page.TableDataInfo;

/**
 * 企业信息
 *
 * @author JUNFU.WANG
 * @date 2023-09-28
 */
@Api(tags = "Q 企业信息表")
@RestController
@RequestMapping("/enterprise")
public class SysCommonEnterpriseController extends BaseController {
    @Autowired
    private ISysCommonEnterpriseService sysCommonEnterpriseService;

    @Autowired
    private ISysDeptService deptService;

    /**
     * 查询企业信息列表
     */
    @ApiOperation("查询企业信息列表")
    @RequiresPermissions("system:enterprise:list")
    @GetMapping("/list")
    public TableDataInfo list(SysCommonEnterprise sysCommonEnterprise) {
        startPage();
        List<SysCommonEnterprise> list = sysCommonEnterpriseService.selectSysCommonEnterpriseList(sysCommonEnterprise);
        return getDataTable(list);
    }

    /**
     * 查询企业信息列表（不分页）
     */
    @ApiOperation("查询企业信息列表（不分页）")
    @RequiresPermissions("system:enterprise:list")
    @GetMapping("/listAll")
    public AjaxResult listAll(SysCommonEnterprise sysCommonEnterprise) {
        List<SysCommonEnterprise> list = sysCommonEnterpriseService.selectSysCommonEnterpriseList(sysCommonEnterprise);
        return success(list);
    }

    /**
     * 导出企业信息列表
     */
    @ApiOperation("导出企业信息列表")
    @RequiresPermissions("system:enterprise:export")
    @Log(title = "企业信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysCommonEnterprise sysCommonEnterprise) {
        List<SysCommonEnterprise> list = sysCommonEnterpriseService.selectSysCommonEnterpriseList(sysCommonEnterprise);
        ExcelUtil<SysCommonEnterprise> util = new ExcelUtil<SysCommonEnterprise>(SysCommonEnterprise.class);
        util.exportExcel(response, list, "企业信息数据");
    }

    /**
     * 获取企业信息详细信息
     */
    @ApiOperation("获取企业信息详细信息")
    @RequiresPermissions("system:enterprise:query")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(sysCommonEnterpriseService.selectSysCommonEnterpriseById(id));
    }

    /**
     * 新增企业信息
     */
    @ApiOperation("新增企业信息")
    @RequiresPermissions("system:enterprise:add")
    @Log(title = "企业信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SysCommonEnterprise sysCommonEnterprise) {
        // 设置默认值
        sysCommonEnterprise.setEntSn(ChineseToPinyinUtils.ToFirstChar(sysCommonEnterprise.getEntName()));

        // 查询是否已经存在当前企业
        SysCommonEnterprise exist = sysCommonEnterpriseService.selectSysCommonEnterpriseByEntSn(sysCommonEnterprise.getEntSn());
        if (exist != null) {
            return error("该企业名称已经存在！");
        }

        int rows = sysCommonEnterpriseService.insertSysCommonEnterprise(sysCommonEnterprise);
        if (rows > 0) {
            deptService.autoGenDeptSync();
        }
        return toAjax(rows);
    }

    /**
     * 修改企业信息
     */
    @ApiOperation("修改企业信息")
    @RequiresPermissions("system:enterprise:edit")
    @Log(title = "企业信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SysCommonEnterprise sysCommonEnterprise) {
        if (UserConstants.DEPT_DISABLE.equals(sysCommonEnterprise.getStopFlag()) && sysCommonEnterpriseService.selectNormalChildrenStationById(sysCommonEnterprise.getId()) > 0) {
            return error("该站点包含未停用的站点！");
        }

        int rows = sysCommonEnterpriseService.updateSysCommonEnterprise(sysCommonEnterprise);
        if (rows > 0) {
            deptService.autoGenDeptSync();
        }
        return toAjax(rows);
    }

    /**
     * 修改企业信息状态
     */
    @ApiOperation("修改企业信息状态")
    @RequiresPermissions("system:enterprise:state")
    @Log(title = "企业信息", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus/{id}/{state}")
    public AjaxResult changeStatus(@PathVariable Long id, @PathVariable Integer state) {
        return toAjax(sysCommonEnterpriseService.updateSysCommonEnterpriseState(id, state));
    }

    /**
     * 删除企业信息
     */
    @ApiOperation("删除企业信息")
    @RequiresPermissions("system:enterprise:remove")
    @Log(title = "企业信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public AjaxResult remove(@PathVariable Long id) {
        if (sysCommonEnterpriseService.hasChildrenStationById(id)) {
            return warn("该企业下存在站点，不允许删除");
        }
        if (sysCommonEnterpriseService.checkStationExistUser(id)) {
            return warn("该企业下存在用户，不允许删除");
        }

        int rows = sysCommonEnterpriseService.deleteSysCommonEnterpriseById(id);
        if (rows > 0) {
            deptService.autoGenDeptSync();
        }
        return toAjax(rows);
    }
}
