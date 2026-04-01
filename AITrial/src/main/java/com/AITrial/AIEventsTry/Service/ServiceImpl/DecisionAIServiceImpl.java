package com.AITrial.AIEventsTry.Service.ServiceImpl;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import java.util.*;

import com.AITrial.AIEventsTry.Entities.AIEntities;
import com.AITrial.AIEventsTry.Entities.AIResponse;
import com.AITrial.AIEventsTry.Entities.Events;
import com.AITrial.AIEventsTry.Entities.Events;
import com.AITrial.AIEventsTry.Service.DecisionService;

import org.springframework.beans.factory.annotation.Value;

@Service
public class DecisionAIServiceImpl implements DecisionService {

    private final RestTemplate restTemplate;

    @Value("${groq.api.key}")
    private String groqApiKey;

    public DecisionAIServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public AIResponse evaluate(AIEntities request) {

        try {
            return callAI(request);
        } catch (Exception e) {
            System.out.println("AI call failed: " + e.getMessage());
            return fallbackResponse(request);
        }
    }

    // MAIN AI CALL
    private AIResponse callAI(AIEntities request) {
        System.out.println("Calling Groq AI");
        System.out.println("API Key: "+ groqApiKey);
        String url = "https://api.groq.com/openai/v1/chat/completions";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + groqApiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Build event sequence string
        StringBuilder sb = new StringBuilder();
    if(request.getEvents() != null){
        for (Events e : request.getEvents()) {
            sb.append(e.getEventType())
              .append(" at ")
              .append(e.getTimestamp())
              .append(", ");
        }
    }
    else{
        System.out.println("No events recieved");
    }
        

        // Strong prompt for Groq
        String prompt = "User journey: " + sb.toString() +
               ". Predict next event type probability."+
               "Allowed values EventType: Purchase, Browse, WishList, Abandon."+
               "Return Only Json like:{\"probability\": 0.75,\"EventType\":\"Pruchase\"}";

        Map<String, Object> body = Map.of(
                "model", "llama-3.1-8b-instant",
                "messages", List.of(
                        Map.of(
                                "role", "user",
                                "content", prompt
                        )
                )
        );

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<Map> responseEntity = restTemplate.postForEntity(url, entity, Map.class);
        System.out.println("Status Code: "+responseEntity.getStatusCode());
        System.out.println("ResponseBody: "+responseEntity.getBody());
        Map response = responseEntity.getBody();
        List choices = (List) response.get("choices");
        Map firstChoice = (Map) choices.get(0);
        Map message = (Map) firstChoice.get("message");

        String aiText = (String) message.get("content");

        System.out.println("Groq Response: " + aiText);

        //  For now, return raw response in reason
        return AIResponse.builder()
                .probability(0.5) // placeholder
                .EventType(aiText)
                .build();
    }

    // FALLBACK (IMPORTANT)
    private AIResponse fallbackResponse(AIEntities request) {

        return AIResponse.builder()
                .probability(0.5)
                .EventType(null)
                .build();
    }
}
