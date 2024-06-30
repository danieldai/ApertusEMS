<h1 align="center" style="margin: 30px 0 30px; font-weight: bold;">阿帕图能源管理系统</h1>
<h4 align="center">基于 Arco Design Pro/Vue3 和 Spring Boot/Spring Cloud & Alibaba 微服务架构</h4>


## 一、平台简介

阿帕图能源管理系统可用于园区、企业的电、水、气、流量、光伏等能源项目，利用边缘网关采集设备数据，通过分析为企业提供高效的能源管理手段，助力企业打造“低碳”、“零碳”、“负碳”园区。

## 二、开源说明
阿帕图开源能源管理系统全部开源，供个人与企业学习研究和交流使用。</br>
① 本项目采用 MIT License 开源协议。</br>
② 代码全部开源。</br>
③ 代码整洁、架构清晰，代码注释在40%以上。</br>
④ 本项目是在 RuoYi-Cloud 基础框架上开发而成。</br>

本系统采用前后端分离的模式</br>
前端采用Arco Design Pro（VUE版本）</br>
后端采用Spring Boot、Spring Cloud & Alibaba</br>
注册中心、配置中心选型Nacos=2.2.2，权限认证使用Redis</br>
流量控制框架选型Sentinel，分布式事务选型Seata</br>
数据库采用MYSQL >= 5 .7, JDK1.8, Maven >= 3.0

## 三、系统模块

~~~
com.eltvpp     
├── eltvpp-ui                         // 前端UI [80]
├── eltvpp-diagram-ui                 // 接线图UI
├── eltvpp-gateway                    // 网关模块 [8080]
├── eltvpp-auth                       // 认证中心 [9200]
├── eltvpp-api                        // 接口模块
│       └── eltvpp-api-system         // 系统接口
├── eltvpp-common                     // 通用模块
│       └── eltvpp-common-core        // 核心模块
│       └── eltvpp-common-datascope   // 权限范围
│       └── eltvpp-common-datasource  // 多数据源
│       └── eltvpp-common-log         // 日志记录
│       └── eltvpp-common-redis       // 缓存服务
│       └── eltvpp-common-seata       // 分布式事务
│       └── eltvpp-common-security    // 安全模块
│       └── eltvpp-common-sensitive   // 数据脱敏
│       └── eltvpp-common-swagger     // 系统接口
├── eltvpp-modules                    // 业务模块
│       └── eltvpp-system             // 系统模块 [9201]
│       └── eltvpp-job                // 定时任务 [9203]
│       └── eltvpp-datav              // 数据服务 [9204]
│       └── eltvpp-file               // 文件服务 [9300]
├── eltvpp-visual                     // 图形化管理模块
│       └── eltvpp-visual-monitor     // 监控中心 [9100]
├──pom.xml                            // 公共依赖
~~~

## 四、系统功能

1. 用户管理	配置用户，决定谁可以访问此系统
2. 角色管理	角色菜单权限分配、设置角色按机构进行数据范围权限划分
3. 菜单管理	配置系统菜单、操作权限、数据权限标识等
4. 部门管理	配置系统组织机构（公司、部门、小组）
5. 岗位管理	配置系统用户所属担任职务
6. 字典管理	对系统中经常使用的一些较为固定的数据进行维护
7. 日志管理	包括登录日志、操作日志、异常日志
8. 多租户 租户分为三级（企业-分组-站点），每个站点都可以单独配置自己的菜单和权限
9. 通道管理 配置DTU和RTU的接入方式和注册码
10. 通讯设备 配置DTU和RTU的协议信息
11. 设备管理 配置数据采集设备，如电能表、多功能表、传感器等
12. 变量管理 配置数据点位
13. 看板管理 自由化程度较高，可以自由调整布局和配置相关点位数据。

## 五、备注

##### （一）数据库使用JSON格式存储的字段
1. 工单管理：“协作人”、“抄送人”、“验证人”、“图片描述”、“现场图片”
2. 维保记录：“维保设备”、“维保人员”、“图片描述”
3. 报警类型：“报警方式”
4. 报警配置：“具体岗位或人员”
5. 报警管理：“具体岗位或人员”
6. 留言回复：“附件合集”
7. 电价标准：“偏差电价”
8. 电价配置：“时间段”
9. 监控设备：“关联设备”
10. 仪表盘配置：“页面配置”、“页面配置预览”
11. 仪表盘卡片配置：“卡片配置”、“卡片配置预览”
12. 报表配置：“表头配置”