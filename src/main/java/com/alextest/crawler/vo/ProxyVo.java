package com.alextest.crawler.vo;

import com.alextest.util.TestUtils;
import lombok.Getter;
import lombok.Setter;
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
        TestUtils.log("正在测试：" + this.toString() + "的可用性；");
        try {
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(this.ip, this.port));
            URL url = new URL(TIAN_YAN_CHA_TEST);
            HttpURLConnection uc = (HttpURLConnection) url.openConnection(proxy);
            uc.connect();
        }catch (Exception e){
            TestUtils.log(this.toString() + "经已经失效，从动态代理池中去除。");
            return false;
        }
        TestUtils.log(this.toString() + "经测试可用。");
        return true;
    }

    @Override
    public String toString(){
        return ip + ":" + port;
    }
}