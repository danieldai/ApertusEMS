package com.eltvpp.auth.service;

import com.eltvpp.common.core.constant.SecurityConstants;
import com.eltvpp.system.api.RemotePublicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @title: 公共服务
 * @author: JUNFU.WANG
 * @date: 2023-10-07 10:09
 * @description:
 */
@Component
public class SysPublicService {
    @Autowired
    private RemotePublicService remotePublicService;

    /**
     * 保存用户权限范围内的第一个站点，并返回结果
     */
    public void setUserCurrentStation() {
        remotePublicService.setUserCurrentStation(SecurityConstants.INNER);
    }
}
