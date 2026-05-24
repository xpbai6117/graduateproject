package com.xn.book;

import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

@MapperScan("com.xn.book.mapper")
@SpringBootApplication
@EnableAspectJAutoProxy
@EnableScheduling
public class AccountParentApplication {
    private static final Logger logger = LoggerFactory.getLogger(AccountParentApplication.class);

    public static void main(String[] args) {

        SpringApplication.run(AccountParentApplication.class, args);
        logger.info("服务启动成功");
    }

}
