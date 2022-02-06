package com.jakubartlomiej.vertxRecruitmentTask.service;

import com.jakubartlomiej.vertxRecruitmentTask.model.Item;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

import static com.jakubartlomiej.vertxRecruitmentTask.service.ApiResponseService.*;
import static com.jakubartlomiej.vertxRecruitmentTask.verticle.WebVerticle.jwtAuth;
import static com.jakubartlomiej.vertxRecruitmentTask.verticle.WebVerticle.mongoClient;

@Slf4j
public class ItemService {
  private static final String MONGO_ITEMS_COLLECTION = "items";

  public static void save(RoutingContext routingContext) {
    String token = routingContext.request().headers().get("Authorization").substring(("Bearer ").length());
    jwtAuth.authenticate(new JsonObject().put("token", token))
      .onSuccess(success -> {
        if (routingContext.getBodyAsJson() != null) {
          String itemName = routingContext.getBodyAsJson().getString("name");
          String owner = success.principal().getString("UUID");
          Item item = new Item();
          item.setId(UUID.randomUUID());
          item.setOwner(UUID.fromString(owner));
          item.setName(itemName);
          JsonObject document = new JsonObject(Json.encode(item));
          mongoClient.save(MONGO_ITEMS_COLLECTION, document, asyncResult -> {
            if (asyncResult.succeeded()) {
              responseHandler(routingContext, HTTP_STATUS_CREATED);
              log.info("Added new item {}!", itemName);
            }
          });
        } else {
          responseHandler(routingContext, HTTP_STATUS_BAD_REQUEST);
          log.info("Request body not correct");
        }
      }).onFailure(failure -> {
      responseHandler(routingContext, HTTP_STATUS_UNAUTHORIZED);
      log.info("Login failed");
    });
  }

  public static void getItems(RoutingContext routingContext) {
    String token = routingContext.request().headers().get("Authorization").substring(("Bearer ").length());
    jwtAuth.authenticate(new JsonObject().put("token", token))
      .onSuccess(success -> {
        String owner = success.principal().getString("UUID");
        JsonObject document = new JsonObject().put("owner", owner);
        mongoClient.find(MONGO_ITEMS_COLLECTION, document, asyncResult -> {
          if (asyncResult.succeeded()) {
            responseHandlerUserItems(routingContext, asyncResult);
            log.info("Item list correct fetch");
          }
        });
      }).onFailure(failure -> {
      responseHandler(routingContext, HTTP_STATUS_UNAUTHORIZED);
      log.info("Login failed");
    });
  }

  public static void validateFieldName(RoutingContext routingContext) {
    try {
      JsonObject body = routingContext.getBodyAsJson();
      if (body.containsKey("name")) {
        if (body.getString("name").length() > 2) {
          routingContext.next();
        } else {
          responseHandler(routingContext, HTTP_STATUS_BAD_REQUEST, "Item name length min 2");
          log.info("Invalidate item name");
        }
      } else {
        responseHandler(routingContext, HTTP_STATUS_BAD_REQUEST);
        log.info("Invalidate item name");
      }
    } catch (Exception e) {
      responseHandler(routingContext, HTTP_STATUS_BAD_REQUEST);
      log.warn(e.getMessage());
    }
  }

}
