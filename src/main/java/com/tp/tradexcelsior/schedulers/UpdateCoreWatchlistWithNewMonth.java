package com.tp.tradexcelsior.schedulers;

import com.tp.tradexcelsior.service.impl.CoreWatchlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class UpdateCoreWatchlistWithNewMonth {

  @Autowired
  private CoreWatchlistService coreWatchlistService;

  //  Scheduled task that runs at the beginning of each month (on the 1st day at midnight).
  //  This task runs once a month on the first day at midnight, ensuring that the core watchlist are updated with the new month's market trend.
  @Scheduled(cron = "0 0 0 1 * ?")
  public void scheduleTaskForNewMonth() {
    coreWatchlistService.updateCoreWatchlistsForNewMonth();
  }

}
