package com.alextest.crawler.thread;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Service
public abstract class ParallelDataProcessService {

    public static final String TO_BE_PROCESSED_DATA_LIST = "toBeProcessedDataList";

    // 多线程处理数据任务的线程数暂时为cpu核心数的8倍
    private static int threadNum = 4;//Runtime.getRuntime().availableProcessors();
    // 初始化异步执行数据处理任务的线程池
    private ExecutorService asyncExecutorService = Executors.newFixedThreadPool(threadNum, new NamedThreadFactory(this.getClass().getSimpleName()));

    /**
     * 任务处理
     *
     * @throws Exception
     */
    public void doTask() {
        doTask(null, null);
    }

    public void doTask(CopyOnWriteArrayList<Object> resultList) {
        doTask(null, resultList);
    }

    public void doTask(Map<String, Object> paramMap) {
        doTask(paramMap, null);
    }

    public void doTask(Map<String, Object> paramMap, CopyOnWriteArrayList<Object> resultList) {
        long start = System.currentTimeMillis();

        String taskName = getTaskName();

        log.info("数据任务：" + taskName + "，" + getTaskDescription() + "，开始；");

        try {
            // 获得将要处理的数据
            List<Object> toBeProcessedDataList = getData();

            // 如果调用子类的getData方法为空，再试图从参数中获取待处理数据
            if (CollectionUtils.isEmpty(toBeProcessedDataList) && !MapUtils.isEmpty(paramMap)) {
                toBeProcessedDataList = (List<Object>) paramMap.get(TO_BE_PROCESSED_DATA_LIST);
            }

            if (CollectionUtils.isEmpty(toBeProcessedDataList)) {
                log.info("数据任务：" + taskName + "没有找到需要待处理的数据，任务退出。");
                return;
            }
            log.info("数据任务：" + taskName + " 待处理总条数：" + toBeProcessedDataList.size());

            // 数据分片
            Map<Integer, List<Object>> dataShards = parseData(threadNum, toBeProcessedDataList);

            // 设定倒计时计数器
            CountDownLatch latch = new CountDownLatch(dataShards.size());

            // 启动线程，异步处理多个数据分片
            Iterator<Map.Entry<Integer, List<Object>>> allShards = dataShards.entrySet().iterator();
            SuperTask taskRunner = null;

            while (allShards.hasNext()) {
                Map.Entry<Integer, List<Object>> item = allShards.next();
                taskRunner = new TaskRunner(taskName, item.getValue(), getTaskExecutorHandler(), paramMap, resultList);
                taskRunner.setStopWatch(new StopWatch());
                taskRunner.setCountDownLatch(latch);
                asyncExecutorService.submit(taskRunner);
            }
            latch.await();

        } catch (Exception e) {
            log.error("数据任务：" + taskName + " 出现异常，请注意！", e);
        } finally {
            log.info("数据任务：" + taskName + " 所有数据全部处理完成！" + " 总耗时：" + (System.currentTimeMillis() - start) / 1000 + " s");
        }
    }

    /**
     * 获取该任务的执行数据
     *
     * @return
     */
    public abstract List<Object> getData();

    /**
     * 任务编码
     *
     * @return
     */
    public abstract String getTaskName();

    /**
     * 是否自动关闭 当没有可执行的数据时自动关闭任务
     *
     * @return
     */
    public boolean autoClose() {
        return true;
    }

    /**
     * 获取单个任务的执行器
     *
     * @return
     */
    public abstract ITaskExecutorHandler getTaskExecutorHandler();

    /**
     * 任务的描述信息
     *
     * @return
     */
    public abstract String getTaskDescription();

    /**
     * 获取任务待处理数据大小
     *
     * @return
     */
    public int getDataSize() {
        return 0;
    }

    ;

    /**
     * 把要处理的数据添加的分组
     *
     * @param map
     * @param index
     * @param item
     */
    protected void addToMap(Map<Integer, List<Object>> map, int index, Object item) {
        List<Object> data = map.get(Integer.valueOf(index));
        if (data == null) {
            data = new ArrayList<>();
        }
        data.add(item);
        map.put(Integer.valueOf(index), data);
    }

    /**
     * 任务分发（分片）
     *
     * @param threadNum
     * @param toBeProcessedDataList
     * @return
     */
    private Map<Integer, List<Object>> parseData(int threadNum, List<Object> toBeProcessedDataList) {
        Map<Integer, List<Object>> map = new HashMap<>();
        for (int i = 0; i < toBeProcessedDataList.size(); i++) {
            for (int index = 0; index < threadNum; index++) {
                if (i % threadNum == index) {
                    addToMap(map, index, toBeProcessedDataList.get(i));
                }
            }
        }
        return map;
    }
}