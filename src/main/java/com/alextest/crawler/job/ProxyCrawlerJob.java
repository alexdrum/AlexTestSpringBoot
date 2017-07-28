package com.alextest.crawler.job;

import com.alextest.common.AlexContextAware;
import com.alextest.crawler.service.ProxyService;
import com.alextest.crawler.vo.ProxyVo;
import com.alextest.util.TestUtils;
import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.google.common.collect.Maps;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.alextest.crawler.CrawlerConst.KUAI_PROXY;

/**
 * Created by alexdrum on 2017/7/27.
 */
@Component
public class ProxyCrawlerJob implements SimpleJob {

    @Override
    public void execute(ShardingContext context) {
        TestUtils.log("抓取动态代理作业启动");

        ProxyService proxyService = (ProxyService) AlexContextAware.getBeanFromApplicationContext("proxyService");
        Document document;
        for (int i = 1; i <= 5; i++) {
            try {
                // 抓取动态代理IP的页面
                String targetURL = String.format(KUAI_PROXY, String.valueOf(i));
                document = Jsoup.connect(targetURL).get();
                TestUtils.log("抓取到了动态代理IP的页面：" + document.toString());

                // 解析代理IP内容并放入到代理IP池中
                Map<Integer, ProxyVo> proxyVoMap = getProxyVoFromDocuments(document);
                proxyService.addBatchProxyVos(proxyVoMap);

                // 休息5秒钟
                Thread.sleep(5000);
            } catch (Exception e) {
                continue;
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
            }
        }

        return resultMap;
    }
}
