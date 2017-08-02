package com.alextest.crawler.job;

import com.alextest.common.AlexContextAware;
import com.alextest.crawler.service.ProxyService;
import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Created by alexdrum on 2017/7/27.
 */
@Slf4j
@Component
public class ProxyTestJob implements SimpleJob {

    @Override
    public void execute(ShardingContext context) {
        log.info("动态代理测试作业启动");
        ProxyService proxyService = (ProxyService) AlexContextAware.getBeanFromApplicationContext("proxyService");
        proxyService.testProxy();
    }
}