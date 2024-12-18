package com.iceteasoftware.notification.configuration;

import com.iceteasoftware.notification.service.impl.EmailJob;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.CronScheduleBuilder;
import org.quartz.TriggerBuilder;
import org.quartz.JobBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuartzConfig {

    @Bean
    public JobDetail emailJobDetail() {
        return JobBuilder.newJob(EmailJob.class)
                .withIdentity("emailJob")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger emailJobTriggerDaily() {
        return TriggerBuilder.newTrigger()
                .forJob(emailJobDetail())
                .withIdentity("emailJobTriggerDaily")
                .withSchedule(CronScheduleBuilder.dailyAtHourAndMinute(17, 5))
                .build();
    }

    @Bean
    public Trigger emailJobTriggerWeekly() {
        return TriggerBuilder.newTrigger()
                .forJob(emailJobDetail())
                .withIdentity("emailJobTriggerWeekly")
                .withSchedule(CronScheduleBuilder.cronSchedule("0 0 9 ? * MON")) // Mỗi thứ Hai lúc 9:00
                .build();
    }

    @Bean
    public Trigger emailJobTriggerMonthly() {
        return TriggerBuilder.newTrigger()
                .forJob(emailJobDetail())
                .withIdentity("emailJobTriggerMonthly")
                .withSchedule(CronScheduleBuilder.cronSchedule("0 0 9 1 * ?")) // Ngày 1 của mỗi tháng lúc 9:00
                .build();
    }
}
