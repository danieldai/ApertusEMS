package com.eltvpp.datav.utils;

import com.eltvpp.common.core.utils.FormatUtils;
import com.eltvpp.datav.domain.vo.ChartCardInfoVo;

import java.util.HashMap;
import java.util.Map;

/**
 * 电力单位准换
 * */
public class EnergyConverter {
    private static final Map<String, String[]> UNIT_MAP = new HashMap<>();

    static {
        UNIT_MAP.put("kw", new String[]{"MW", "GW"});
        UNIT_MAP.put("kwh", new String[]{"MWh", "GWh"});
        UNIT_MAP.put("kvar", new String[]{"MVar", "GVar"});
        UNIT_MAP.put("kvarh", new String[]{"MVarh", "GVarh"});
    }

    public static void convertEnergy(ChartCardInfoVo.SingleInfo singleInfo) {
        String unit = singleInfo.getUnit().toLowerCase();
        if (UNIT_MAP.containsKey(unit)) {
            updateSingleInfo(singleInfo, UNIT_MAP.get(unit));
        }
    }

    private static void updateSingleInfo(ChartCardInfoVo.SingleInfo singleInfo, String[] units) {
        float value = singleInfo.getSingleValue();
        if (value >= 1000000) {
            singleInfo.setSingleValue(FormatUtils.calculateRatioCeil(value, 1000000f));
            singleInfo.setUnit(units[1]);
        } else if (value >= 1000) {
            singleInfo.setSingleValue(FormatUtils.calculateRatioCeil(value, 1000f));
            singleInfo.setUnit(units[0]);
        }
    }
}
