
package com.AITrial.AIEventsTry.Entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class AIResponse {
 private double probability;
 private String EventType;
}
