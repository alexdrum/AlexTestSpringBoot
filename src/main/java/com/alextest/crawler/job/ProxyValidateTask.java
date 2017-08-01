package com.alextest.crawler.job;

import com.alextest.common.AlexContextAware;
import com.alextest.crawler.thread.ITaskExecutorHandler;
import com.alextest.crawler.thread.ParallelDataProcessService;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 验证代理IP有效性多线程任务
 */
@Component
public class ProxyValidateTask extends ParallelDataProcessService {

    @Override
    public List<Object> getData() {
        return null;
    }

    @Override
    public String getTaskName() {
        return "ProxyValidateTask";
    }

    @Override
    public ITaskExecutorHandler getTaskExecutorHandler() {
        return (ProxyValidateExecutor) AlexContextAware.getBeanFromApplicationContext("proxyValidateExecutor");
    }

    @Override
    public String getTaskDescription() {
        return "验证代理IP有效性多线程任务";
    }
}
