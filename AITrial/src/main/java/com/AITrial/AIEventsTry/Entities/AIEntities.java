package com.AITrial.AIEventsTry.Entities;
import java.util.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AIEntities {
   private Map<String, Object> Attributes;
   private List<Events> events;
   
}
