package com.alextest.crawler.job;

import com.alextest.crawler.thread.ITaskExecutorHandler;
import com.alextest.crawler.vo.ProxyVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * 验证代理IP有效性 处理器
 */
@Slf4j
@Service
public class ProxyValidateExecutor implements ITaskExecutorHandler {

    @Override
    public String handle(Object var1, String taskName) {
        return null;
    }

    @Override
    public String handle(Object var1, String taskName, CopyOnWriteArrayList<Object> resultList) {
        return null;
    }

    @Override
    public String handle(Object var1, String taskName, Map<String, Object> paramMap) {
        return null;
    }

    @Override
    public String handle(Object var1, String taskName, Map<String, Object> paramMap, CopyOnWriteArrayList<Object> resultList) {
        List<ProxyVo> proxyVoList = (List<ProxyVo>) var1;
        proxyVoList.forEach(proxyVo -> {
            if(proxyVo.isValid()==false){
                resultList.add(proxyVo);
            }
        });
        return null;
    }
}
