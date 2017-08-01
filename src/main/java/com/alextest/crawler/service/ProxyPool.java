package com.alextest.crawler.service;

import com.alextest.crawler.vo.ProxyVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by alexdrum on 2017/7/27.
 */
@Slf4j
@Component
public class ProxyPool {

    private ConcurrentHashMap<Integer, ProxyVo> proxyVoMap = new ConcurrentHashMap<>();

    @PostConstruct
    private void initialization(){
        System.out.println("ProxyService已经初始化完毕！");
    }

    public ConcurrentHashMap<Integer, ProxyVo> getProxyVoMap() {
        return this.proxyVoMap;
    }

    /**
     * 获取一个代理地址
     */
    public ProxyVo getProxy() {
        for (int i = 0; i < 3; i++) {
            Integer[] keys = this.proxyVoMap.keySet().toArray(new Integer[0]);
            Random random = new Random();
            Integer randomKey = keys[random.nextInt(keys.length)];
            ProxyVo resultProxyVo = this.proxyVoMap.get(randomKey);
            if (resultProxyVo == null) {
                continue;
            }
            return resultProxyVo;
        }
        return null;
    }

    // 添加一批代理地址
    public void addBatchProxyVos(Map<Integer, ProxyVo> proxyVoMap) {
        this.proxyVoMap.putAll(proxyVoMap);
    }
}