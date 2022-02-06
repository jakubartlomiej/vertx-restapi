package com.jakubartlomiej.vertxRecruitmentTask.restapi;

import com.jakubartlomiej.vertxRecruitmentTask.service.UserService;
import io.vertx.ext.web.RoutingContext;

public class UserRestApi {

  public static void checkLoginAvailable(RoutingContext routingContext) {
    UserService.checkIfLoginAvailable(routingContext);
  }

  public static void register(RoutingContext routingContext) {
    UserService.save(routingContext);
  }

  public static void checkLoginAndPassword(RoutingContext routingContext) {
    UserService.checkLoginAndPassword(routingContext);
  }

  public static void validateFieldLoginAndPassword(RoutingContext routingContext) {
    UserService.validateFieldLoginAndPassword(routingContext);
  }
}
