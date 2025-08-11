package com.tp.tradexcelsior.dto.response;

import com.tp.tradexcelsior.entity.MarketTrendByMonth;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CoreWatchlistResponseDto {

  private String id;
  private String company;
  private String analysisLink;
  private String Sector;
  private String marketCap;
  private List<MarketTrendByMonth> marketTrendByMonthList;

}
