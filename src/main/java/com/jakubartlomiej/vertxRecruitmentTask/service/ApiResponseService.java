package com.jakubartlomiej.vertxRecruitmentTask.service;

import io.vertx.core.AsyncResult;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import java.util.List;

import static com.jakubartlomiej.vertxRecruitmentTask.verticle.WebVerticle.jwtAuth;

public class ApiResponseService {
  public static final int HTTP_STATUS_OK = 200;
  public static final int HTTP_STATUS_CREATED = 201;
  public static final int HTTP_STATUS_BAD_REQUEST = 400;
  public static final int HTTP_STATUS_UNAUTHORIZED = 401;


  public static void responseHandler(RoutingContext routingContext, int httpStatusCode, String message) {
    JsonObject response = new JsonObject().put("message", message);
    routingContext.response()
      .putHeader("content-type", "application/json")
      .setStatusCode(httpStatusCode)
      .end(response.encodePrettily());
  }

  public static void responseHandler(RoutingContext routingContext, int httpStatusCode) {
    routingContext.response()
      .putHeader("content-type", "application/json")
      .setStatusCode(httpStatusCode)
      .end();
  }

  public static void responseHandlerUserItems(RoutingContext routingContext, AsyncResult<List<JsonObject>> asyncResult) {
    List<JsonObject> jsonArray = asyncResult.result();
    JsonObject response = new JsonObject().put("items", jsonArray);
    routingContext.response()
      .putHeader("content-type", "application/json")
      .setStatusCode(HTTP_STATUS_OK)
      .end(response.encodePrettily());
  }

  public static void responseHandlerSendToken(RoutingContext routingContext, String userUUID) {
    JsonObject token = new JsonObject().put("token:", jwtAuth.generateToken(
      new JsonObject()
        .put("UUID", userUUID)));
    routingContext.response()
      .putHeader("content-type", "application/json")
      .setStatusCode(HTTP_STATUS_OK)
      .end(token.encodePrettily());
  }
}
