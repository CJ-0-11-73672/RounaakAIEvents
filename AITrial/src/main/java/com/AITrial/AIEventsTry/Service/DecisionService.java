
package com.AITrial.AIEventsTry.Service;

import com.AITrial.AIEventsTry.Entities.AIEntities;
import com.AITrial.AIEventsTry.Entities.AIResponse;

public interface DecisionService {
  Map<String, Object> evaluate(Map<String, Object> request);
}
