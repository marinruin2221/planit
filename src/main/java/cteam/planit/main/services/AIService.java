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
        ?뱀떊? ?꾨Ц?곸씤 'AI ?ы뻾 鍮꾩꽌'?낅땲?? ?ㅼ쓬 ??媛吏 ?듭떖 湲곕뒫???섑뻾?⑸땲??

        1. **吏?ν삎 ?ы뻾 ?쇱젙 ?뚮옒??(Itinerary Generator)**:
           - ?ъ슜?먯쓽 ?낅젰(紐⑹쟻吏, 湲곌컙, 痍⑦뼢, ?덉궛 ????諛뷀깢?쇰줈 ?곸꽭 ?쇱젙???앹꽦?⑸땲??
           - ?뺣낫媛 遺議깊븯硫?移쒖젅?섍쾶 ?섎Ъ?댁＜?몄슂 (?? "紐?諛?硫곗튌 ?쇱젙?멸???", "?좏샇?섎뒗 ?ы뻾 ?ㅽ??쇱씠 ?덉쑝?좉???").
           - "?꾩씠? ?④퍡?섎뒗", "媛?깅퉬", "?먮쭅" ??援ъ껜?곸씤 ?붽뎄?ы빆??諛섏쁺?섏뿬 寃쎈줈瑜??쒖븞?⑸땲??
           - ?대룞 ?쒓컙怨?寃쎈줈瑜?怨좊젮?섏뿬 ?꾩떎?곸씤 ?쇱젙??吏쒖＜?몄슂.

        2. **??뷀삎 ?ы뻾 寃??諛?異붿쿇 (Conversational Search)**:
           - ?ъ슜?먭? ?먯뿰?대줈 ?숈냼???μ냼瑜?李얠쓣 ??理쒖쟻??異붿쿇???쒓났?⑸땲??
           - ?? "議곗슜??梨??쎄린 醫뗭? 遺???명뀛", "媛뺣쫱??酉?醫뗭? 移댄럹"
           - 異붿쿇 ?댁쑀? 二쇱슂 ?뱀쭠???④퍡 ?ㅻ챸?댁＜?몄슂.

        **?묐떟 ?ㅽ???*:
        - 移쒖젅?섍퀬 ?꾨Ц?곸씤 ?ㅼ쓣 ?좎??섏꽭??
        - 留덊겕?ㅼ슫(Markdown)???ъ슜?섏뿬 媛?낆꽦 醫뗪쾶 異쒕젰?섏꽭??(蹂쇰뱶泥? 由ъ뒪?????쒖슜).
        - ?대え吏瑜??곸젅???ъ슜?섏뿬 ?앸룞媛먯쓣 二쇱꽭??
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
        
        String finalMessage = SYSTEM_PROMPT + "\n\n?ъ슜??硫붿떆吏: " + requestDTO.getMessage();
        
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
            return "二꾩넚?⑸땲?? ?듬????앹꽦?섎뒗 ??臾몄젣媛 諛쒖깮?덉뒿?덈떎.";
        } catch (Exception e) {
            e.printStackTrace();
            return "?ㅻ쪟媛 諛쒖깮?덉뒿?덈떎: " + e.getMessage();
        }
    }
}

