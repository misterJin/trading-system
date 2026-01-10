package com.example.tradingsystem.job;

import com.example.tradingsystem.application.SettlementService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SettlementJob {

    private final SettlementService settlementService;

    public SettlementJob(SettlementService settlementService) {
        this.settlementService = settlementService;
    }

    // Run once a day at 02:00
    @Scheduled(cron = "0 0 2 * * *")
    public void runDailySettlement() {
        settlementService.settle();
    }
}


