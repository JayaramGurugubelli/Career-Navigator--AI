package career_Navigator_parent.coding.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableAsync
public class CodingAsyncConfig {

    @Bean(name = "submissionExecutor")
    public Executor submissionExecutor() {

        ThreadPoolTaskExecutor executor =
                new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(12);
        executor.setQueueCapacity(500);

        executor.setThreadNamePrefix(
                "submission-executor-"
        );

        executor.setWaitForTasksToCompleteOnShutdown(
                true
        );

        executor.setAwaitTerminationSeconds(
                60
        );

        /*
         * CallerRunsPolicy avoids silently losing a submission event
         * when the executor queue is temporarily full.
         */
        executor.setRejectedExecutionHandler(
                new ThreadPoolExecutor.CallerRunsPolicy()
        );

        executor.setTaskDecorator(
                contextCopyingTaskDecorator()
        );

        executor.initialize();

        return executor;
    }

    @Bean(name = "testCaseGenerationExecutor")
    public Executor testCaseGenerationExecutor() {

        ThreadPoolTaskExecutor executor =
                new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(4);
        executor.setQueueCapacity(100);

        executor.setThreadNamePrefix(
                "test-case-generation-"
        );

        executor.setWaitForTasksToCompleteOnShutdown(
                true
        );

        executor.setAwaitTerminationSeconds(
                60
        );

        executor.setRejectedExecutionHandler(
                new ThreadPoolExecutor.CallerRunsPolicy()
        );

        executor.setTaskDecorator(
                contextCopyingTaskDecorator()
        );

        executor.initialize();

        return executor;
    }

    private TaskDecorator contextCopyingTaskDecorator() {

        return runnable -> {

            ClassLoader contextClassLoader =
                    Thread.currentThread()
                            .getContextClassLoader();

            return () -> {

                Thread currentThread =
                        Thread.currentThread();

                ClassLoader previousClassLoader =
                        currentThread.getContextClassLoader();

                try {
                    currentThread.setContextClassLoader(
                            contextClassLoader
                    );

                    runnable.run();

                } finally {
                    currentThread.setContextClassLoader(
                            previousClassLoader
                    );
                }
            };
        };
    }
}