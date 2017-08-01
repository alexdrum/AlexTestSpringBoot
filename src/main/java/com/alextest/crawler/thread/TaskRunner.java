package com.alextest.crawler.thread;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by homelink on 2016/8/3.
 */
@Slf4j
public class TaskRunner extends SuperTask {
    private List<?> value;

    private ITaskExecutorHandler taskExecutorHandler;
    private String taskName;
    private CopyOnWriteArrayList<Object> resultList;
    private Map<String, Object> paramMap;

    public TaskRunner(String taskName, List<?> value, ITaskExecutorHandler taskExecutorHandler,
                      Map<String, Object> paramMap, CopyOnWriteArrayList<Object> resultList) {
        this.taskName = taskName;
        this.value = value;
        this.taskExecutorHandler = taskExecutorHandler;
        this.resultList = resultList;
        this.paramMap = paramMap;
    }

    @Override
    public void run() {
        String threadName = taskName + "_" + Thread.currentThread().getName();
        if (value == null || value.isEmpty()) {
            log.info("线程：" + threadName + " 没有要处理的数据，退出。");
            return;
        }

        long start = System.currentTimeMillis();
        StringBuilder sb = new StringBuilder("线程：" + threadName + " 共需要处理数据：" + value.size() + "条；");
        try {
            if(paramMap==null && resultList==null){
                taskExecutorHandler.handle(value, taskName);
            }else if(paramMap==null && resultList!=null){
                taskExecutorHandler.handle(value, taskName, resultList);
            }else if(paramMap!=null && resultList==null){
                taskExecutorHandler.handle(value, taskName, paramMap);
            }else if(paramMap!=null && resultList!=null){
                taskExecutorHandler.handle(value, taskName, paramMap, resultList);
            }
        } catch (Exception e) {
            log.error("线程：" + threadName + " 处理数据异常 ", e);
        } finally {
            workDone();
            long times = (System.currentTimeMillis() - start) / 1000;
            sb.append("线程：" + threadName + " 完成所有工作，耗时：").append(times).append(" s.");
            log.info(sb.toString());
        }
    }
}
