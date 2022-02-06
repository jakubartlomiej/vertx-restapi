package com.jakubartlomiej.vertxRecruitmentTask.service;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import lombok.extern.slf4j.Slf4j;

import static com.jakubartlomiej.vertxRecruitmentTask.verticle.WebVerticle.jwtAuth;
import static com.jakubartlomiej.vertxRecruitmentTask.verticle.WebVerticle.mongoClient;
import static com.jakubartlomiej.vertxRecruitmentTask.service.ApiResponseService.*;

@Slf4j
public class JwtTokenService {

  public static void sendToken(RoutingContext routingContext) {
    String login = routingContext.getBodyAsJson().getString("login");
    JsonObject document = new JsonObject()
      .put("login", login);
    mongoClient.findOne("users", document, null, asyncResult -> {
      if (asyncResult.succeeded()) {
        if (asyncResult.result() != null) {
          String userUUID = asyncResult.result().getString("id");
          responseHandlerSendToken(routingContext, userUUID);
        } else {
          responseHandler(routingContext, HTTP_STATUS_BAD_REQUEST);
          log.info("Failed create token");
        }
      }
    });
  }

  public static void jwtAuthenticateHandler(RoutingContext routingContext) {
    if (routingContext.request().headers().get("Authorization") != null) {
      String token = routingContext.request().headers().get("Authorization").substring(("Bearer ").length());
      jwtAuth.authenticate(new JsonObject().put("token", token))
        .onSuccess(success -> {
          routingContext.next();
        })
        .onFailure(err -> {
          responseHandler(routingContext, HTTP_STATUS_UNAUTHORIZED);
        });
    } else {
      responseHandler(routingContext, HTTP_STATUS_UNAUTHORIZED);
    }
  }
}
