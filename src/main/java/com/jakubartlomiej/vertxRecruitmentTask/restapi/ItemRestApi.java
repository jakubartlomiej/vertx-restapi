package com.jakubartlomiej.vertxRecruitmentTask.restapi;

import com.jakubartlomiej.vertxRecruitmentTask.service.ItemService;
import io.vertx.ext.web.RoutingContext;

public class ItemRestApi {

  public static void addItem(RoutingContext routingContext) {
    ItemService.save(routingContext);
  }

  public static void findByOwner(RoutingContext routingContext) {
    ItemService.getItems(routingContext);
  }

  public static void validateFieldItemName(RoutingContext routingContext) {
    ItemService.validateFieldName(routingContext);
  }
}
