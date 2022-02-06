package com.jakubartlomiej.vertxRecruitmentTask.configuration;

import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;

@Slf4j
public class ConfigLoader {
  public static final String SERVER_PORT = "SERVER_PORT";
  public static final String DB_HOST = "DB_HOST";
  public static final String DB_PORT = "DB_PORT";
  public static final String DB_NAME = "DB_NAME";
  public static final String JWT_SECRET = "JWT_SECRET";
  public static final String JWT_ALGORITHM = "JWT_ALGORITHM";
  public static final String JWT_EXPIRES_IN_MINUTES = "JWT_EXPIRES_IN_MINUTES";
  private static final List<String> EXPOSED_JSON_VARIABLES = Arrays.asList(SERVER_PORT, DB_HOST, DB_PORT, DB_NAME
    , JWT_SECRET, JWT_ALGORITHM, JWT_EXPIRES_IN_MINUTES);

  public static Future<BrokerConfig> load(Vertx vertx) {
    final var exposedKeys = new JsonArray();
    EXPOSED_JSON_VARIABLES.forEach(exposedKeys::add);
    ConfigStoreOptions configStoreOptions = new ConfigStoreOptions()
      .setType("file")
      .setFormat("json")
      .setConfig(new JsonObject().put("path", "config/config.json"));
    ConfigRetriever configRetriever = ConfigRetriever.create(vertx,
      new ConfigRetrieverOptions()
        .addStore(configStoreOptions));
    return configRetriever.getConfig().map(BrokerConfig::from);
  }
}
