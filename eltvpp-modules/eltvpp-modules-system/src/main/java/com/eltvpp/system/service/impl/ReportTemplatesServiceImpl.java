package com.eltvpp.system.service.impl;

import com.eltvpp.common.core.utils.DateUtils;
import com.eltvpp.common.security.utils.SecurityUtils;
import com.eltvpp.system.api.domain.ReportTemplates;
import com.eltvpp.system.mapper.ReportTemplatesMapper;
import com.eltvpp.system.service.IPublicService;
import com.eltvpp.system.service.IReportTemplatesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 报表模版Service业务层处理
 * 
 * @author eltvpp
 * @date 2024-06-06
 */
@Service
public class ReportTemplatesServiceImpl implements IReportTemplatesService 
{
    @Autowired
    private ReportTemplatesMapper reportTemplatesMapper;

    @Autowired
    private IPublicService publicService;

    /**
     * 查询报表模版
     * 
     * @param id 报表模版主键
     * @return 报表模版
     */
    @Override
    public ReportTemplates selectReportTemplatesById(Long id)
    {
        return reportTemplatesMapper.selectReportTemplatesById(id);
    }

    /**
     * 查询报表模版列表
     * 
     * @param reportTemplates 报表模版
     * @return 报表模版
     */
    @Override
    public List<ReportTemplates> selectReportTemplatesList(ReportTemplates reportTemplates)
    {
        reportTemplates.setDeptId(publicService.getCurrentStation());
        reportTemplates.setUserId(SecurityUtils.getUserId());
        reportTemplates.setDeleteFlag(0);
        return reportTemplatesMapper.selectReportTemplatesList(reportTemplates);
    }

    /**
     * 新增报表模版
     * 
     * @param reportTemplates 报表模版
     * @return 结果
     */
    @Override
    public int insertReportTemplates(ReportTemplates reportTemplates)
    {
        if (reportTemplates.getEntId() == null || reportTemplates.getEntId() <= 0) {
            reportTemplates.setEntId(publicService.getCurrentEnterprise());
        }
        if (reportTemplates.getDeptId() == null || reportTemplates.getDeptId() <= 0) {
            reportTemplates.setDeptId(publicService.getCurrentStation());
        }
        reportTemplates.setCreateBy(SecurityUtils.getNickName());
        reportTemplates.setCreateTime(DateUtils.getNowDate());
        if (reportTemplates.getStopFlag() == null) {
            reportTemplates.setStopFlag(0);
        }
        reportTemplates.setDeleteFlag(0);
        reportTemplates.setMultiLevelHeader(1);
        //设置创建人
        reportTemplates.setUserId(SecurityUtils.getUserId());
        reportTemplates.setSystemFlag(1);
        return reportTemplatesMapper.insertReportTemplates(reportTemplates);
    }

    /**
     * 修改报表模版
     * 
     * @param reportTemplates 报表模版
     * @return 结果
     */
    @Override
    public int updateReportTemplates(ReportTemplates reportTemplates)
    {
        reportTemplates.setUpdateBy(SecurityUtils.getNickName());
        reportTemplates.setUpdateTime(DateUtils.getNowDate());
        return reportTemplatesMapper.updateReportTemplates(reportTemplates);
    }

    /**
     * 修改报表模版状态
     *
     * @param id 报表模版主键
     * @param state 状态
     * @return 结果
     */
    @Override
    public int updateReportTemplatesState(Long id, Integer state) {
        ReportTemplates reportTemplates = new ReportTemplates();
        reportTemplates.setId(id);
        reportTemplates.setStopFlag(state);
        reportTemplates.setUpdateBy(SecurityUtils.getNickName());
        reportTemplates.setUpdateTime(DateUtils.getNowDate());
        return reportTemplatesMapper.updateReportTemplates(reportTemplates);
    }

    /**
     * 批量删除报表模版
     * 
     * @param ids 需要删除的报表模版主键
     * @return 结果
     */
    @Override
    public int deleteReportTemplatesByIds(Long[] ids)
    {
        return reportTemplatesMapper.deleteReportTemplatesByIds(ids);
    }

    /**
     * 删除报表模版信息
     * 
     * @param id 报表模版主键
     * @return 结果
     */
    @Override
    public int deleteReportTemplatesById(Long id)
    {
        return reportTemplatesMapper.deleteReportTemplatesById(id);
    }
}
