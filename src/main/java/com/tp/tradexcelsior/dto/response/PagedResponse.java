package com.tp.tradexcelsior.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PagedResponse<T> {
  private List<T> content;        // The actual data (search results)
  private int totalItems;         // Total items in the database
  private int totalPages;         // Total number of pages
  private int currentPage;        // Current page number
  private int pageSize;           // Number of items per page
}