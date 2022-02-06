package com.jakubartlomiej.vertxRecruitmentTask;

import com.jakubartlomiej.vertxRecruitmentTask.verticle.MainVerticle;
import io.vertx.core.Vertx;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(VertxExtension.class)
public class TestWebVerticle {

  @BeforeEach
  void verticle_deployed(Vertx vertx, VertxTestContext testContext) throws Throwable {
    vertx.deployVerticle(new MainVerticle());
  }

  @Test
  void return_all(Vertx vertx, VertxTestContext testContext) throws Throwable {
    WebClient webClient = WebClient.create(vertx);
    webClient.post(3000, "localhost", "/register")
      .putHeader("Content-Type", "application/json")
      .send()
      .onComplete(test -> {
        testContext.completeNow();
      });
  }
}
