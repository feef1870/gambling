package org.example.backend.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.backend.dto.DealerCommentResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Map;

@Slf4j
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

            DealerCommentResponse response = restClient.post()
                    .uri("/api/dealer/comment")
                    .body(payload)
                    .retrieve()
                    .body(DealerCommentResponse.class);

            return response != null ? response.comment() : null;

        } catch (RestClientException e) {
            log.warn("AI dealer request failed", e);
            return null;
        }
    }
}
