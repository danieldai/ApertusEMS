package com.eltvpp.system.api;

import com.eltvpp.common.core.constant.SecurityConstants;
import com.eltvpp.common.core.constant.ServiceNameConstants;
import com.eltvpp.common.core.domain.R;
import com.eltvpp.system.api.domain.SysStation;
import com.eltvpp.system.api.factory.RemoteUserFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * @title: 公共方法
 * @author: JUNFU.WANG
 * @date: 2023-10-07 9:54
 * @description:
 */
@FeignClient(contextId = "remotePublicService", value = ServiceNameConstants.SYSTEM_SERVICE, fallbackFactory = RemoteUserFallbackFactory.class)
public interface RemotePublicService {
    /**
     * 保存用户权限范围内的第一个站点，并返回结果
     * 说明：@innerAuth+AOP 实现 eltvpp-api 远程接口
     *
     * @param source 请求来源
     * @return 结果
     */
    @GetMapping("/public/setUserFirstStation")
    public R<Boolean> setUserCurrentStation(@RequestHeader(SecurityConstants.FROM_SOURCE) String source);

    /**
     * 获取用户当前企业
     *
     * @param source 请求来源
     */
    @GetMapping("/public/getCurrentStationInfo/inner")
    public SysStation getCurrentStationInfo(@RequestHeader(SecurityConstants.FROM_SOURCE) String source);
}
