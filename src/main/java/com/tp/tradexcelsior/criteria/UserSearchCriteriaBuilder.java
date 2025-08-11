package com.tp.tradexcelsior.criteria;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.util.StringUtils;

public class UserSearchCriteriaBuilder {

  public static Criteria buildSearchCriteria(String name, String email, String mobileNumber) {
    Criteria criteria = new Criteria();

    // Add conditions dynamically based on provided parameters
    boolean hasFilter = false;  // Flag to check if any filters are applied

    if (StringUtils.hasText(name)) {
      // Search for both firstName and lastName fields using orOperator
      criteria = criteria.orOperator(
          Criteria.where("firstName").regex(name, "i"),
          Criteria.where("lastName").regex(name, "i")
      );
      hasFilter = true;
    }

    if (StringUtils.hasText(email)) {
      // Add email filter using 'and' for more strict matching
      criteria = hasFilter ? criteria.and("email").regex(email, "i") : Criteria.where("email").regex(email, "i");
      hasFilter = true;
    }


    // Mobile number filter
    if (StringUtils.hasText(mobileNumber)) {
      // Apply regex on mobile number if it's not null or empty
      criteria = hasFilter ? criteria.and("mobileNumber").regex(mobileNumber, "i") : Criteria.where("mobileNumber").regex(mobileNumber, "i");
      hasFilter = true;
    } else if (mobileNumber == null) {
      // Ensure we don't match documents where mobileNumber is null
      criteria = hasFilter ? criteria.and("mobileNumber").exists(true) : Criteria.where("mobileNumber").exists(true);
      hasFilter = true;
    }

    return criteria;
  }
}