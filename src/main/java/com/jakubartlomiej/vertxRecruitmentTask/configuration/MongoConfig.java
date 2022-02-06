package com.jakubartlomiej.vertxRecruitmentTask.configuration;

import io.vertx.core.json.JsonObject;

public class MongoConfig {
  public static JsonObject getDbConfig(BrokerConfig configuration) {
    JsonObject dbConfig = new JsonObject();
    dbConfig.put("host", configuration.getDbHost());
    dbConfig.put("port", configuration.getDbPort());
    dbConfig.put("db_name", configuration.getDbName());
    return dbConfig;
  }
}
