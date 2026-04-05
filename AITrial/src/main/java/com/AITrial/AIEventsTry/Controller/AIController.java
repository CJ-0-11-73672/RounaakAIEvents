package com.AITrial.AIEventsTry.Controller;

import java.util.Map;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.AITrial.AIEventsTry.Entities.AIEntities;
import com.AITrial.AIEventsTry.Entities.AIResponse;
import com.AITrial.AIEventsTry.Service.DecisionService;
@RequestMapping("/api/ai")
@RestController
public class AIController {
  private final DecisionService decisionService;
  public AIController(DecisionService decisionService){
    this.decisionService = decisionService;
  }
 @PostMapping("/decision")
public CompletableFuture<List<Map<String, Object>>> getDecision(
        @RequestBody List<Map<String, Object>> request) {

    System.out.println("Batch Request size: " + request.size());

    List<CompletableFuture<Map<String, Object>>> futures = request.stream()
            .map(req -> decisionService.evaluateAsync(req))
            .toList();

    return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
            .thenApply(v -> futures.stream()
                    .map(CompletableFuture::join)
                    .toList());
}
}
