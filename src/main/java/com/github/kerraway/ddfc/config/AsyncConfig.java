package com.github.kerraway.ddfc.config;

import org.slf4j.MDC;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.annotation.AsyncConfigurerSupport;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.Assert;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executor;

import static com.github.kerraway.ddfc.filter.MdcFilter.REQUEST_UUID_KEY;

/**
 * 配置异步线程池，并拷贝主线程的 MDC 信息
 *
 * @author kerraway
 * @date 2019/05/19
 */
@EnableAsync
@Configuration
public class AsyncConfig extends AsyncConfigurerSupport {

  private static final int POOL_SIZE = 40;
  private static final String THREAD_NAME_PREFIX = "async-thread-";

  @Override
  public Executor getAsyncExecutor() {
    return getExecutor(THREAD_NAME_PREFIX, POOL_SIZE);
  }

  private Executor getExecutor(String threadNamePrefix, int poolSize) {
    Assert.notNull(threadNamePrefix, "threadNamePrefix must not be null.");
    Assert.isTrue(poolSize > 0, "poolSize must be positive.");

    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setTaskDecorator(getTaskDecoratorWithRequestUuid());
    executor.setThreadNamePrefix(threadNamePrefix);
    executor.setCorePoolSize(poolSize);
    executor.setMaxPoolSize(poolSize);
    executor.setWaitForTasksToCompleteOnShutdown(true);
    executor.setAwaitTerminationSeconds(10);
    executor.initialize();
    return executor;
  }

  private TaskDecorator getTaskDecoratorWithRequestUuid() {
    return runnable -> {
      Map<String, String> contextMap = MDC.getCopyOfContextMap();
      if (contextMap == null || contextMap.get(REQUEST_UUID_KEY) == null) {
        MDC.put(REQUEST_UUID_KEY, UUID.randomUUID().toString());
        contextMap = MDC.getCopyOfContextMap();
      }
      Map<String, String> finalContextMap = contextMap;
      return () -> {
        try {
          MDC.setContextMap(finalContextMap);
          runnable.run();
        } finally {
          MDC.clear();
        }
      };
    };
  }

}
