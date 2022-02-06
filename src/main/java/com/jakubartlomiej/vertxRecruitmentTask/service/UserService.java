package com.jakubartlomiej.vertxRecruitmentTask.service;

import com.jakubartlomiej.vertxRecruitmentTask.model.User;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;

import java.util.UUID;

import static com.jakubartlomiej.vertxRecruitmentTask.service.ApiResponseService.*;
import static com.jakubartlomiej.vertxRecruitmentTask.verticle.WebVerticle.mongoClient;

@Slf4j
public class UserService {
  private static final String MONGO_USERS_COLLECTION = "users";

  public static void checkIfLoginAvailable(RoutingContext routingContext) {
    String login = routingContext.getBodyAsJson().getString("login");
    JsonObject document = new JsonObject().put("login", login);
    mongoClient.findOne(MONGO_USERS_COLLECTION, document, null, asyncResult -> {
      if (asyncResult.succeeded()) {
        if (asyncResult.result() == null) {
          routingContext.next();
        } else {
          String message = "Login is not available";
          responseHandler(routingContext, HTTP_STATUS_BAD_REQUEST, message);
          log.info("Login is not available {}", login);
        }
      }
    });
  }

  public static void checkLoginAndPassword(RoutingContext routingContext) {
    String login = routingContext.getBodyAsJson().getString("login");
    String password = routingContext.getBodyAsJson().getString("password");
    JsonObject document = new JsonObject()
      .put("login", login);
    mongoClient.findOne(MONGO_USERS_COLLECTION, document, null, asyncResult -> {
      if (asyncResult.succeeded()) {
        if (asyncResult.result() != null) {
          String passwordFromDb = asyncResult.result().getString("password");
          if (BCrypt.checkpw(password, passwordFromDb)) {
            routingContext.next();
            log.info("Login successful {}", login);
          } else {
            errorLoginOrPasswordHandler(routingContext, login);
          }
        } else {
          errorLoginOrPasswordHandler(routingContext, login);
        }
      } else {
        errorLoginOrPasswordHandler(routingContext, login);
      }
    });
  }

  private static void errorLoginOrPasswordHandler(RoutingContext routingContext, String login) {
    String message = "Incorrect login or password";
    responseHandler(routingContext, HTTP_STATUS_BAD_REQUEST, message);
    log.info("Login error - user not found {}", login);
  }

  public static void save(RoutingContext routingContext) {
    String login = routingContext.getBodyAsJson().getString("login");
    String password = routingContext.getBodyAsJson().getString("password");
    User user = new User();
    user.setId(UUID.randomUUID());
    user.setLogin(login);
    user.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));
    JsonObject document = new JsonObject(Json.encode(user));
    mongoClient.save(MONGO_USERS_COLLECTION, document, asyncResult -> {
      if (asyncResult.succeeded()) {
        responseHandler(routingContext, HTTP_STATUS_CREATED);
        log.info("Saved user {} with id: {}", user.getLogin(), user.getId());
      } else {
        responseHandler(routingContext, HTTP_STATUS_BAD_REQUEST);
        log.info("User save failed {}", user.getLogin());
      }
    });
  }

  public static void validateFieldLoginAndPassword(RoutingContext routingContext) {
    try {
      JsonObject body = routingContext.getBodyAsJson();
      if (body.containsKey("login") && body.containsKey("password")) {
        if (body.getString("login"). length() > 4 && body.getString("password").length() > 4) {
          routingContext.next();
        } else {
          responseHandler(routingContext,HTTP_STATUS_BAD_REQUEST, "Login and password must be min 5 length");
          log.info("Invalidate password or login");
        }
      } else {
        responseHandler(routingContext, HTTP_STATUS_BAD_REQUEST);
        log.info("Invalidate password or login");
      }
    } catch (Exception e) {
      responseHandler(routingContext, HTTP_STATUS_BAD_REQUEST);
      log.warn(e.getMessage());
    }
  }
}
