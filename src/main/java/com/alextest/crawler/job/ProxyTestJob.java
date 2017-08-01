package com.alextest.crawler.job;

import com.alextest.common.AlexContextAware;
import com.alextest.crawler.service.ProxyPool;
import com.alextest.crawler.service.ProxyService;
import com.alextest.crawler.vo.ProxyVo;
import com.alextest.util.TestUtils;
import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by alexdrum on 2017/7/27.
 */
@Component
public class ProxyTestJob implements SimpleJob {

    @Override
    public void execute(ShardingContext context) {
        TestUtils.log("动态代理测试作业启动");
        ProxyService proxyService = (ProxyService) AlexContextAware.getBeanFromApplicationContext("proxyService");
        proxyService.testProxy();
    }
}