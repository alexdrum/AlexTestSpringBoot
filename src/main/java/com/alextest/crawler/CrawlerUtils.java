package com.alextest.crawler;

import com.alextest.crawler.vo.ProxyVo;
import com.alextest.util.TestUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;

/**
 * Created by alexdrum on 2017/7/28.
 */
public class CrawlerUtils {

    // 请求URL获得网页的document对象
    public static Document getDocumentFromURL(String targetURL) throws IOException {
        Document returnDocument = Jsoup.connect(targetURL).get();
        return returnDocument;
    }

    // 通过代理IP请求URL获得网页的document对象
    public static Document getDocumentFromURLWithProxy(String targetURL, ProxyVo proxyVo) throws IOException {
        Document returnDocument;
        String proxyIp = proxyVo.getIp();
        Integer proxyPort = proxyVo.getPort();

        // 使用代理IP请求网页
        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyIp, proxyPort));
        URL url = new URL(targetURL);
        HttpURLConnection uc = (HttpURLConnection) url.openConnection(proxy);
        uc.connect();
        InputStream is = uc.getInputStream();
        BufferedReader buffer = new BufferedReader(new InputStreamReader(is));
        StringBuffer bs = new StringBuffer();
        String l;
        while ((l = buffer.readLine()) != null) {
            bs.append(l);
        }

        // 将获得的网页转为文档对象
        TestUtils.log("通过代理IP获得网页文档对象内容：" + bs.toString());
        returnDocument = Jsoup.parse(bs.toString());
        return returnDocument;
    }
}
