package me.robin.spring.cloud.utils.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.security.access.ConfigAttribute;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lubin.Xuan on 2017-09-26.
 * {desc}
 */
@Slf4j
public class MvcApplicationStartListener implements ApplicationListener<ApplicationReadyEvent> {

    private List<ConfigAttribute> securedResList = new ArrayList<>();

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        log.info("SECURED REST RESOURCE {}", securedResList);
    }

    public void addSecuredRes(List<ConfigAttribute> securedResList) {
        this.securedResList.addAll(securedResList);
    }
}
