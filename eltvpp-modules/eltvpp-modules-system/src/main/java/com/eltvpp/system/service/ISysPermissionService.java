package com.eltvpp.system.service;

import java.util.Set;

import com.eltvpp.system.api.domain.SysUser;

/**
 * 权限信息 服务层
 * 
 * @author eltvpp
 */
public interface ISysPermissionService
{
    /**
     * 获取角色数据权限
     * 
     * @return 角色权限信息
     */
    public Set<String> getRolePermission(SysUser user);

    /**
     * 获取菜单数据权限
     * 
     * @return 菜单权限信息
     */
    public Set<String> getMenuPermission(SysUser user);
}
