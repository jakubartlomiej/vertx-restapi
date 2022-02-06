package com.jakubartlomiej.vertxRecruitmentTask.configuration;

import io.vertx.ext.auth.JWTOptions;
import io.vertx.ext.auth.PubSecKeyOptions;
import io.vertx.ext.auth.jwt.JWTAuthOptions;

public class JwtConfig {
  public static JWTAuthOptions getJwtConfig(BrokerConfig configuration) {
    return new JWTAuthOptions()
      .addPubSecKey(new PubSecKeyOptions()
        .setAlgorithm(configuration.getJwtAlgorithm())
        .setBuffer(configuration.getJwtSecret()))
      .setJWTOptions(new JWTOptions()
        .setExpiresInMinutes(configuration.getJwtExpiresInMinutes()));
  }
}
