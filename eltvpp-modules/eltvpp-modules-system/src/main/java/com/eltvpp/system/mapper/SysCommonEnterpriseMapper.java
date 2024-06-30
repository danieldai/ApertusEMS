package com.eltvpp.system.mapper;

import java.util.List;

import com.eltvpp.system.domain.SysCommonEnterprise;

/**
 * 企业信息Mapper接口
 *
 * @author JUNFU.WANG
 * @date 2023-09-27
 */
public interface SysCommonEnterpriseMapper
{
    /**
     * 查询企业信息
     *
     * @param id 企业信息主键
     * @return 企业信息
     */
    public SysCommonEnterprise selectSysCommonEnterpriseById(Long id);

    public SysCommonEnterprise selectSysCommonEnterpriseByEntSn(String entSn);

    /**
     * 查询企业信息列表
     *
     * @param sysCommonEnterprise 企业信息
     * @return 企业信息集合
     */
    public List<SysCommonEnterprise> selectSysCommonEnterpriseList(SysCommonEnterprise sysCommonEnterprise);

    /**
     * 获取"企业-站点"关联列表
     * @return 企业信息集合
     */
    public List<SysCommonEnterprise> selectEnterpriseStationList();

    /**
     * 根据ID查询所有子站点（正常状态）
     *
     * @param id 站点ID
     * @return 子站点数
     */
    public int selectNormalChildrenStationById(Long id);

    /**
     * 查询站点是否存在子节点
     *
     * @param id 站点ID
     * @return 结果
     */
    public int hasChildrenStationById(Long id);

    /**
     * 查询站点是否存在用户
     *
     * @param id 站点ID
     * @return 结果
     */
    public int checkStationExistUser(Long id);

    /**
     * 新增企业信息
     *
     * @param sysCommonEnterprise 企业信息
     * @return 结果
     */
    public int insertSysCommonEnterprise(SysCommonEnterprise sysCommonEnterprise);

    /**
     * 修改企业信息
     *
     * @param sysCommonEnterprise 企业信息
     * @return 结果
     */
    public int updateSysCommonEnterprise(SysCommonEnterprise sysCommonEnterprise);

    /**
     * 删除企业信息
     *
     * @param id 企业信息主键
     * @return 结果
     */
    public int deleteSysCommonEnterpriseById(Long id);

    /**
     * 批量删除企业信息
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSysCommonEnterpriseByIds(Long[] ids);
}
