package com.jakubartlomiej.vertxRecruitmentTask.verticle;

import com.jakubartlomiej.vertxRecruitmentTask.configuration.BrokerConfig;
import com.jakubartlomiej.vertxRecruitmentTask.configuration.ConfigLoader;
import com.jakubartlomiej.vertxRecruitmentTask.configuration.JwtConfig;
import com.jakubartlomiej.vertxRecruitmentTask.configuration.MongoConfig;
import com.jakubartlomiej.vertxRecruitmentTask.restapi.ItemRestApi;
import com.jakubartlomiej.vertxRecruitmentTask.restapi.UserRestApi;
import com.jakubartlomiej.vertxRecruitmentTask.service.ApiRequestService;
import com.jakubartlomiej.vertxRecruitmentTask.service.JwtTokenService;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class WebVerticle extends AbstractVerticle {
  public static MongoClient mongoClient;
  public static JWTAuth jwtAuth;

  @Override
  public void start(Promise<Void> startPromise) {
    ConfigLoader.load(vertx)
      .onFailure(startPromise::fail)
      .onSuccess(configuration -> {
        log.info("Retrieved configuration: {}", configuration);
        startHttpServerAndRouter(startPromise, configuration);
        JsonObject configMongo = MongoConfig.getDbConfig(configuration);
        mongoClient = MongoClient.create(vertx, configMongo);
        jwtAuth = JWTAuth.create(vertx, JwtConfig.getJwtConfig(configuration));
      });
  }

  private void startHttpServerAndRouter(Promise<Void> startPromise, BrokerConfig configuration) {
    Router router = getRouter();
    vertx.createHttpServer()
      .requestHandler(router)
      .listen(configuration.getServerPort(), http -> {
        if (http.succeeded()) {
          startPromise.complete();
          log.info("HTTP server started on port {}", configuration.getServerPort());
        } else {
          startPromise.fail("Unable to load config");
          log.info("Unable to load config");
        }
      });
  }

  private Router getRouter() {
    Router router = Router.router(vertx);
    router.route("/*").handler(BodyHandler.create());
    router.post("/login")
      .handler(ApiRequestService::validatedBodyRequest)
      .handler(UserRestApi::validateFieldLoginAndPassword)
      .handler(UserRestApi::checkLoginAndPassword)
      .handler(JwtTokenService::sendToken);
    router.post("/register")
      .handler(ApiRequestService::validatedBodyRequest)
      .handler(UserRestApi::validateFieldLoginAndPassword)
      .handler(UserRestApi::checkLoginAvailable)
      .handler(UserRestApi::register);
    router.post("/items")
      .handler(ApiRequestService::validatedBodyRequest)
      .handler(ItemRestApi::validateFieldItemName)
      .handler(JwtTokenService::jwtAuthenticateHandler)
      .handler(ItemRestApi::addItem);
    router.get("/items")
      .handler(JwtTokenService::jwtAuthenticateHandler)
      .handler(ItemRestApi::findByOwner);
    return router;
  }
}
