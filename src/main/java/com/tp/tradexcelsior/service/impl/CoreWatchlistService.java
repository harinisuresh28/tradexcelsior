package com.tp.tradexcelsior.service.impl;

import com.tp.tradexcelsior.criteria.CoreWatchlistSearchCriteriaBuilder;
import com.tp.tradexcelsior.dto.request.CoreWatchlistRequestDto;
import com.tp.tradexcelsior.dto.request.WatchlistTrendUpdateDto;
import com.tp.tradexcelsior.dto.response.CoreWatchlistResponseDto;
import com.tp.tradexcelsior.dto.response.PagedResponse;
import com.tp.tradexcelsior.entity.CoreWatchlist;
import com.tp.tradexcelsior.entity.MarketTrendByMonth;
import com.tp.tradexcelsior.exception.custom.CoreWatchlistAlreadyExistsException;
import com.tp.tradexcelsior.exception.custom.CoreWatchlistNotFoundException;
import com.tp.tradexcelsior.repo.CoreWatchlistRepo;
import com.tp.tradexcelsior.service.ICoreWatchlistService;
import com.tp.tradexcelsior.util.ResponseWrapper;
import com.tp.tradexcelsior.util.WatchlistUtils;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class CoreWatchlistService implements ICoreWatchlistService {

  @Autowired
  private CoreWatchlistRepo coreWatchlistRepo;

  @Autowired
  private MongoTemplate mongoTemplate;

  @Autowired
  private ModelMapper modelMapper;


  @Override
  @Transactional
  public ResponseWrapper<CoreWatchlistResponseDto> addCoreWatchlist(CoreWatchlistRequestDto watchlistRequestDto) {
    // Check if the watchlist with the same company already exists
    coreWatchlistRepo.findByCompanyAndIsDeletedFalse(watchlistRequestDto.getCompany())
        .ifPresent(existingWatchlist -> {
          throw new CoreWatchlistAlreadyExistsException("A watchlist entry for this company already exists.");
        });

    // Map the DTO to the CoreWatchlist entity
    CoreWatchlist coreWatchlist = modelMapper.map(watchlistRequestDto, CoreWatchlist.class);

    // Generate the 24 months of market trends
    List<MarketTrendByMonth> marketTrendList = generateLast24MonthsEmptyTrends();

    // Set the market trend list in the CoreWatchlist
    coreWatchlist.setMarketTrendByMonthList(marketTrendList);

    // Save the CoreWatchlist
    CoreWatchlist savedWatchlist = coreWatchlistRepo.save(coreWatchlist);

    // Log the successful save and return the response DTO
    log.info("New core watchlist added successfully : {}", savedWatchlist.getCompany());
    CoreWatchlistResponseDto responseDto = modelMapper.map(savedWatchlist, CoreWatchlistResponseDto.class);
    return ResponseWrapper.success(HttpStatus.CREATED.value(), responseDto, "New core watchlist added successfully.");
  }

  @Override
  public ResponseWrapper<CoreWatchlistResponseDto> getCoreWatchList(String coreWatchlistId) {
    CoreWatchlist coreWatchlist = coreWatchlistRepo.findByIdAndIsDeletedFalse(coreWatchlistId)
        .orElseThrow(() -> {
          log.error("Core Watchlist not found for ID: {}", coreWatchlistId);
          return new CoreWatchlistNotFoundException("Core Watchlist not found for ID: " + coreWatchlistId);
        });

    log.info("Fetching core watchlist with ID: {}", coreWatchlistId);
    CoreWatchlistResponseDto responseDto = modelMapper.map(coreWatchlist, CoreWatchlistResponseDto.class);
    return ResponseWrapper.success(HttpStatus.OK.value(), responseDto, "Core watchlist fetched successfully.");
  }


  @Override
  public ResponseWrapper<PagedResponse<CoreWatchlistResponseDto>> getAllCoreWatchlist(
      int page, int size, String monthYear, String sortDirection, String sortBy) {

    long totalItems = coreWatchlistRepo.countByIsDeletedFalse();
    int totalPages = (int) Math.ceil((double) totalItems / size);

    // Handle the case where the requested page exceeds available pages
    if (page >= totalPages && totalPages > 0) {
      log.warn("Requested page {} exceeds available pages. Returning empty response.", page);
      PagedResponse<CoreWatchlistResponseDto> pagedResponse= new PagedResponse<>(List.of(), (int) totalItems, totalPages, page, size);
      return ResponseWrapper.success(HttpStatus.OK.value(), pagedResponse, "List of core watchlist.");
    }

    // Default size handling
    if (size < 1) {
      size = 10;
    } else if (size > 100) {
      size = 100;
    }

    // Determine the sort direction
    Sort.Direction direction = Sort.Direction.ASC;
    if ("desc".equalsIgnoreCase(sortDirection)) {
      direction = Sort.Direction.DESC;
    }

    // Add sorting by 'company', 'sector', or any other parameter (e.g., 'marketCap')
    Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

    // Fetch paginated data with default sorting by 'company', 'sector', etc.
    Page<CoreWatchlist> coreWatchlistPage = coreWatchlistRepo.findByIsDeletedFalse(pageable);

    List<CoreWatchlistResponseDto> coreWatchlistResponseDto = coreWatchlistPage.getContent().stream()
        .map(res -> modelMapper.map(res, CoreWatchlistResponseDto.class))
        .collect(Collectors.toList());

    // If sortBy is marketCap, sort the results numerically
    if ("marketCap".equalsIgnoreCase(sortBy)) {
      Sort.Direction finalDirection = direction;
      coreWatchlistResponseDto.sort((watchlist1, watchlist2) -> {
        long marketCap1 = WatchlistUtils.parseMarketCap(watchlist1.getMarketCap());
        long marketCap2 = WatchlistUtils.parseMarketCap(watchlist2.getMarketCap());
        return finalDirection == Sort.Direction.ASC
            ? Long.compare(marketCap1, marketCap2)
            : Long.compare(marketCap2, marketCap1);
      });
    } else {
      // Handle sorting by trend or mostRecentTrend
      if (monthYear != null && !monthYear.isEmpty()) {
        // Sort by trend of the given month
        coreWatchlistResponseDto.sort((watchlist1, watchlist2) -> {
          String trend1 = getTrendByMonth(watchlist1, monthYear);
          String trend2 = getTrendByMonth(watchlist2, monthYear);
          return trend1.compareTo(trend2); // Ascending order, for descending use trend2.compareTo(trend1)
        });
      } else {
        // Default to sorting by the trend of the most recent month
        coreWatchlistResponseDto.sort((watchlist1, watchlist2) -> {
          String trend1 = getMostRecentTrend(watchlist1);
          String trend2 = getMostRecentTrend(watchlist2);
          return trend1.compareTo(trend2); // Ascending order, for descending use trend2.compareTo(trend1)
        });
      }
    }

    log.info("Fetched {} core watchlists, page {} of {}.", coreWatchlistResponseDto.size(), page, totalPages);
    PagedResponse<CoreWatchlistResponseDto> pagedResponse= new PagedResponse<>(coreWatchlistResponseDto, (int) totalItems, totalPages, page, size);
    return ResponseWrapper.success(HttpStatus.OK.value(), pagedResponse, "List of core watchlist.");
  }


  // Helper method to get the trend of a specific month
  private String getTrendByMonth(CoreWatchlistResponseDto watchlist, String monthYear) {
    List<MarketTrendByMonth> trends = watchlist.getMarketTrendByMonthList();
    if (trends != null && !trends.isEmpty()) {
      // Find the trend for the specified month
      return trends.stream()
          .filter(trend -> trend.getMonthYear().equalsIgnoreCase(monthYear))
          .map(MarketTrendByMonth::getTrend)
          .findFirst()
          .orElse(""); // Return empty string if the month is not found
    }
    return ""; // If no trends exist, return empty string
  }

  // Helper method to get the trend of the most recent month
  private String getMostRecentTrend(CoreWatchlistResponseDto watchlist) {
    List<MarketTrendByMonth> trends = watchlist.getMarketTrendByMonthList();
    if (trends != null && !trends.isEmpty()) {
      // Sort the trends by the most recent month (e.g., Feb 2025)
      trends.sort(Comparator.comparing(MarketTrendByMonth::getMonthYear).reversed());
      return trends.get(0).getTrend(); // Return the trend of the most recent month
    }
    return ""; // If no trends exist, return empty string
  }


  @Override
  @Transactional
  public ResponseWrapper<CoreWatchlistResponseDto> updateCoreWatchlist(CoreWatchlistRequestDto watchlistRequestDto, String coreWatchlistId) {
    CoreWatchlist existingWatchlist = coreWatchlistRepo.findByIdAndIsDeletedFalse(coreWatchlistId)
        .orElseThrow(() -> {
          log.error("Core watchlist not found with id: {}", coreWatchlistId);
          return new CoreWatchlistNotFoundException("No core watchlist with this id: " + coreWatchlistId);
        });

    // Check if the watchlist with the same company already exists
    coreWatchlistRepo.findByCompanyAndIsDeletedFalse(watchlistRequestDto.getCompany())
        .ifPresent(existingCompany -> {
          throw new CoreWatchlistAlreadyExistsException("A watchlist entry for this company already exists.");
        });

    Update update = new Update();

    if (watchlistRequestDto.getCompany() != null && !watchlistRequestDto.getCompany().isEmpty()) {
      update.set("company", watchlistRequestDto.getCompany());
    }
    if (watchlistRequestDto.getAnalysisLink() != null && !watchlistRequestDto.getAnalysisLink().isEmpty()) {
      update.set("analysisLink", watchlistRequestDto.getAnalysisLink());
    }
    if (watchlistRequestDto.getSector() != null && !watchlistRequestDto.getSector().isEmpty()) {
      update.set("sector", watchlistRequestDto.getSector());
    }
    if (watchlistRequestDto.getMarketCap() != null && !watchlistRequestDto.getMarketCap().isEmpty()) {
      update.set("marketCap", watchlistRequestDto.getMarketCap());
    }

    update.set("lastModified", LocalDateTime.now());

    mongoTemplate.updateFirst(Query.query(Criteria.where("_id").is(coreWatchlistId)), update, CoreWatchlist.class);

    CoreWatchlist updatedWatchlist = coreWatchlistRepo.findByIdAndIsDeletedFalse(coreWatchlistId)
        .orElseThrow(() -> {
          log.error("Core watchlist not found after update with id: {}", coreWatchlistId);
          return new CoreWatchlistNotFoundException("Core watchlist not found after update with id: " + coreWatchlistId);
        });

    log.info("Core watchlist updated successfully with ID: {}", coreWatchlistId);
    CoreWatchlistResponseDto responseDto = modelMapper.map(updatedWatchlist, CoreWatchlistResponseDto.class);
    return ResponseWrapper.success(HttpStatus.OK.value(), responseDto, "Core watchlist updated successfully");
  }

  @Override
  public ResponseWrapper<PagedResponse<CoreWatchlistResponseDto>> searchCoreWatchlists(String company, int page, int size) {
    Query query = new Query();
    query.addCriteria(CoreWatchlistSearchCriteriaBuilder.buildSearchCriteria(company));

    long totalItems = mongoTemplate.count(query, CoreWatchlist.class);
    int totalPages = (int) Math.ceil((double) totalItems / size);

    if (totalPages == 0) {
      page = 0;
    }

    if (page >= totalPages && totalPages > 0) {
      log.warn("Requested page {} exceeds available pages. Returning empty response.", page);
      PagedResponse<CoreWatchlistResponseDto> pagedResponse= new PagedResponse<>(List.of(), (int) totalItems, totalPages, page, size);
      return ResponseWrapper.success(HttpStatus.OK.value(), pagedResponse, "Search result of core watclist");
    }

    Pageable pageable = PageRequest.of(page, size);
    query.with(pageable);

    List<CoreWatchlist> coreWatchlists = mongoTemplate.find(query, CoreWatchlist.class);
    List<CoreWatchlistResponseDto> coreWatchlistResponseDto = coreWatchlists.stream()
        .map(res -> modelMapper.map(res, CoreWatchlistResponseDto.class))
        .collect(Collectors.toList());

    log.info("Fetched {} core watchlists for company '{}' on page {} of {}.", coreWatchlistResponseDto.size(), company, page, totalPages);
    PagedResponse<CoreWatchlistResponseDto> pagedResponse= new PagedResponse<>(coreWatchlistResponseDto, (int) totalItems, totalPages, page, size);
    return ResponseWrapper.success(HttpStatus.OK.value(), pagedResponse, "Search result of core watclist");
  }


  @Override
  @Transactional
  public ResponseWrapper<CoreWatchlistResponseDto> updateCurrentMonthTrend(WatchlistTrendUpdateDto watchlistTrendUpdateDto) {

    String companyName= watchlistTrendUpdateDto.getCompany();
    String trend = watchlistTrendUpdateDto.getTrend();
    // Fetch the CoreWatchlist based on company name
    CoreWatchlist coreWatchlist = coreWatchlistRepo.findByCompanyAndIsDeletedFalse(companyName)
        .orElseThrow(() -> new RuntimeException("Watchlist for company " + companyName + " not found"));

    // Get the current date and the current month-year like "MMM yyyy"
    LocalDate currentDate = LocalDate.now();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM yyyy");
    String currentMonthYear = currentDate.format(formatter);

    // Validate the trend input
    trend = trend.toUpperCase();
    if (!trend.isEmpty() && !trend.equals("STRONG") && !trend.equals("MEDIUM") && !trend.equals("WEAK")) {
      throw new IllegalArgumentException("Invalid trend. Allowed values are 'STRONG', 'MEDIUM', or 'WEAK' in upper or lower case.");
    }

    // Check if the current month exists in the marketTrendList
    List<MarketTrendByMonth> marketTrendList = coreWatchlist.getMarketTrendByMonthList();
    Optional<MarketTrendByMonth> currentMonthEntry = marketTrendList.stream()
        .filter(entry -> entry.getMonthYear().equals(currentMonthYear))
        .findFirst();

    if (currentMonthEntry.isPresent()) {
      // If the current month is in the list, update the trend for that month
      currentMonthEntry.get().setTrend(!trend.isEmpty() ? trend : "");
    } else {
      // If the current month is not in the list, throw an exception or handle as needed
      throw new RuntimeException("Current month entry not found in the watchlist.");
    }

    // Setting Last Modified Time
    coreWatchlist.setLastModified(LocalDateTime.now());

    // Save the updated CoreWatchlist
    CoreWatchlist updatedCoreWatchlist = coreWatchlistRepo.save(coreWatchlist);

    CoreWatchlistResponseDto responseDto= modelMapper.map(updatedCoreWatchlist, CoreWatchlistResponseDto.class);
    return ResponseWrapper.success(HttpStatus.OK.value(), responseDto, "Current month trend updated successfully.");
  }

  // The actual task logic for updating the watchlists
  @Override
  @Transactional
  public ResponseWrapper<String> updateCoreWatchlistsForNewMonth() {
    // Get the current date and the current month-year like "MMM yyyy"
    LocalDate currentDate = LocalDate.now();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM yyyy");
    String currentMonthYear = currentDate.format(formatter);

    // Fetch all CoreWatchlist entries
    List<CoreWatchlist> allCoreWatchlists = coreWatchlistRepo.findByIsDeletedFalse();

    // Prepare to store all updated CoreWatchlist objects
    List<CoreWatchlist> updatedCoreWatchlists = new ArrayList<>();

    LocalDateTime lastModifiedTime = LocalDateTime.now();

    // Update market trends for each CoreWatchlist
    for (CoreWatchlist coreWatchlist : allCoreWatchlists) {
      // Get the marketTrendList for each CoreWatchlist
      List<MarketTrendByMonth> marketTrendList = coreWatchlist.getMarketTrendByMonthList();

      // Check if the first entry of the list is the current month
      if (!marketTrendList.isEmpty() && !marketTrendList.get(0).getMonthYear().equals(currentMonthYear)) {
        // Remove the oldest month (last element)
        marketTrendList.remove(marketTrendList.size() - 1);

        // Add the new month to the front of the list
        marketTrendList.add(0, new MarketTrendByMonth(currentMonthYear, ""));

        // Set the updated list back to the CoreWatchlist entity
        coreWatchlist.setMarketTrendByMonthList(marketTrendList);
        coreWatchlist.setLastModified(lastModifiedTime);

        // Add to the list of updated CoreWatchlists
        updatedCoreWatchlists.add(coreWatchlist);
      }
    }

    // Perform a bulk save if any CoreWatchlist was updated
    if (!updatedCoreWatchlists.isEmpty()) {
      coreWatchlistRepo.saveAll(updatedCoreWatchlists);
      return ResponseWrapper.success(HttpStatus.OK.value(), "New month trend field added.", "Watchlist added with new month's trend (default as empty).");
    }else{
      return ResponseWrapper.success(HttpStatus.OK.value(), "New month trend field already added.","Watchlist already updated with the new month's trend (default as empty).");
    }
  }

  // Helper method to generate the last 24 months of empty market trends
  private List<MarketTrendByMonth> generateLast24MonthsEmptyTrends() {
    List<MarketTrendByMonth> marketTrendList = new ArrayList<>();
    LocalDate currentDate = LocalDate.now();

    // Loop to create trends for the last 24 months
    for (int i = 0; i < 24; i++) {
      LocalDate monthDate = currentDate.minusMonths(i);
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM yyyy");
      String monthYear = monthDate.format(formatter);

      // Add a trend for each month (initially empty trend)
      MarketTrendByMonth marketTrend = new MarketTrendByMonth(monthYear, "");
      marketTrendList.add(marketTrend);
    }

    return marketTrendList;
  }

  @Override
  public ResponseWrapper<String> deleteCoreWatchList(String coreWatchlistId) {
    CoreWatchlist coreWatchlist = coreWatchlistRepo.findByIdAndIsDeletedFalse(coreWatchlistId)
        .orElseThrow(() -> {
          log.error("Core Watchlist not found for ID: {}", coreWatchlistId);
          return new CoreWatchlistNotFoundException("Core Watchlist not found for ID: " + coreWatchlistId);
        });

    log.info("Deleting core watchlist with ID: {}", coreWatchlistId);
    coreWatchlist.setDeleted(true);
    coreWatchlist.setLastModified(LocalDateTime.now());
    coreWatchlistRepo.save(coreWatchlist);
    return ResponseWrapper.success(HttpStatus.OK.value(), "Core watchlist Id: "+ coreWatchlistId,"Deleted successfully.");
  }

}
