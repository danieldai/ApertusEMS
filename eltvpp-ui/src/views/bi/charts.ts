import * as echarts from 'echarts'
// 折线柱状图
const handleChartLineBarData = (lineSetting:any)=> {
	const option = {
		tooltip: {
			trigger: 'axis',
			axisPointer: {
				type: 'shadow'
			}
		},
		grid: {
			top: 16,
			left: 40,
			right: 10,
			bottom: 24
		},
		xAxis: [{
			type: 'category',
			data: ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月'],
			axisLine: {
				lineStyle: {
					color: 'rgba(255,255,255)'
				}
			},
			axisTick: {
				show: false
			},
			axisLabel: {
				color: '#FFFFFF'
			},
		}],
		yAxis: [{
			axisLine: {
				show: false
			},
			axisLabel: {
				color: '#FFFFFF'
			},
			splitLine: {
				lineStyle: {
					color: 'rgba(255,255,255,0.4)'
				}
			}
		}],
		series: [{
			type: 'bar',
			data: [22, 35, 55, 66, 44, 88, 11, 23, 55, 44, 66, 100],
			barWidth: '10px',
			// 是否开始每一个柱状的背景色
			showBackground: lineSetting.showBackground || false,
			backgroundStyle: {
				color: 'rgba(17, 218, 202, 0.1)',
				borderRadius: 50
			},
			itemStyle: {
				color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{
					offset: 0,
					color: lineSetting.offsetStartColor // 0% 处的颜色
				}, {
					offset: 1,
					color: lineSetting.offsetEndColor // 100% 处的颜色
				}], false),
				borderRadius: [20, 20, 0, 0]
			}
		}]
	}
	return option
}
// 横向折线柱状图
const handleChartAcrossLineBarData = ()=> {
	const option = {
		tooltip: {
			trigger: 'axis',
			axisPointer: {
				type: 'shadow'
			}
		},
		grid: {
			top: 16,
			left: 40,
			right: 20,
			bottom: 24
		},
		xAxis: {
			type: 'value',
			boundaryGap: [0, 0.01],
			axisLabel: {
				color: '#FFFFFF'
			},
		},
		yAxis: {
			type: 'category',
			data: ['1月', '2月', '3月', '4月', '5月', '6月'],
			axisTick: {
				show: false
			},
			axisLabel: {
				color: '#FFFFFF'
			},
		},
		series: [
			{
				type: 'bar',
				data: [22, 35, 55, 66, 44, 88],
				barWidth: '10px',
				// 是否开始每一个柱状的背景色
				showBackground: false,
				backgroundStyle: {
					color: 'rgba(17, 218, 202, 0.1)',
					borderRadius: 50
				},
				itemStyle: {
					color: new echarts.graphic.LinearGradient(1, 0, 0, 1, [{
						offset: 0,
						color: 'rgba(250,198,28)' // 0% 处的颜色
					}, {
						offset: 1,
						color: 'rgba(250, 198, 28, 0)' // 100% 处的颜色
					}], false),
					borderRadius: [0, 20, 20, 0]
				}
			}
		]
	}
	return option
}
// 折线图
const handleChartLineData = (lineSetting:any) => {
	const option = {
		tooltip: {
			trigger: 'axis'
		},
		grid: {
			top: 16,
			left: 40,
			right: 10,
			bottom: 24
		},
		xAxis: {
			type: 'category',
			data: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'],
			boundaryGap: false,
			axisLabel: {
				color: '#FFFFFF'
			},
			axisTick: {
				show: false
			},
		},
		yAxis: {
			type: 'value',
			axisLabel: {
				color: '#FFFFFF'
			},
		},
		series: [
			{
				data: [320, 132, 501, 234, 390, 530, 120],
				type: 'line',
				smooth: lineSetting.smooth, // 平滑曲线显示
				symbol: "circle", //标记的图形为实心圆
				symbolSize: 6, //标记的大小
				showSymbol: !lineSetting.smooth,
				itemStyle: {
					color: lineSetting.offsetStartColor, //改变折线点的颜色
				},
				areaStyle: {
					color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{
							offset: 0, color: lineSetting.offsetStartColor // 0% 处的颜色
						}, {
							offset: 1, color: lineSetting.offsetEndColor // 100% 处的颜色
						}]
					)  //背景渐变色
				},
			}
		]
	}
	return option
}
export {
	handleChartLineBarData,
	handleChartAcrossLineBarData,
	handleChartLineData
}
