package com.AITrial.AIEventsTry.Service.ServiceImpl;

import com.AITrial.AIEventsTry.Service.DecisionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class DecisionAIServiceImpl implements DecisionService {

    private final RestTemplate restTemplate;

    @Value("${groq.api.key}")
    private String groqApiKey;

    public DecisionAIServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public Map<String, Object> evaluate(Map<String, Object> request) {

        try {
            return callAI(request);
        } catch (Exception e) {
            System.out.println("AI call failed: " + e.getMessage());
            return fallbackResponse();
        }
    }

    // MAIN AI CALL
    private Map<String, Object> callAI(Map<String, Object> request) {

        String url = "https://api.groq.com/openai/v1/chat/completions";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(groqApiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        ObjectMapper mapper = new ObjectMapper();

        // Extract ATTRIBUTES (dynamic)
        Map<String, Object> attributes =
                (Map<String, Object>) request.get("attributes");

        String attributesJson = "{}";
        try {
            if (attributes != null) {
                attributesJson = mapper.writeValueAsString(attributes);
            }
        } catch (Exception e) {
            System.out.println("Attribute parsing failed");
        }

        // Extract EVENTS (dynamic)
        List<Map<String, Object>> events =
                (List<Map<String, Object>>) request.get("events");

        StringBuilder sb = new StringBuilder();

        if (events != null && !events.isEmpty()) {

            int start = Math.max(0, events.size() - 5); // last 5 events

            for (int i = start; i < events.size(); i++) {

                Map<String, Object> e = events.get(i);

                String eventType = Objects.toString(e.getOrDefault("eventType", e.get("EventType")),
        "Unknown");
                String timestamp = Objects.toString(
        e.getOrDefault("timestamp", e.get("Timestamp")),
        ""
);

                sb.append(eventType)
                  .append(" at ")
                  .append(timestamp)
                  .append(", ");
            }
        }

        String journey = sb.toString();

        // Build STRONG prompt
        String prompt = """
Return ONLY valid JSON.

STRICT FORMAT:
{
  "probability": number,
  "eventType": string
}

Rules:
- Do NOT explain
- Do NOT include markdown
- Do NOT include multiple JSON objects
- Do NOT include comments
- Output must be a single JSON object only

User journey:
""" + journey;

        // Build request body
        Map<String, Object> body = Map.of(
                "model", "llama-3.1-8b-instant",
                "temperature", 0.0,
                "messages", List.of(
                        Map.of(
                                "role", "system",
                                "content", "You are a strict JSON API. Return ONLY valid JSON."
                        ),
                        Map.of(
                                "role", "user",
                                "content", prompt
                        )
                )
        );

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<Map> responseEntity =
                restTemplate.postForEntity(url, entity, Map.class);

        Map response = responseEntity.getBody();
        if (response == null || response.get("choices") == null) {
            System.out.println("Invalid AI response, using fallback");
            return fallbackResponse();
           }
        // Extract AI response
        List choices = (List) response.get("choices");
        Map firstChoice = (Map) choices.get(0);
        Map message = (Map) firstChoice.get("message");

        String aiText = (String) message.get("content");

        System.out.println("AI Raw Response: " + aiText);
        
// 🔥 Extract ONLY JSON (BEST METHOD)
int start = aiText.indexOf("{");
int end = aiText.lastIndexOf("}");

String json = "{}";

if (start != -1 && end != -1 && end > start) {
    json = aiText.substring(start, end + 1);
} else {
    System.out.println("Invalid JSON from AI, using fallback");
}

System.out.println("Extracted JSON: " + json);

// Parse AI output safely
double probability = 0.5;
String eventType = "web.webpagedetails.pageViews";

try {
    Map<String, Object> aiResult =
            mapper.readValue(json, Map.class);

    if (aiResult.get("probability") != null) {
        probability = Double.parseDouble(
                aiResult.get("probability").toString()
        );

        // clamp
        if (probability < 0) probability = 0;
        if (probability > 1) probability = 1;
    }

    Object eventObj = aiResult.get("eventType");

    if (eventObj == null) {
        eventObj = aiResult.get("EventType"); // fallback
    }

    if (eventObj != null) {
        eventType = eventObj.toString();
    }

} catch (Exception e) {
    System.out.println("Parsing failed, using fallback");
}

        // Final response
        Map<String, Object> output = new HashMap<>();
        output.put("probability", probability);
        output.put("eventType", eventType);

        return output;
    }

    // FALLBACK
    private Map<String, Object> fallbackResponse() {

        Map<String, Object> fallback = new HashMap<>();
        fallback.put("probability", 0.5);
        fallback.put("eventType", "web.webpagedetails.pageViews");

        return fallback;
    }
}
