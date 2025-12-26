package cteam.planit.main.services;

import cteam.planit.main.dto.AIRequestDTO;
import cteam.planit.main.dto.AIResponseDTO;
import cteam.planit.main.entity.Accommodation;
import cteam.planit.main.repository.AccommodationRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

    @Value("${spring.ai.model:gemini-2.0-flash-lite}")
    private String modelName;

    private final WebClient.Builder webClientBuilder;
    private final AccommodationRepository accommodationRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/{model}:generateContent?key={key}";

    // 1. Intent Extraction System Prompt
    private static final String INTENT_SYSTEM_PROMPT = """
                 당신은 여행 쿼리 분석기입니다. 사용자의 질문을 분석하여 다음 JSON 형식으로 추출하세요.
                 응답은 오직 JSON만 반환해야 합니다. 마크다운 코드 블록(```json 등)을 사용하지 마세요.

                 {
                   "areaCode": "지역코드 (서울=1, 인천=2, 대전=3, 대구=4, 광주=5, 부산=6, 울산=7, 세종=8, 경기=31, 강원=32, 충북=33, 충남=34, 경북=35, 경남=36, 전북=37, 전남=38, 제주=39)",
                   "categories": ["카테고리 (HOTEL, CONDO, PENSION, MOTEL, GUESTHOUSE, HANOK, CAMPING, GLAMPING)"],
                   "minPrice": 숫자 (원 단위, 없으면 null),
                   "maxPrice": 숫자 (원 단위, 없으면 null),
                   "isSearchQuery": boolean (숙소/장소 검색 의도가 있으면 true, 아니면 false)
                 }

                 예시:
                 "제주도 10만원대 펜션 추천해줘" -> {"areaCode": "39", "categories": ["PENSION"], "minPrice": 100000, "maxPrice": 200000, "isSearchQuery": true}
                 "서울 호텔 50만원 이하" -> {"areaCode": "1", "categories": ["HOTEL"], "maxPrice": 500000, "isSearchQuery": true}
                 "안녕하세요" -> {"isSearchQuery": false}
            """;

    // 2. Final Response System Prompt
    private static final String RESPONSE_SYSTEM_PROMPT = """
                당신은 전문적인 'AI 여행 비서'입니다.
                사용자의 질문과 검색된 숙소 정보를 바탕으로 친절하고 유용한 답변을 제공하세요.

                규칙:
                1. 검색된 숙소가 있다면 그 중 상위 3~5개를 구체적으로 언급하며 추천 이유를 설명하세요.
                2. 검색 결과가 없다면 "조건에 맞는 숙소를 찾지 못했습니다"라고 솔직하게 말하고, 대안을 제시하세요.
                3. 마크다운을 사용하여 가독성을 높이세요 (볼드체, 리스트, 이모지 활용).
                4. 사용자가 지도에서 위치를 확인할 수 있다는 멘트를 마지막에 덧붙여주세요. (예: "지도에서 위치를 확인해보세요!")
                5. 가격 정보가 있다면 "약 X만원" 형태로 언급하세요.
            """;

    public Mono<AIResponseDTO> getChatResponse(AIRequestDTO requestDTO) {
        // 1. Intent Extraction
        return extractIntent(requestDTO.getMessage())
                .flatMap(intentJson -> {
                    try {
                        JsonNode intent = objectMapper.readTree(intentJson);
                        boolean isSearchQuery = intent.path("isSearchQuery").asBoolean(false);

                        if (!isSearchQuery) {
                            // 단순 대화인 경우 바로 일반 응답 생성
                            return generateSimpleResponse(requestDTO.getMessage());
                        }

                        // 2. DB Search
                        String areaCode = intent.has("areaCode") ? intent.get("areaCode").asText() : null;
                        List<String> categories = new ArrayList<>();
                        if (intent.has("categories")) {
                            for (JsonNode cat : intent.get("categories")) {
                                String mapped = mapCategory(cat.asText());
                                if (mapped != null)
                                    categories.add(mapped);
                            }
                        }
                        Integer minPrice = intent.has("minPrice") && !intent.get("minPrice").isNull()
                                ? intent.get("minPrice").asInt()
                                : null;
                        Integer maxPrice = intent.has("maxPrice") && !intent.get("maxPrice").isNull()
                                ? intent.get("maxPrice").asInt()
                                : null;

                        List<String> areaCodes = (areaCode != null && !areaCode.isEmpty()) ? List.of(areaCode) : null;
                        List<String> cat3List = categories.isEmpty() ? null : categories;

                        Page<Accommodation> searchResult = accommodationRepository.findWithFilters(
                                areaCodes, cat3List, minPrice, maxPrice, PageRequest.of(0, 5) // Top 5
                        );

                        List<Accommodation> accommodations = searchResult.getContent();

                        // 3. Final Response Generation
                        return generateFinalResponse(requestDTO.getMessage(), accommodations, intentJson);

                    } catch (Exception e) {
                        e.printStackTrace();
                        return generateSimpleResponse(requestDTO.getMessage());
                    }
                });
    }

    private Mono<String> extractIntent(String userMessage) {
        String prompt = INTENT_SYSTEM_PROMPT + "\n\nUser Question: " + userMessage;
        return callGemini(prompt).map(this::cleanJsonString);
    }

    private String cleanJsonString(String response) {
        // Markdown code block 제거 (```json ... ```)
        if (response.startsWith("```")) {
            response = response.replaceAll("^```[a-z]*\\n", "").replaceAll("\\n```$", "");
        }
        return response.trim();
    }

    private Mono<AIResponseDTO> generateFinalResponse(String userMessage, List<Accommodation> accommodations,
            String intentJson) {
        StringBuilder context = new StringBuilder();
        context.append("User Question: ").append(userMessage).append("\n\n");
        context.append("Extracted Intent: ").append(intentJson).append("\n\n");
        context.append("Search Results:\n");

        if (accommodations.isEmpty()) {
            context.append("No accommodations found matching the criteria.\n");
        } else {
            for (Accommodation acc : accommodations) {
                context.append(String.format("- [%s] %s (Price: %s, Area: %s)\n",
                        acc.getTitle(), acc.getAddr1(),
                        acc.getMinPrice() != null ? acc.getMinPrice() : "Unknown",
                        acc.getAreacode()));
            }
        }

        String prompt = RESPONSE_SYSTEM_PROMPT + "\n\nContext:\n" + context.toString();

        return callGemini(prompt).map(responseText -> {
            AIResponseDTO responseDTO = new AIResponseDTO();
            responseDTO.setResponse(responseText);
            responseDTO.setRecommendations(accommodations);
            return responseDTO;
        });
    }

    private Mono<AIResponseDTO> generateSimpleResponse(String userMessage) {
        Map<String, Object> request = new HashMap<>();
        List<Map<String, Object>> contents = new ArrayList<>();
        Map<String, Object> userContent = new HashMap<>();
        userContent.put("role", "user");
        userContent.put("parts", List.of(Map.of("text", RESPONSE_SYSTEM_PROMPT + "\n\nUser: " + userMessage)));
        contents.add(userContent);
        request.put("contents", contents);

        return webClientBuilder.build()
                .post()
                .uri(GEMINI_API_URL, modelName, apiKey)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(String.class)
                .map(this::parseGeminiResponse)
                .map(text -> {
                    AIResponseDTO dto = new AIResponseDTO();
                    dto.setResponse(text);
                    dto.setRecommendations(new ArrayList<>()); // Empty list
                    return dto;
                });
    }

    private Mono<String> callGemini(String prompt) {
        Map<String, Object> request = new HashMap<>();
        List<Map<String, Object>> contents = new ArrayList<>();
        Map<String, Object> userContent = new HashMap<>();
        userContent.put("role", "user");
        userContent.put("parts", List.of(Map.of("text", prompt)));
        contents.add(userContent);
        request.put("contents", contents);

        return webClientBuilder.build()
                .post()
                .uri(GEMINI_API_URL, modelName, apiKey)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(String.class)
                .map(this::parseGeminiResponse);
    }

    private String mapCategory(String aiCategory) {
        if (aiCategory == null)
            return null;
        switch (aiCategory.toUpperCase()) {
            case "HOTEL":
                return "B02010100";
            case "CONDO":
                return "B02010200";
            case "PENSION":
                return "B02010400";
            case "MOTEL":
                return "B02010500";
            case "GUESTHOUSE":
                return "B02010700";
            case "HANOK":
                return "B02011000";
            case "CAMPING":
                return "B02011100";
            case "GLAMPING":
                return "B02011200";
            default:
                return null;
        }
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
            return "";
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
