package com.eltvpp.system.domain.handler;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.extension.handlers.AbstractJsonTypeHandler;
import com.eltvpp.system.domain.jsonvo.VariableConfigVo;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.util.List;

/**
 * @title: 自定义类型处理器
 * @author: JUNFU.WANG
 * @date: 2023-11-24 9:51
 * @description:
 */
@MappedJdbcTypes(JdbcType.JAVA_OBJECT)
@MappedTypes({List.class})
public class VeriableConfigTypeHandler extends AbstractJsonTypeHandler<List<VariableConfigVo>> {
    @Override
    protected List<VariableConfigVo> parse(String json) {
        return JSON.parseArray(json, VariableConfigVo.class);
    }

    @Override
    protected String toJson(List<VariableConfigVo> obj) {
        return JSON.toJSONString(obj);
    }
}