package com.eltvpp.datav.domain.vo;

import com.eltvpp.datav.domain.vo.echarts.EchartsOption;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 卡片信息
 *
 * @author: XIAOTONG.CAO
 */
@Data
public class ChartCardInfoVo implements Serializable {


    /**
     * 卡片名称
     */
    private String cardName;

    /**
     * echarts图表数据
     */
    private EchartsOption echartsOption;

    /**
     * 单值
     */
    private SingleInfo singleInfo;

    /**
     * 状态值
     */
    private StatusInfo statusInfo;

    /**
     * 仪表盘信息
     */
    private DashboardInfo dashboardInfo;


    /**
     * 排行信息
     */
    private List<RankingInfo> rankingList;

    /**
     * 桑基图信息
     * */
    private SankeyInfo sankeyInfo;

    /**
     * 通用表格
     * */
    private Object tableInfo;

    /**
     * 电价信息
     * */
    private Map<String, List<Object>> schemeMap;


    /**
     * 右侧-仪表盘信息
     */
    private List<DashboardInfo> dashboardRightList;

    /**
     * 右侧-单值
     */
    private List<SingleInfo> singleRightList;

    /**
     * 状态值
     */
    private List<StatusInfo> statusRightList;

    /**
     * echarts图表数据
     */
    private List<EchartsOption> echartsOptionList;

    /**
     * 单值信息
     */
    @Data
    public static class SingleInfo implements Serializable {

        private String varName;

        /**
         * 变量编码
         */
        private String varSn;

        /**
         * 图标
         */
        private String icon;

        /**
         * 图标颜色
         */
        private String iconColor;

        /**
         * 背景颜色
         */
        private String backgroundColor;

        /**
         * 单值
         */
        private Float SingleValue;

        /**
         * 辅助指标-环比
         */
        private Boolean chain;

        /**
         * 环比-值
         */
        private Float chainValue;

        /**
         * 环比-值 比率
         */
        private Float chainRatio;

        /**
         * 辅助指标-同比
         */
        private Boolean yoy;

        /**
         * 同比-值
         */
        private Float yoyValue;

        /**
         * 同比-值 比率
         */
        private Float yoyRatio;

        /**
         * 单位
         */
        private String unit;

        public SingleInfo() {

        }

        public SingleInfo(String varName,String varSn, String unit, String icon, String iconColor, String backgroundColor) {
            this.varName = varName;
            this.varSn = varSn;
            this.unit = unit;
            this.icon = icon;
            this.iconColor = iconColor;
            this.backgroundColor = backgroundColor;
            this.SingleValue = 0F;
            this.chain = false;
            this.chainValue = 0F;
            this.chainRatio = 0F;
            this.yoy = false;
            this.yoyValue = 0F;
            this.yoyRatio = 0F;
        }
    }

    /**
     * 状态
     */
    @Data
    public static class StatusInfo implements Serializable {
        /**
         * 变量
         */
        private String icon;

        /**
         * 状态 true 在线 false 离线
         */
        private Boolean status;

        /**
         * 变量编码
         */
        private String varSn;

        public StatusInfo() {
            this.status = false;
        }
    }

    /**
     * 仪表盘、水滴图
     */
    @Data
    public static class DashboardInfo implements Serializable {

        /**
         * 名称
         */
        private String name;

        /**
         * 值
         */
        private Float value;

        /**
         * 最大值
         */
        private Float minValue;

        /**
         * 最小值
         */
        private Float maxValue;

        /**
         * 单位
         */
        private String unit;

        /**
         * 变量
         */
        private String varSn;

        public DashboardInfo() {

        }

        public DashboardInfo(String name, String unit, String varSn) {
            this.name = name;
            this.unit = unit;
            this.maxValue = 0F;
            this.minValue = 0F;
            this.value = 0F;
            this.varSn = varSn;
        }
    }

    /**
     * 排行
     */
    @Data
    public static class RankingInfo implements Serializable {

        /**
         * 排名
         */
        private Integer index;

        /**
         * 名称
         */
        private String name;

        /**
         * 单位
         */
        private String unit;

        /**
         * 值
         */
        private Float value;

        /**
         * 比值
         */
        private Float ratio;

        public RankingInfo() {

        }

        /**
         * @param name 名称
         * @param unit 单位
         * @param value 值
         * */
        public RankingInfo(String name, String unit,Float value) {
            this.name = name;
            this.unit = unit;
            this.value = value;
            this.ratio = 0f;
        }
    }

    /**
     * 桑基图
     * */
    @Data
    public static class SankeyInfo implements Serializable{

        /**
         * 名字  结构简单{name:xxx}
         * */
        private Set<SankeyNameInfo> data;

        /**
         * 关联关系
         * */
        private List<SankeyLinksInfo> links;
    }

    @Data
    public static class SankeyNameInfo implements Serializable{
        private String name;
        public SankeyNameInfo(String name){
            this.name = name;
        }
    }

    @Data
    public static class SankeyLinksInfo implements Serializable{

        /**
         * 边的源节点名称
         * */
        private String source;
        /**
         * 边的目标节点名称
         * */
        private String target;

        /**
         * 边的数值，决定边的宽度。
         * */
        private Float value;

        public SankeyLinksInfo(){

        }

        public SankeyLinksInfo(String source,String target,Float value){
            this.source = source;
            this.target= target;
            this.value =  value;
        }
    }
}
