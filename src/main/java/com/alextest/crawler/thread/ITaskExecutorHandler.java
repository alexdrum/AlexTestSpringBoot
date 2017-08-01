package com.alextest.crawler.thread;


import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 任务执行器
 */
public interface ITaskExecutorHandler {
    public String handle(Object var1, String taskName);
    public String handle(Object var1, String taskName, CopyOnWriteArrayList<Object> resultList);
    public String handle(Object var1, String taskName, Map<String, Object> paramMap);
    public String handle(Object var1, String taskName, Map<String, Object> paramMap, CopyOnWriteArrayList<Object> resultList);
}
