package com.raysmond.artirest.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;


public class TestListenReceiveMsg implements ApplicationListener<ApplicationStartedEvent>  {
    private Logger logger = LoggerFactory.getLogger(TestListenReceiveMsg.class);

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        SpringApplication app = event.getSpringApplication();
        app.setShowBanner(false);// 不显示banner信息
        logger.info("==MyApplicationStartedEventListener==");
    }
}
