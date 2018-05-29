# springboot_elasticsearch

项目使用的是elasticsearch5.5.2

若安装插件请下载对应版本

此版本默认要求服务器2G内存，可以到config下修改

集群搭在了虚拟机上，已上传至网盘：链接: https://pan.baidu.com/s/1jOjUnZFv2FMdcSG-UmZdYg 密码: 4ivi

主要功能

1.可向elasticsearch动态添加索引，默认已添加pinyin分词器与手机号邮箱分词（elasticsearch默认数字不分词）

2.可根据index与type与搜索内容搜索索引库中document

3.实现根据拼音与汉字搜索人名，并且根据首字母排序

4.手机号分词，输入手机尾号或者中间位数搜索联系人
