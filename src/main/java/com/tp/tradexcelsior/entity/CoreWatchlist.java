package com.tp.tradexcelsior.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CoreWatchlist extends CommonEntity{

  @Id
  private String id;

  @Indexed(unique = true)
  private String company;
  private String analysisLink;
  private String Sector;
  private String marketCap;
  private List<MarketTrendByMonth> marketTrendByMonthList;
}

