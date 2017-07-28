package com.alextest.crawler.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;

import static com.alextest.crawler.CrawlerConst.TIAN_YAN_CHA_TEST;

/**
 * 用于存放动态代理IP的vo
 * Created by alexdrum on 2017/7/27.
 */
@Getter
@Setter
@ToString
public class ProxyVo {
    private String ip;
    private int port;

    // 获得代理IP的hashCode
    public Integer getHashCode() {
        if (StringUtils.isBlank(ip) || port <= 0) {
            return -1;
        }
        String proxyString = ip + ":" + String.valueOf(port);
        return proxyString.hashCode();
    }

    // 测试代理IP是否可用
    public boolean isValid(){

        try {
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("124.206.107.125", 3128));
            URL url = new URL(TIAN_YAN_CHA_TEST);
            HttpURLConnection uc = (HttpURLConnection) url.openConnection(proxy);
            uc.connect();
        }catch (Exception e){
            return false;
        }
        return true;
    }
}