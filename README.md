# springboot_elasticsearch

项目使用的是elasticsearch5.5.2

若安装插件请下载对应版本

此版本默认要求服务器2G内存，可以到config下修改

集群搭在linux上，镜像已上传至网盘：链接: https://pan.baidu.com/s/1TctmmChy7lSfXlyiL1vi6Q 密码: 1gyt
【感谢好友支持下载，指出问题】
【链接: https://pan.baidu.com/s/1jOjUnZFv2FMdcSG-UmZdYg 密码: 4ivi】【纯净镜像，想自己安装的可以下载此镜像】
主要功能

    1.可向elasticsearch动态添加索引，默认已添加pinyin分词器与手机号邮箱分词（elasticsearch默认数字不分词）

    2.可根据index与type与搜索内容搜索索引库中document

    3.实现根据拼音与汉字搜索人名，并且根据首字母排序

    4.手机号分词，输入手机尾号或者中间位数搜索联系人

linux说明

  
    账号密码 root Qq873759727
  
          xiaobo Qq873759728
          
    elasticsearch集群说明
  
    master
    
      目录 /usr/local/java/es_slave/elasticsearch-5.5.2
      
    节点
    
      目录  /user/local/java/es_slave/es_slave1
            /user/local/java/es_slave/es_slave2
    后台启动
    
      进入目录
      
      [xiaobo@localhost es_slave1]$ ./bin/elasticsearch -d
    
    停用
      [xiaobo@localhost es_slave1]$ lsof -i:9200
      COMMAND  PID   USER   FD   TYPE DEVICE SIZE/OFF NODE NAME
      java    7109 xiaobo  122u  IPv6  81256      0t0  TCP *:wap-wsp (LISTEN)
      [xiaobo@localhost es_slave1]$ kill -9 7109
    【注意：root用户无法启动，必须切换至xiaobo】
      
    elasticsearch head插件后台启动命令
       nohup grunt server &exit
          
        
