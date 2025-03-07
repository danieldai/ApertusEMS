package com.eltvpp.system.mapper;

import java.util.List;
import com.eltvpp.system.api.domain.SysLogOper;

/**
 * 操作日志 数据层
 * 
 * @author eltvpp
 */
public interface SysLogOperMapper
{
    /**
     * 新增操作日志
     * 
     * @param operLog 操作日志对象
     */
    public int insertOperlog(SysLogOper operLog);

    /**
     * 查询系统操作日志集合
     * 
     * @param operLog 操作日志对象
     * @return 操作日志集合
     */
    public List<SysLogOper> selectOperLogList(SysLogOper operLog);

    /**
     * 批量删除系统操作日志
     * 
     * @param operIds 需要删除的操作日志ID
     * @return 结果
     */
    public int deleteOperLogByIds(Long[] operIds);

    /**
     * 查询操作日志详细
     * 
     * @param operId 操作ID
     * @return 操作日志对象
     */
    public SysLogOper selectOperLogById(Long operId);

    /**
     * 清空操作日志
     */
    public void cleanOperLog();
}
