const newChartData = () => {
	return {
		grid: {
			top: 0,
			left: 20,
			right: 0,
			bottom: '2%',
			containLabel: true,
		},
		backgroundColor: "rgba(0, 0, 0, 0)",
		xAxis: {
			type: "category",
			data: [
				0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19,
				20, 21, 22, 23
			],
			axisTick: {
				show: false,
			},
			axisLine: {
				show: false,
			},
			axisLabel: {
				color: "#ffffff",
				fontSize: 6,
				fontWeight: 'bold'
			}
		},
		yAxis: {
			axisLine: {
				show: false,
			},
			axisTick: {
				show: false,
			},
			axisLabel: {
				show: false,
			},
			splitLine: {
				show: false
			}
		},
		series: [
			{
				data: [50, 30, 30, 30, 40, 60, 70, 80, 90, 110, 110, 95, 105, 105, 95, 75, 80, 85, 90, 65, 45, 30, 30, 20],
				type: "line",
				smooth: true,
				symbol: 'circle', // 设置交汇点为实心圆
				showSymbol: false,
				lineStyle: {
					color: 'rgba(245, 108, 108, 1)'
				},
				areaStyle: {
					color: 'rgba(245, 108, 108, 1)'
				}
			},
		],
	}
}
const newZhuChartData = () => {
	return  {
		xAxis: {
			type: 'category',
			data: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun']
		},
		yAxis: {
			type: 'value'
		},
		series: [
			{
				data: [
					120,
					{
						value: 200,
						itemStyle: {
							color: '#a90000'
						}
					},
					150,
					80,
					70,
					110,
					130
				],
				type: 'bar'
			}
		]
	};
}
export {
	newChartData,
	newZhuChartData
}
