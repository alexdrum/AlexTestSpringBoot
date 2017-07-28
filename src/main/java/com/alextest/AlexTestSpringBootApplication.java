package com.alextest;

import com.alextest.crawler.job.ProxyCrawlerJob;
import com.alextest.crawler.job.ProxyTestJob;
import com.dangdang.ddframe.job.config.JobCoreConfiguration;
import com.dangdang.ddframe.job.config.simple.SimpleJobConfiguration;
import com.dangdang.ddframe.job.lite.api.JobScheduler;
import com.dangdang.ddframe.job.lite.config.LiteJobConfiguration;
import com.dangdang.ddframe.job.reg.base.CoordinatorRegistryCenter;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperConfiguration;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AlexTestSpringBootApplication {

	/**
	 * SpringBoot启动main方法
	 */
	public static void main(String[] args) {
		// SpringBoot容器启动
		SpringApplication.run(AlexTestSpringBootApplication.class, args);
		// 初始化注册中心
		CoordinatorRegistryCenter coordinatorRegistryCenter = createRegistryCenter();
		// 启动抓取代理IP爬虫作业
		new JobScheduler(coordinatorRegistryCenter, createProxyCrawlerJobConfiguration()).init();
		// 启动清理不可用代理IP作业
		new JobScheduler(coordinatorRegistryCenter, createProxyTestJobConfiguration()).init();
	}

	/**
	 * 建立注册中心链接
	 */
	private static CoordinatorRegistryCenter createRegistryCenter() {
		CoordinatorRegistryCenter regCenter = new ZookeeperRegistryCenter(new ZookeeperConfiguration("localhost:2181", "elastic-job-center"));
		regCenter.init();
		return regCenter;
	}

	/**
	 * 建立抓取代理IP爬虫作业
	 */
	private static LiteJobConfiguration createProxyCrawlerJobConfiguration() {
		// 抓取代理IP爬虫作业定义（作业名，Cron表达式，分片数）
		JobCoreConfiguration proxyCrawlerJobCoreConfig = JobCoreConfiguration.newBuilder("proxyCrawlerJob", "0/15 * * * * ?", 1).build();
		// 抓取代理IP类配置
		SimpleJobConfiguration proxyCrawlerSimpleJobConfig = new SimpleJobConfiguration(proxyCrawlerJobCoreConfig, ProxyCrawlerJob.class.getCanonicalName());
		// 定义Lite作业根配置
		LiteJobConfiguration proxyCrawlerJobConfig = LiteJobConfiguration.newBuilder(proxyCrawlerSimpleJobConfig).build();
		// 返回配置
		return proxyCrawlerJobConfig;
	}

	/**
	 * 建立清理不可用代理IP作业
	 */
	private static LiteJobConfiguration createProxyTestJobConfiguration() {
		// 清理代理IP爬虫作业定义（作业名，Cron表达式，分片数）
		JobCoreConfiguration proxyTestJobCoreConfig = JobCoreConfiguration.newBuilder("proxyTestJob", "0/30 * * * * ?", 1).build();
		// 清理代理IP类配置
		SimpleJobConfiguration proxyTestSimpleJobConfig = new SimpleJobConfiguration(proxyTestJobCoreConfig, ProxyTestJob.class.getCanonicalName());
		// 定义Lite作业根配置
		LiteJobConfiguration proxyTestJobConfig = LiteJobConfiguration.newBuilder(proxyTestSimpleJobConfig).build();
		// 返回配置
		return proxyTestJobConfig;
	}
}
