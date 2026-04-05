
package com.AITrial.AIEventsTry.Service;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.AITrial.AIEventsTry.Entities.AIEntities;
import com.AITrial.AIEventsTry.Entities.AIResponse;

public interface DecisionService {
   CompletableFuture< Map<String, Object>> evaluateAsync(Map<String, Object> request);
}
