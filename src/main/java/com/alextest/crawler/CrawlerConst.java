package com.alextest.crawler;

/**
 * Created by alexdrum on 2017/7/24.
 */
public class CrawlerConst {
    // 抓取公司信息爬虫静态变量
    public static final String WIN_ROOT = "C:/Users/alexdrum/Desktop/";
    public static final String LINUX_ROOT = "/home/ziroom/";
    public static final String SEED_FILE = "seedFile.xlsx";
    public static final String TIAN_YAN_CHA_PREFIX = "https://www.tianyancha.com/search?key=";
    public static final String TIAN_YAN_CHA_SUFFIX = "&checkFrom=searchBox";
    public static final String TIAN_YAN_CHA_TEST = TIAN_YAN_CHA_PREFIX + "阔以" + TIAN_YAN_CHA_SUFFIX;


    // 抓取动态代理IP的静态变量
    public static final String KUAI_PROXY = "http://www.kuaidaili.com/free/intr/%s/";
}