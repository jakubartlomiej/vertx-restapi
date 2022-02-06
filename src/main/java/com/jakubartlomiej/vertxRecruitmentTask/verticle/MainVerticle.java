package com.jakubartlomiej.vertxRecruitmentTask.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MainVerticle extends AbstractVerticle {

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.exceptionHandler(error ->
      log.error("Unhandled:", error));
    vertx.deployVerticle(new MainVerticle())
      .onFailure(error -> log.error("Failed to deploy:", error))
      .onSuccess(id -> {
        log.info("Deployed {} with id {}", MainVerticle.class.getSimpleName(), id);
      });
  }

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    vertx.deployVerticle(WebVerticle.class.getName())
      .onFailure(startPromise::fail)
      .onSuccess(id -> {
        log.info("Deployed {} with id {}", WebVerticle.class.getSimpleName(), id);
      });
  }
}
