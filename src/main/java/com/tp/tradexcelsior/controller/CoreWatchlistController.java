package com.tp.tradexcelsior.controller;

import com.tp.tradexcelsior.dto.request.CoreWatchlistRequestDto;
import com.tp.tradexcelsior.dto.request.WatchlistTrendUpdateDto;
import com.tp.tradexcelsior.dto.response.CoreWatchlistResponseDto;
import com.tp.tradexcelsior.dto.response.PagedResponse;
import com.tp.tradexcelsior.service.impl.CoreWatchlistService;
import com.tp.tradexcelsior.util.ResponseWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
@Tag(name = "CoreWatchList Management", description = "APIs for managing CoreWatchList")
@RestController
@RequestMapping("/api/v1/core-watchlist")
public class CoreWatchlistController {

  @Autowired
  private CoreWatchlistService coreWatchlistService;

  // Create a new core watchlist
  @Operation(summary = "Create a new core watchlist", description = "Create a new core watchlist entry for a company")
  @PostMapping
  public ResponseEntity<ResponseWrapper<CoreWatchlistResponseDto>> addCoreWatchlist(@RequestBody @Valid CoreWatchlistRequestDto watchlistRequestDto) {
    ResponseWrapper<CoreWatchlistResponseDto> createdWatchlist = coreWatchlistService.addCoreWatchlist(watchlistRequestDto);
    return ResponseEntity.status(HttpStatus.CREATED).body(createdWatchlist);
  }

  // Get a core watchlist by ID
  @Operation(summary = "Get a core watchlist by ID", description = "Fetch the details of a core watchlist by its ID")
  @GetMapping("/{id}")
  public ResponseEntity<ResponseWrapper<CoreWatchlistResponseDto>> getCoreWatchlist(@PathVariable String id) {
    ResponseWrapper<CoreWatchlistResponseDto> watchlistResponse = coreWatchlistService.getCoreWatchList(id);
    return ResponseEntity.ok(watchlistResponse);  // Return status 200 with core watchlist details
  }

  // Get a paginated list of core watchlists
  @Operation(summary = "Get a paginated list of core watchlists", description = "Retrieve a paginated list of core watchlists")
  @GetMapping
  public ResponseEntity<ResponseWrapper<PagedResponse<CoreWatchlistResponseDto>>> getAllCoreWatchlists(
      @RequestParam(defaultValue = "0") int page,  // Default to the first page
      @RequestParam(defaultValue = "10") int size,   // Default to a page size of 10
      @RequestParam(required = false) String monthYear,  // Optional monthYear for sorting by a specific month
      @RequestParam(defaultValue = "asc") String sortDirection, // Default sorting direction "asc"
      @RequestParam(defaultValue = "company") String sortBy  // Default sorting by company
  ) {
    ResponseWrapper<PagedResponse<CoreWatchlistResponseDto>> pagedResponse = coreWatchlistService.getAllCoreWatchlist(
        page, size, monthYear, sortDirection, sortBy
    );
    return ResponseEntity.ok(pagedResponse);
  }


  // Update an existing core watchlist
  @Operation(summary = "Update an existing core watchlist", description = "Update the details of a core watchlist")
  @PutMapping("/{id}")
  public ResponseEntity<ResponseWrapper<CoreWatchlistResponseDto>> updateCoreWatchlist(
      @RequestBody @Valid CoreWatchlistRequestDto watchlistRequestDto, @PathVariable String id) {
    ResponseWrapper<CoreWatchlistResponseDto> updatedWatchlist = coreWatchlistService.updateCoreWatchlist(watchlistRequestDto, id);
    return ResponseEntity.ok(updatedWatchlist);
  }

  // Search core watchlists by parameters
  @Operation(summary = "Search core watchlists", description = "Search core watchlists based on company name or other parameters")
  @GetMapping("/search")
  public ResponseEntity<ResponseWrapper<PagedResponse<CoreWatchlistResponseDto>>> searchCoreWatchlists(
      @RequestParam(required = false) String company,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {

    ResponseWrapper<PagedResponse<CoreWatchlistResponseDto>> searchResults = coreWatchlistService.searchCoreWatchlists(company, page, size);
    return ResponseEntity.ok(searchResults);
  }

  // Update the trend for the current month of a specific company
  @Operation(summary = "Update current month trend for a company", description = "Update the trend for the current month for a specific company's core watchlist")
  @PutMapping("/update-trend")
  public ResponseEntity<ResponseWrapper<CoreWatchlistResponseDto>> updateCurrentMonthTrend(@RequestBody @Valid WatchlistTrendUpdateDto watchlistTrendUpdateDto) {

    ResponseWrapper<CoreWatchlistResponseDto> updatedWatchlist = coreWatchlistService.updateCurrentMonthTrend(watchlistTrendUpdateDto);
    return ResponseEntity.ok(updatedWatchlist);
  }

  // Update all core watchlists for the new month
  @Operation(summary = "Update all core watchlists for the new month", description = "Update all core watchlists with the new month trend (default as empty if not provided)")
  @PutMapping("/update-all")
  public ResponseEntity<ResponseWrapper<String>> updateCoreWatchlistsForNewMonth() {
    ResponseWrapper<String> response = coreWatchlistService.updateCoreWatchlistsForNewMonth();
    return ResponseEntity.ok(response);
  }


  // Delete a core watchlist by ID
  @Operation(summary = "Delete a core watchlist by ID", description = "Delete a core watchlist entry by its ID")
  @DeleteMapping("/{id}")
  public ResponseEntity<ResponseWrapper<String>> deleteCoreWatchlist(@PathVariable String id) {
    ResponseWrapper<String> response = coreWatchlistService.deleteCoreWatchList(id);

    return ResponseEntity.ok(response);
  }
}
