package com.eltvpp.system.api;

import com.eltvpp.common.core.constant.SecurityConstants;
import com.eltvpp.common.core.constant.ServiceNameConstants;
import com.eltvpp.system.api.factory.RemoteElectricPriceSchemeConfigFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

/**
 * 电度电价配置
 *
 * @author XIAOTONG.CAO
 */
@FeignClient(contextId = "remoteElectricPriceSchemeConfigService", value = ServiceNameConstants.SYSTEM_SERVICE, fallbackFactory = RemoteElectricPriceSchemeConfigFallbackFactory.class)

public interface RemoteElectricPriceSchemeConfigService {
    /**
     * 获取站点配置的尖峰平谷时间段
     */
    @GetMapping("/price-scheme-config/getSeasonalRangeList/innerAuth")
    public Map<String, List<Object>> getSeasonalRangeList(@RequestParam("schemeId") Long schemeId, @RequestHeader(SecurityConstants.FROM_SOURCE) String source);
}
