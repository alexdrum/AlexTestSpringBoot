package com.alextest.crawler.service;

import com.alextest.common.AlexContextAware;
import com.alextest.crawler.CrawlerUtils;
import com.alextest.crawler.vo.ProxyVo;
import com.alextest.util.TestUtils;
import com.google.common.collect.Maps;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.alextest.crawler.CrawlerConst.KUAI_PROXY;

/**
 * Created by alexdrum on 2017/7/28.
 */
@Service
public class ProxyService {

    @Autowired
    ProxyPool proxyPool;

    // 从代理网站获取免费的代理IP
    public void getProxyFromWeb() {
        TestUtils.log("动态代理爬虫启动");
        while (true) {
            try {
                for (int i = 1; i <= 15; i++) {
                    Document document;
                    try {
                        TestUtils.log("开始抓取一页IP；");
                        // 抓取动态代理IP的页面
                        String targetURL = String.format(KUAI_PROXY, String.valueOf(i));
//                        ProxyVo aProxyVo = null;
//                        try {
//                            aProxyVo = proxyPool.getProxy();
//                        } catch (Exception e) {
//                            TestUtils.log("动态代理IP池没货了，用本机IP抓吧；");
//                        }
//                        if (aProxyVo == null) {
//                            document = CrawlerUtils.getDocumentFromURL(targetURL);
//                        } else {
//                            document = CrawlerUtils.getDocumentFromURLWithProxy(targetURL, aProxyVo);
//                        }
                        document = CrawlerUtils.getDocumentFromURL(targetURL);
                        TestUtils.log("抓取到了动态代理IP的页面;");

                        // 解析代理IP内容并放入到代理IP池中
                        Map<Integer, ProxyVo> proxyVoMap = getProxyVoFromDocuments(document);
                        proxyPool.addBatchProxyVos(proxyVoMap);
                        TestUtils.log("抓取一页IP完毕，抓取线程：" + Thread.currentThread().getName() + " 休息5秒；");
                        // 休息5秒钟
                        Thread.sleep(5000);
                    } catch (Exception e) {
                        TestUtils.log("抓取IP页面出现异常，继续尝试；");
                        continue;
                    }
                }
                TestUtils.log("抓取多页IP完毕，抓取线程：" + Thread.currentThread().getName() + " 休息60秒；");
                Thread.sleep(600000);
            } catch (Exception e) {
                if (!(e instanceof InterruptedException)) {
                    continue;
                }
            }
        }
    }

    // 从获取的页面中解析ip地址信息
    private Map<Integer, ProxyVo> getProxyVoFromDocuments(Document document) {
        Map<Integer, ProxyVo> resultMap = Maps.newHashMap();

        Elements ipTable = document.getElementsByClass("table table-bordered table-striped");
        TestUtils.log("存放IP信息的table：" + ipTable.toString());

        for (int i = 0; i < 15; i++) {
            String ip = ipTable.get(0).child(1).child(i).child(0).text();
            String port = ipTable.get(0).child(1).child(i).child(1).text();
            ProxyVo proxyVo = new ProxyVo();
            proxyVo.setIp(ip);
            proxyVo.setPort(Integer.valueOf(port));
            Integer hashCode = proxyVo.getHashCode();
            if (hashCode != -1) {
                resultMap.put(hashCode, proxyVo);
                TestUtils.log("抓取到了代理IP：" + proxyVo.toString());
            }
        }

        return resultMap;
    }

    // 测试代理池中的ip可用性
    public void testProxy() {
        TestUtils.log("动态代理测试启动");
        while (true) {
            try {
                // 获取动态代理Map
                Map<Integer, ProxyVo> proxyVoMap = proxyPool.getProxyVoMap();
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
                TestUtils.log("动态代理测试线程：" + Thread.currentThread().getName() + " 休息30秒；");
                Thread.sleep(30000);
            } catch (Exception e) {
                if (!(e instanceof InterruptedException)) {
                    continue;
                }
            }
        }
    }
}