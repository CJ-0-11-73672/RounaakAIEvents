package com.AITrial.AIEventsTry.Controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.AITrial.AIEventsTry.Entities.AIEntities;
import com.AITrial.AIEventsTry.Entities.AIResponse;
import com.AITrial.AIEventsTry.Service.DecisionService;
import java.util.Map;
@RequestMapping("/api/ai")
@RestController
public class AIController {
  private final DecisionService decisionService;
  public AIController(DecisionService decisionService){
    this.decisionService = decisionService;
  }
   @PostMapping("/decision")
public Map<String, Object> getDecision(@RequestBody Map<String, Object> request) {

    System.out.println("Incoming Request: " + request);

    return decisionService.evaluate(request);
}
}
