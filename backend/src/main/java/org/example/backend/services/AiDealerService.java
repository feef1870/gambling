package org.example.backend.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AiDealerService {
    private final RestClient restClient;

    public String getDealerComment(String status, int playerTotal, int dealerTotal, int betAmount) {
        if ("IN_PROGRESS".equals(status)) {
            return null;
        }

        try {
            Map<String, Object> payload = Map.of(
                    "status", status,
                    "player_total", playerTotal,
                    "dealer_total", dealerTotal,
                    "bet_amount", betAmount
            );

            Map response = restClient.post()
                    .uri("/api/dealer/comment")
                    .body(payload)
                    .retrieve()
                    .body(Map.class);

            return response != null ? (String) response.get("comment") : "No comment.";

        } catch (Exception e) {
            System.err.println("An error occurred " + e.getMessage());
            return "An error occurred";
        }
    }
}
