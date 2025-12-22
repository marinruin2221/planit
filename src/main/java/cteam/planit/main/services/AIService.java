package cteam.planit.main.services;

import cteam.planit.main.dto.AIRequestDTO;
import cteam.planit.main.dto.AIResponseDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AIService {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${spring.ai.model:gemini-2.5-flash-lite}")
    private String modelName;

    private final WebClient.Builder webClientBuilder;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/{model}:generateContent?key={key}";
    private static final String SYSTEM_PROMPT = """
        당신은 전문적인 'AI 여행 비서'입니다. 다음 두 가지 핵심 기능을 수행합니다.

        1. **지능형 여행 일정 플래너 (Itinerary Generator)**:
           - 사용자의 입력(목적지, 기간, 취향, 예산 등)을 바탕으로 상세 일정을 생성합니다.
           - 정보가 부족하면 친절하게 되물어주세요 (예: "몇 박 며칠 일정인가요?", "선호하는 여행 스타일이 있으신가요?").
           - "아이와 함께하는", "가성비", "힐링" 등 구체적인 요구사항을 반영하여 경로를 제안합니다.
           - 이동 시간과 경로를 고려하여 현실적인 일정을 짜주세요.

        2. **대화형 여행 검색 및 추천 (Conversational Search)**:
           - 사용자가 자연어로 숙소나 장소를 찾을 때 최적의 추천을 제공합니다.
           - 예: "조용히 책 읽기 좋은 부산 호텔", "강릉의 뷰 좋은 카페"
           - 추천 이유와 주요 특징을 함께 설명해주세요.

        **응답 스타일**:
        - 친절하고 전문적인 톤을 유지하세요.
        - 마크다운(Markdown)을 사용하여 가독성 좋게 출력하세요 (볼드체, 리스트 등 활용).
        - 이모지를 적절히 사용하여 생동감을 주세요.
    """;

    public Mono<AIResponseDTO> getChatResponse(AIRequestDTO requestDTO) {
        return webClientBuilder.build()
                .post()
                .uri(GEMINI_API_URL, modelName, apiKey)
                .bodyValue(createGeminiRequest(requestDTO))
                .retrieve()
                .bodyToMono(String.class)
                .map(this::parseGeminiResponse)
                .map(text -> {
                    AIResponseDTO response = new AIResponseDTO();
                    response.setResponse(text);
                    return response;
                });
    }

    private Map<String, Object> createGeminiRequest(AIRequestDTO requestDTO) {
        Map<String, Object> request = new HashMap<>();
        List<Map<String, Object>> contents = new ArrayList<>();

        if (requestDTO.getHistory() != null && !requestDTO.getHistory().isEmpty()) {
             for (AIRequestDTO.MessagePart part : requestDTO.getHistory()) {
                 Map<String, Object> content = new HashMap<>();
                 content.put("role", part.getRole());
                 content.put("parts", List.of(Map.of("text", part.getText())));
                 contents.add(content);
             }
        }
        
        String finalMessage = SYSTEM_PROMPT + "\n\n사용자 메시지: " + requestDTO.getMessage();
        
        Map<String, Object> userContent = new HashMap<>();
        userContent.put("role", "user");
        userContent.put("parts", List.of(Map.of("text", finalMessage)));
        contents.add(userContent);

        request.put("contents", contents);
        
        Map<String, Object> generationConfig = new HashMap<>();
        generationConfig.put("maxOutputTokens", 1000);
        request.put("generationConfig", generationConfig);

        return request;
    }

    private String parseGeminiResponse(String jsonResponse) {
        try {
            JsonNode root = objectMapper.readTree(jsonResponse);
            JsonNode candidates = root.path("candidates");
            if (candidates.isArray() && candidates.size() > 0) {
                JsonNode content = candidates.get(0).path("content");
                JsonNode parts = content.path("parts");
                if (parts.isArray() && parts.size() > 0) {
                    return parts.get(0).path("text").asText();
                }
            }
            return "죄송합니다. 답변을 생성하는 데 문제가 발생했습니다.";
        } catch (Exception e) {
            e.printStackTrace();
            return "오류가 발생했습니다: " + e.getMessage();
        }
    }
}
