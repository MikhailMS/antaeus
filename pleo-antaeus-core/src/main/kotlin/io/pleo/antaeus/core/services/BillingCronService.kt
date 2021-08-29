package io.pleo.antaeus.core.services

import org.quartz.*
import org.quartz.impl.StdSchedulerFactory

/*
  BillingCronService - responsible for linking BillingService and Cron scheduler,
  so we can control how frequent BillingService would be executed
 */
class BillingCronService(
        private val billingService: BillingService,
        private val cronExpression: String
) {
    fun start() {
        try {
            val scheduler: Scheduler = StdSchedulerFactory.getDefaultScheduler()
            val job = JobBuilder
                    .newJob(BillingServiceJob::class.java)
                    .build()
            job.jobDataMap["billingService"] = billingService
            val trigger = TriggerBuilder
                    .newTrigger()
                    .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
                    .build()

            scheduler.start()
            scheduler.scheduleJob(job, trigger)
        } catch (exception: SchedulerException) {
            exception.printStackTrace();
        }
    }
}

/*
  BillingServiceJob - responsible for calling functions from BillingService when BillingCronService is triggered
 */
class BillingServiceJob: Job {
    override fun execute(context: JobExecutionContext) {
        val service = context.jobDetail.jobDataMap["billingService"] as BillingService
        service.processInvoices()
    }
}