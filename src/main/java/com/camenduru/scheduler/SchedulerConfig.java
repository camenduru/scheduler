package com.camenduru.scheduler;

import java.util.concurrent.Executor;

import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.camenduru.scheduler.repository.JobRepository;

@Configuration
@EnableAsync
@EnableScheduling
public class SchedulerConfig implements AsyncConfigurer, SchedulingConfigurer {
    
    @Value("${camenduru.scheduler.cron1}")
    private String cron1;

    @Autowired
    private JobRepository jobRepository;
    
    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskScheduler executor = new ThreadPoolTaskScheduler();
        executor.setPoolSize(100);
        executor.setThreadNamePrefix("schedule-pool-");
        executor.initialize();
        return executor;
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(getAsyncExecutor());
        taskRegistrar.addTriggerTask(new Runnable() {
            @Override
            public void run() {
                System.out.println("configureTasks()");
            }
        }, new Trigger() {
            @Override
            public Date nextExecutionTime(TriggerContext triggerContext) {
                Calendar nextExecutionTime = new GregorianCalendar();
                Date lastActualExecutionTime = triggerContext.lastActualExecutionTime();
                nextExecutionTime.setTime(lastActualExecutionTime != null ? lastActualExecutionTime : new Date());
                nextExecutionTime.add(Calendar.SECOND, 60);
                System.out.println("Trigger() | " + 60);
                return nextExecutionTime.getTime();
            }

            @Override
            public Instant nextExecution(TriggerContext triggerContext) {
                return nextExecutionTime(triggerContext).toInstant();
            }
        });
        taskRegistrar.addCronTask(new Runnable() {
            @Override
            public void run() {
                try {
                    int size = jobRepository.findAll().size();
                    System.out.println("addCronTask() | " + size);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, cron1);
    }

}