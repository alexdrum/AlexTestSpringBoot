package com.alextest.crawler.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

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

    //
    public Integer getHashCode() {
        if (StringUtils.isBlank(ip) || port <= 0) {
            return -1;
        }
        String proxyString = ip + ":" + String.valueOf(port);
        return proxyString.hashCode();
    }
}
