package com.jakubartlomiej.vertxRecruitmentTask.service;

import io.vertx.ext.web.RoutingContext;

import static com.jakubartlomiej.vertxRecruitmentTask.service.ApiResponseService.HTTP_STATUS_BAD_REQUEST;
import static com.jakubartlomiej.vertxRecruitmentTask.service.ApiResponseService.responseHandler;

public class ApiRequestService {
  public static void validatedBodyRequest(RoutingContext routingContext) {
    if (routingContext.getBody() != null) {
      routingContext.next();
    } else {
      responseHandler(routingContext, HTTP_STATUS_BAD_REQUEST);
    }
  }
}
