package com.eltvpp.system.api;

import com.eltvpp.system.api.domain.SysCommonCreate;
import com.eltvpp.system.api.factory.RemoteCommonCreateFallbackFactory;
import com.eltvpp.common.core.constant.SecurityConstants;
import com.eltvpp.common.core.constant.ServiceNameConstants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

/**
 * 数据表生成记录
 *
 * @author XIAOTONG.CAO
 */
@FeignClient(contextId = "remoteCommonCreateService", value = ServiceNameConstants.SYSTEM_SERVICE, fallbackFactory = RemoteCommonCreateFallbackFactory.class)
public interface RemoteCommonCreateService {

    /**
     * 查询生成记录
     * */
    @PostMapping("/common-create/list/inner")
    public List<SysCommonCreate> list(@RequestBody SysCommonCreate commonCreate, @RequestHeader(SecurityConstants.FROM_SOURCE) String source);
}
