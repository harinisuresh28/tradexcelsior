package com.tp.tradexcelsior.criteria;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.util.StringUtils;

public class CoreWatchlistSearchCriteriaBuilder {

  public static Criteria buildSearchCriteria(String companyName) {
    // Initialize Criteria object
    Criteria criteria = new Criteria();

    // Only add the filter if companyName is provided
    if (StringUtils.hasText(companyName)) {
      // Use regex for case-insensitive search for partial matching
      criteria.and("company").regex(companyName, "i");
    }

    return criteria;
  }
}