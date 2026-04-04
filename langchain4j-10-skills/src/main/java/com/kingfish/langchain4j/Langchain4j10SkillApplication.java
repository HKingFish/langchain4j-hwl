package com.kingfish.langchain4j;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @Author : haowl
 * @Date : 2026/3/16 21:02
 * @Desc :
 */
@SpringBootApplication
public class Langchain4j10SkillApplication {
    public static void main(String[] args) {
        // 指定使用 Spring RestClient 作为 HTTP 客户端，避免 classpath 中多实现冲突
        System.setProperty("langchain4j.http.clientBuilderFactory",
                "dev.langchain4j.http.client.spring.restclient.SpringRestClientBuilderFactory");
        SpringApplication.run(Langchain4j10SkillApplication.class, args);
    }
}