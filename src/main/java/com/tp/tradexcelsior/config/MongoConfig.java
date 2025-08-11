package com.tp.tradexcelsior.config;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class MongoConfig {

//  @Value("${spring.data.mongodb.host}")
//  private String host;
//
//  @Value("${spring.data.mongodb.port}")
//  private int port;
//
//  @Value("${spring.data.mongodb.database}")
//  private String database;
//
//  @Bean
//  public MongoClient mongoClient() {
//    // MongoDB client is created using the host, port, and database
//    String uri = "mongodb://" + host + ":" + port;
//    return MongoClients.create(uri);  // You can modify this if you need authentication or SSL
//  }
//
//  @Bean
//  public MongoDatabase mongoDatabase(MongoClient mongoClient) {
//    return mongoClient.getDatabase(database); // Get the database from the MongoClient
//  }

//  @Bean
//  public GridFSBucket gridFSBucket(MongoDatabase mongoDatabase) {
//    return GridFSBuckets.create(mongoDatabase); // Create the GridFSBucket for interacting with GridFS
//  }

  @Bean
  public GridFSBucket gridFSBucket(MongoTemplate mongoTemplate) {
    return GridFSBuckets.create(mongoTemplate.getDb()); // Creates a GridFSBucket from MongoTemplate
  }
}