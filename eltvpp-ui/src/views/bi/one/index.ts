import {handleChartAcrossLineBarData, handleChartLineBarData, handleChartLineData} from "@/views/bi/charts";
import {ref} from "vue";

const getData = () => {
	const dataList = []
	for (let i = 0; i < 10; i++) {
		dataList.push({
			id: i,
			time: '2024/04/06 13:31:21',
			incident: '输入电压上限越界-223V',
			site: '山东银凯特产业园区',
			facility: '总配电柜',
			category: '类别',
			level: '等级',
			status: '未处理',
			maintain: '日常维护'
		})
	}
	return dataList
}
const lineBarSetting = {
	offsetStartColor: '#0e88f6',
	offsetEndColor: '#0e88f600',
	showBackground: false
}
const lineBarSetting2 = {
	offsetStartColor: 'rgba(17,218,202)',
	offsetEndColor: 'rgba(17, 218, 202, 0)',
	showBackground: true
}
const lineSetting = {
	offsetStartColor: '#fac61c',
	offsetEndColor: '#fac61c00',
	smooth: false
}
const lineSetting2 = {
	offsetStartColor: '#11daca',
	offsetEndColor: '#11daca00',
	smooth: true
}
export default {
	templateId: "20240529",
	background: {
		type: "image",
		url: "https:www.eltvpp.com/1.png"
	},
	header: {
		date: true,
		weather: true,
		network: true,
	},
	content: {
		stationNum: 12,
		deviceNum: 20,
		pointNum: 20,
		yearEmissionReduction: "过去1年减少碳排放：23 CO2吨 相当于植树239棵",
	},
	coreMain: {
		label: "总功率",
		value: "1238.99",
		unit: "总功率（kw）"
	},
	coreBackground: {
		type: "image",
		url: "https:www.eltvpp.com/1.png"
	},
	coreSubStat: [
		{
			label: "年发电",
			value: "1238.99",
			unit: "千瓦时"
		}, {
			label: "年储能",
			value: "1238.99",
			unit: "千瓦时"
		}, {
			label: "年充电",
			value: "1238.99",
			unit: "千瓦时"
		}
	],
	cardList: {
		leftTop: [
			{
				title: "实时总发电量",
				unit: "kw",
				key: "",
				data: handleChartLineBarData(lineBarSetting)
			}, {
				title: "实时总用电量",
				unit: "kWh",
				key: "",
				data: handleChartLineBarData(lineBarSetting2)
			},
			{
				title: "实时总储能量",
				unit: "kWh",
				key: "",
				data: handleChartAcrossLineBarData()
			}
		],
		leftBottom: [
			{
				title: "",
				unit: "",
				key: "",
				data: ""
			}, {
				title: "",
				unit: "",
				key: "",
				data: ""
			}
		],
		rightTop: [
			{
				title: "总排碳量统计",
				unit: "CO₂吨",
				key: "",
				data: handleChartLineBarData(lineBarSetting)
			}, {
				title: "实时电价信息",
				unit: "kWh/元",
				key: "",
				data: handleChartLineData(lineSetting)
			},
			{
				title: "实时碳价信息",
				unit: "CO₂吨/元",
				key: "",
				data: handleChartLineData(lineSetting2)
			}
		],
		rightBottom: [
			{
				title: "实时告警信息",
				unit: "",
				key: "",
				data: getData()
			},
			{
				title: "实时维护信息",
				unit: "",
				key: "",
				data: getData()
			}
		],
	}
}
