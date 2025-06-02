package com.trnqngmnh.library.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.trnqngmnh.library.service.OrderWebService;

@Configuration
@EnableScheduling
public class ScheduledTasks {

	@Autowired
	private OrderWebService orderWebService;

	@Scheduled(cron = "0 0 0 * * ?") // Mỗi ngày lúc 00:00
	public void scheduleDeleteOldOrders() {
		orderWebService.deleteOldOrders();
	}
}
