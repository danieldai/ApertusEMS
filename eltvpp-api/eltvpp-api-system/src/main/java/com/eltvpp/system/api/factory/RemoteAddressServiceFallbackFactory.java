package com.eltvpp.system.api.factory;

import com.eltvpp.system.api.RemoteAddressService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;

/**
 * 地址服务降级处理
 *
 * @author eltvpp
 */
public class RemoteAddressServiceFallbackFactory  implements FallbackFactory<RemoteAddressService> {
    private static final Logger log = LoggerFactory.getLogger(RemoteAddressServiceFallbackFactory.class);
    @Override
    public RemoteAddressService create(Throwable throwable) {
        log.error("地址服务调用失败:{}", throwable.getMessage());
        return new RemoteAddressService() {
            @Override
            public String getAddressName(Long provinceId, Long cityId, Long countyId, String source) {
                return null;
            }
        };
    }
}
