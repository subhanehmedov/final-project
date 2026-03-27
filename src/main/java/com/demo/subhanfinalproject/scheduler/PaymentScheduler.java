package com.demo.subhanfinalproject.scheduler;

import com.demo.subhanfinalproject.service.PaymentService;
import lombok.RequiredArgsConstructor;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentScheduler {
    private final PaymentService paymentService;

    @Scheduled(cron = "0 0 0 * * *")
    @SchedulerLock(name = "processPaymentsLock", lockAtLeastFor = "1m", lockAtMostFor = "5m")
    public void processPaymentsTask() {
        paymentService.processPendingPayments();
    }
}
