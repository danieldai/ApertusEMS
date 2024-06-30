package com.eltvpp.system.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import com.eltvpp.common.core.constant.SecurityConstants;
import com.eltvpp.common.core.constant.ServiceNameConstants;
import com.eltvpp.common.core.domain.R;
import com.eltvpp.system.api.domain.SysLogLogin;
import com.eltvpp.system.api.domain.SysLogOper;
import com.eltvpp.system.api.factory.RemoteLogFallbackFactory;

/**
 * 日志服务
 * 
 * @author eltvpp
 */
@FeignClient(contextId = "remoteLogService", value = ServiceNameConstants.SYSTEM_SERVICE, fallbackFactory = RemoteLogFallbackFactory.class)
public interface RemoteLogService
{
    /**
     * 保存系统日志
     *
     * @param sysLogOper 日志实体
     * @param source 请求来源
     * @return 结果
     */
    @PostMapping("/operlog")
    public R<Boolean> saveLog(@RequestBody SysLogOper sysLogOper, @RequestHeader(SecurityConstants.FROM_SOURCE) String source) throws Exception;

    /**
     * 保存访问记录
     *
     * @param sysLogLogin 访问实体
     * @param source 请求来源
     * @return 结果
     */
    @PostMapping("/logininfor")
    public R<Boolean> saveLogininfor(@RequestBody SysLogLogin sysLogLogin, @RequestHeader(SecurityConstants.FROM_SOURCE) String source);
}
