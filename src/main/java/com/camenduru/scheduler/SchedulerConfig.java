package com.camenduru.scheduler;

import java.util.Date;
import java.util.concurrent.Executor;

import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import com.camenduru.scheduler.domain.enumeration.JobStatus;
import com.camenduru.scheduler.repository.DetailRepository;
import com.camenduru.scheduler.repository.JobRepository;

@Configuration
@EnableAsync
@EnableScheduling
public class SchedulerConfig implements AsyncConfigurer, SchedulingConfigurer {

    @Value("${camenduru.scheduler.default.free.total}")
    private String defaultFreeTotal;

    @Value("${camenduru.scheduler.default.paid.total}")
    private String defaultPaidTotal;
    
    @Value("${camenduru.scheduler.cron1}")
    private String cron1;

    @Value("${camenduru.scheduler.cron2}")
    private String cron2;

    @Value("${camenduru.scheduler.cron3}")
    private String cron3;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private DetailRepository detailRepository;
    
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
        taskRegistrar.addCronTask(new Runnable() {
            @Override
            public void run() {
                try {
                    Date twelveHoursAgo = new Date(System.currentTimeMillis() - (12 * 60 * 60 * 1000));
                    jobRepository.findAllNonExpiredJobsOlderThanTheDate(twelveHoursAgo)
                        .forEach(job -> {
                            job.setStatus(JobStatus.EXPIRED);
                            jobRepository.save(job);
                        });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, cron1);
        taskRegistrar.addCronTask(new Runnable() {
            @Override
            public void run() {
                try {
                    detailRepository.findAllByMembershipIsFree()
                        .forEach(detail -> {
                            detail.setTotal(defaultFreeTotal);
                            detailRepository.save(detail);
                        });
                    detailRepository.findAllByMembershipIsPaid()
                        .forEach(detail -> {
                            detail.setTotal(defaultPaidTotal);
                            detailRepository.save(detail);
                        });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, cron2);
        taskRegistrar.addCronTask(new Runnable() {
            @Override
            public void run() {
                try {
                    Date tenMinutesAgo = new Date(System.currentTimeMillis() - (10 * 60 * 1000));
                    jobRepository.findAllWorkingJobsOlderThanTheDate(tenMinutesAgo)
                        .forEach(job -> {
                            job.setStatus(JobStatus.FAILED);
                            jobRepository.save(job);
                        });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, cron3);
    }

}