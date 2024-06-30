package com.eltvpp.system.controller;

import com.eltvpp.common.security.annotation.InnerAuth;
import com.eltvpp.system.api.domain.SysCommonCreate;
import com.eltvpp.system.service.ISysCommonCreateService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags = "C 参数配置表")
@RestController
@RequestMapping("/common-create")
public class SysCommonCreateController {

    @Autowired
    private ISysCommonCreateService commonCreateService;

    /**
     * 远程内部调用-查询生成记录列表
     * @param commonCreate 查询条件
     * @return 生成记录
     */
    @InnerAuth
    @PostMapping("/list/inner")
    public List<SysCommonCreate> selectDictDataByType(@RequestBody SysCommonCreate commonCreate){
        return commonCreateService.selectSysCommonCreateList(commonCreate);
    }
}
