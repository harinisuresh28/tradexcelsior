package com.tp.tradexcelsior.service;

import com.tp.tradexcelsior.dto.request.CoreWatchlistRequestDto;
import com.tp.tradexcelsior.dto.request.WatchlistTrendUpdateDto;
import com.tp.tradexcelsior.dto.response.CoreWatchlistResponseDto;
import com.tp.tradexcelsior.dto.response.PagedResponse;
import com.tp.tradexcelsior.util.ResponseWrapper;

public interface ICoreWatchlistService {
  ResponseWrapper<CoreWatchlistResponseDto> addCoreWatchlist(CoreWatchlistRequestDto watchlistRequestDto);
  ResponseWrapper<CoreWatchlistResponseDto> getCoreWatchList(String coreWatchlistId);
  ResponseWrapper<PagedResponse<CoreWatchlistResponseDto>> getAllCoreWatchlist(int page, int size, String monthYear, String sortBy, String sortDirection);
  ResponseWrapper<CoreWatchlistResponseDto> updateCoreWatchlist(CoreWatchlistRequestDto watchlistRequestDto, String coreWatchlistId);
  ResponseWrapper<PagedResponse<CoreWatchlistResponseDto>> searchCoreWatchlists(String company, int page, int size);
  ResponseWrapper<CoreWatchlistResponseDto> updateCurrentMonthTrend(WatchlistTrendUpdateDto watchlistTrendUpdateDto);
  ResponseWrapper<String> updateCoreWatchlistsForNewMonth();
  ResponseWrapper<String> deleteCoreWatchList(String coreWatchlistId);
}
