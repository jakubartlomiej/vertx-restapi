package com.jakubartlomiej.vertxRecruitmentTask.configuration;

import io.vertx.core.json.JsonObject;
import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class BrokerConfig {
  int serverPort;
  String dbHost;
  int dbPort;
  String dbName;
  String jwtSecret;
  String jwtAlgorithm;
  int jwtExpiresInMinutes;

  public static BrokerConfig from(JsonObject config) {
    return BrokerConfig.builder()
      .serverPort(config.getInteger(ConfigLoader.SERVER_PORT))
      .dbHost(config.getString(ConfigLoader.DB_HOST))
      .dbPort(config.getInteger(ConfigLoader.DB_PORT))
      .dbName(config.getString(ConfigLoader.DB_NAME))
      .jwtSecret(config.getString(ConfigLoader.JWT_SECRET))
      .jwtAlgorithm(config.getString(ConfigLoader.JWT_ALGORITHM))
      .jwtExpiresInMinutes(config.getInteger(ConfigLoader.JWT_EXPIRES_IN_MINUTES))
      .build();
  }
}
