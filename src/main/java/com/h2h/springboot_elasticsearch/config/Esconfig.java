package com.h2h.springboot_elasticsearch.config;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

@Configuration
public class Esconfig {

    @Bean
    public TransportClient client() throws UnknownHostException{
//        InetSocketTransportAddress node = new InetSocketTransportAddress(
//                InetAddress.getByName("192.168.218.128"),
//                9200
//        );
//        Settings settings = Settings.builder().put(Settings.EMPTY).put("client.transport.ignore_cluster_name",true).build();
//
//        TransportClient client = new PreBuiltTransportClient(Settings.EMPTY).addTransportAddress(node);
        // 创建客户端, 使用的默认集群名, "elasticSearch"
//        client = TransportClient.builder().build()
//                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("www.wenbronk.com"), 9300));

        // 通过setting对象指定集群配置信息, 配置的集群名
        Settings settings = Settings.builder().put("cluster.name", "xiaobo") // 设置集群名
//                .put("client.transport.sniff", true) // 开启嗅探 , 开启后会一直连接不上, 原因未知
//                .put("network.host", "192.168.50.37")
                .put("client.transport.ignore_cluster_name", false) // 忽略集群名字验证, 打开后集群名字不对也能连接上
//                .put("client.transport.nodes_sampler_interval", 5) //报错,
//                .put("client.transport.ping_timeout", 5) // 报错, ping等待时间,
                .build();
        TransportClient client = new PreBuiltTransportClient(settings)
                .addTransportAddress(new InetSocketTransportAddress(new InetSocketAddress("192.168.218.128", 9300)));
        // 默认5s
        // 多久打开连接, 默认5s
        System.out.println("success connect");
        return client;
    }
}
