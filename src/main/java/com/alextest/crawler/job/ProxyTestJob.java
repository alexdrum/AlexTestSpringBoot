package com.alextest.crawler.job;

import com.alextest.common.AlexContextAware;
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
        // 获取动态代理Map
        Map<Integer, ProxyVo> proxyVoMap = proxyService.getProxyVoMap();
        TestUtils.log("动态代理Map测试前共有：" + proxyVoMap.size() + "个代理IP；");
        List<ProxyVo> proxyVoList = new ArrayList(proxyVoMap.values());
        // 筛选不可用的代理IP
        List<ProxyVo> invalidProxyVoList = proxyVoList
                .stream()
                .parallel()
                .filter(proxyVo -> proxyVo.isValid() == false)
                .collect(Collectors.toList());
        // 从map中去除
        invalidProxyVoList.forEach(proxyVo -> proxyVoMap.remove(proxyVo.getHashCode()));
        TestUtils.log("已经去除：" + invalidProxyVoList.size() + "个代理IP；");
        TestUtils.log("去除不可用IP后Map共有：" + proxyVoMap.size() + "个代理IP；");
    }
}
