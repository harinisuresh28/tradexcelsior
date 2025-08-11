package com.tp.tradexcelsior.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MarketTrendByMonth {
  private String monthYear;  // Month-Year, e.g., "Jan 2025"
  private String trend;      // e.g., "StrongTrend" or "WeakTrend" or "MediumTrend"
}