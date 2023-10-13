package l.f.mappool.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.embedded.tomcat.TomcatProtocolHandlerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.core.task.support.TaskExecutorAdapter;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

@Slf4j
@EnableAsync
@Configuration
@EnableScheduling
public class ThreadPoolConfig {
    private static final ThreadFactory VIRTUAL_THREAD_FACTORY = Thread.ofVirtual()
            .name("v-thread-",0)
            .uncaughtExceptionHandler((thread, exception) -> {
                log.error("thread [{}] throw error:",thread.getName(), exception);
            })
            .factory();
    private static final ExecutorService virtualThreadPerTaskExecutor = Executors.newThreadPerTaskExecutor(VIRTUAL_THREAD_FACTORY);


    @Bean
    public TaskExecutor taskExecutor() {
        return new TaskExecutorAdapter(virtualThreadPerTaskExecutor);
    }

    @Bean
    public Executor threadPoolTaskExecutor() {
        return taskExecutor();
    }

    @Bean
    public AsyncTaskExecutor applicationTaskExecutor() {
        return (TaskExecutorAdapter)taskExecutor();
    }

    @Bean
    public TomcatProtocolHandlerCustomizer<?> protocolHandlerCustomizer() {
        return protocolHandler -> protocolHandler.setExecutor(virtualThreadPerTaskExecutor);
    }
}
