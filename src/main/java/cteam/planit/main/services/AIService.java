package cteam.planit.main.services;

import cteam.planit.main.dao.Event;
import cteam.planit.main.dao.EventRepository;
import cteam.planit.main.dto.AIRequestDTO;
import cteam.planit.main.dto.AIResponseDTO;
import cteam.planit.main.entity.Accommodation;
import cteam.planit.main.repository.AccommodationRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AIService {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${spring.ai.model:gemini-2.0-flash}")
    private String modelName;

    private final WebClient.Builder webClientBuilder;
    private final AccommodationRepository accommodationRepository;
    private final EventRepository eventRepository;
    private final GeminiFileService geminiFileService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/{model}:generateContent?key={key}";

    private String knowledgeBaseFileUri;

    // System Prompt for RAG
    private static final String RAG_SYSTEM_PROMPT = """
                당신은 전문적인 'AI 여행 비서'입니다.
                제공된 파일(숙소 및 행사 데이터베이스)을 바탕으로 사용자의 질문에 가장 적합한 숙소와 관련 행사/축제를 추천하고 여행 조언을 제공하세요.

                규칙:
                1. 사용자의 질문을 분석하여 위치, 가격대, 숙소 유형, 특징, 여행 시기 등을 파악하세요.
                2. **반드시 제공된 파일 내의 데이터에서만 숙소와 행사를 검색하세요. 파일에 없는 정보는 절대 추천하지 마세요.**
                3. **숙소 추천 시**:
                   - 상위 3~5개의 숙소를 추천하세요.
                   - 각 숙소의 이름 바로 뒤에 반드시 `[ID:숙소ID]`를 붙여야 합니다. (예: **신라호텔 [ID:12345]**)
                4. **행사/축제 추천 시**:
                   - 사용자의 여행 지역이나 시기에 맞는 행사/축제가 있다면 함께 추천하세요.
                   - 각 행사의 이름 바로 뒤에 반드시 `[EventID:행사ID]`를 붙여야 합니다. (예: **머드축제 [EventID:9876]**)
                5. **만약 조건에 맞는 숙소나 행사가 파일에 없다면, 솔직하게 해당 조건에 맞는 데이터가 없다고 말하세요.**
                6. 답변은 친절하고 전문적인 톤을 유지하며, 마크다운(볼드체, 리스트 등)을 사용하여 가독성을 높이세요.
                7. 마지막에는 "지도에서 위치를 확인해보세요!"와 같은 멘트를 덧붙이세요.

                답변 예시:
                1. **공주한옥마을 [ID:1867806]**: 공주 관광단지 내에 위치한...
                2. **봉황재 [ID:2708325]**: 공주 원도심에 위치한...

                추천 행사:
                - **공주 군밤축제 [EventID:555]**: 겨울철 공주에서 즐길 수 있는...
            """;

    @PostConstruct
    public void initializeKnowledgeBase() {
        new Thread(() -> {
            try {
                System.out.println("[AI] Initializing Knowledge Base...");
                List<Accommodation> allAccommodations = accommodationRepository.findAll();
                List<Event> allEvents = eventRepository.findAll();

                if (allAccommodations.isEmpty() && allEvents.isEmpty()) {
                    System.out.println("[AI] No data found in DB. Skipping knowledge base upload.");
                    return;
                }

                File file = createDataFile(allAccommodations, allEvents);
                System.out.println("[AI] Data file created: " + file.getAbsolutePath());

                String fileUri = geminiFileService.uploadFile(file.getAbsolutePath(), "text/csv");
                this.knowledgeBaseFileUri = fileUri;
                System.out.println("[AI] Knowledge Base Uploaded Successfully. File URI: " + fileUri);

                // Keep the file for user inspection
                System.out.println("[AI] RAG data file saved locally at: " + file.getAbsolutePath());

            } catch (Exception e) {
                System.err.println("[AI] Failed to initialize knowledge base: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();
    }

    private File createDataFile(List<Accommodation> accommodations, List<Event> events) throws IOException {
        // Save to project root for easy access
        File file = new File("rag_data.csv");
        // Use UTF-8 encoding explicitly
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
            // Write CSV Header
            writer.write("Type,ID,Name,Address,Category,Price,AreaCode,Dates,Description\n");

            // Write Accommodations
            for (Accommodation acc : accommodations) {
                StringBuilder line = new StringBuilder();
                line.append("Accommodation,");
                line.append(escapeCsv(acc.getContentId())).append(",");
                line.append(escapeCsv(acc.getTitle())).append(",");
                line.append(escapeCsv(acc.getAddr1() + " " + acc.getAddr2())).append(",");
                line.append(escapeCsv(getCategoryName(acc.getCat3()))).append(",");
                line.append(escapeCsv(acc.getMinPrice() != null ? String.valueOf(acc.getMinPrice()) : "Unknown"))
                        .append(",");
                line.append(escapeCsv(acc.getAreacode())).append(",");
                line.append("N/A,"); // Dates for accommodation
                line.append("N/A"); // Description for accommodation
                line.append("\n");
                writer.write(line.toString());
            }

            // Write Events
            for (Event evt : events) {
                StringBuilder line = new StringBuilder();
                line.append("Event,");
                line.append(escapeCsv(String.valueOf(evt.getId()))).append(",");
                line.append(escapeCsv(evt.getTitle())).append(",");
                line.append("Unknown,"); // Address for event (might not be available in simple entity)
                line.append(escapeCsv(evt.getCategory())).append(",");
                line.append("N/A,"); // Price for event
                line.append("Unknown,"); // AreaCode for event
                line.append(escapeCsv(evt.getStartAt().toString() + "~" + evt.getEndAt().toString())).append(",");
                line.append(escapeCsv(evt.getDescription()));
                line.append("\n");
                writer.write(line.toString());
            }
        }
        return file;
    }

    private String escapeCsv(String value) {
        if (value == null)
            return "";
        String escaped = value.replace("\"", "\"\"");
        if (escaped.contains(",") || escaped.contains("\n") || escaped.contains("\"")) {
            return "\"" + escaped + "\"";
        }
        return escaped;
    }

    private String getCategoryName(String cat3) {
        if (cat3 == null)
            return "Unknown";
        // Simple mapping for readability
        return switch (cat3) {
            case "B02010100" -> "Hotel";
            case "B02010200" -> "Condo";
            case "B02010400" -> "Pension";
            case "B02010500" -> "Motel";
            case "B02010700" -> "Guesthouse";
            case "B02011000" -> "Hanok";
            case "B02011100" -> "Camping";
            case "B02011200" -> "Glamping";
            default -> cat3;
        };
    }

    public Mono<AIResponseDTO> getChatResponse(AIRequestDTO requestDTO) {
        if (knowledgeBaseFileUri == null) {
            AIResponseDTO errorResponse = new AIResponseDTO();
            errorResponse.setResponse("죄송합니다. AI 지식 베이스가 아직 준비되지 않았습니다. 잠시 후 다시 시도해주세요.");
            errorResponse.setRecommendations(new ArrayList<>());
            return Mono.just(errorResponse);
        }

        return callGeminiWithFile(requestDTO.getMessage())
                .map(responseText -> {
                    AIResponseDTO responseDTO = new AIResponseDTO();

                    // 1. Extract IDs from response text
                    List<String> contentIds = extractContentIds(responseText);
                    List<Long> eventIds = extractEventIds(responseText);

                    // 2. Fetch Entities
                    List<Accommodation> recommendations = new ArrayList<>();
                    if (!contentIds.isEmpty()) {
                        recommendations = accommodationRepository.findAllById(contentIds);
                    }

                    List<Event> recommendedEvents = new ArrayList<>();
                    if (!eventIds.isEmpty()) {
                        recommendedEvents = eventRepository.findAllById(eventIds);
                    }

                    // 3. Remove IDs from text for cleaner display
                    String cleanResponse = responseText.replaceAll("\\[ID:\\d+\\]", "")
                            .replaceAll("\\[EventID:\\d+\\]", "");

                    responseDTO.setResponse(cleanResponse);
                    responseDTO.setRecommendations(recommendations);
                    responseDTO.setRecommendedEvents(recommendedEvents);
                    return responseDTO;
                });
    }

    private List<String> extractContentIds(String text) {
        List<String> ids = new ArrayList<>();
        // Regex to find [ID:12345] pattern
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\\[ID:(\\d+)\\]");
        java.util.regex.Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            ids.add(matcher.group(1));
        }
        return ids;
    }

    private List<Long> extractEventIds(String text) {
        List<Long> ids = new ArrayList<>();
        // Regex to find [EventID:12345] pattern
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\\[EventID:(\\d+)\\]");
        java.util.regex.Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            try {
                ids.add(Long.parseLong(matcher.group(1)));
            } catch (NumberFormatException e) {
                // Ignore invalid IDs
            }
        }
        return ids;
    }

    private Mono<String> callGeminiWithFile(String userMessage) {
        Map<String, Object> request = new HashMap<>();
        List<Map<String, Object>> contents = new ArrayList<>();

        Map<String, Object> userContent = new HashMap<>();
        userContent.put("role", "user");

        List<Map<String, Object>> parts = new ArrayList<>();

        // 1. File Data
        Map<String, Object> fileData = new HashMap<>();
        fileData.put("mime_type", "text/csv");
        fileData.put("file_uri", knowledgeBaseFileUri);
        parts.add(Map.of("file_data", fileData));

        // 2. Text Prompt
        String fullPrompt = RAG_SYSTEM_PROMPT + "\n\nUser Question: " + userMessage;
        parts.add(Map.of("text", fullPrompt));

        userContent.put("parts", parts);
        contents.add(userContent);

        request.put("contents", contents);

        System.out.println("[AI] Calling Gemini API with File RAG. Model: " + modelName);

        return webClientBuilder.build()
                .post()
                .uri(GEMINI_API_URL, modelName, apiKey)
                .bodyValue(request)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> response.bodyToMono(String.class)
                                .flatMap(errorBody -> {
                                    System.err.println("[AI] Gemini API Error - Status: " + response.statusCode()
                                            + ", Body: " + errorBody);
                                    return Mono.error(new RuntimeException("Gemini API Error: " + errorBody));
                                }))
                .bodyToMono(String.class)
                .doOnNext(response -> System.out.println("[AI] Gemini API Response received"))
                .map(this::parseGeminiResponse)
                .onErrorResume(e -> {
                    System.err.println("[AI] Error calling Gemini API: " + e.getMessage());
                    e.printStackTrace();
                    return Mono.just("죄송합니다. AI 서비스에 일시적인 문제가 발생했습니다. (오류: " + e.getMessage() + ")");
                });
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
            return "답변을 생성할 수 없습니다.";
        } catch (Exception e) {
            e.printStackTrace();
            return "오류가 발생했습니다.";
        }
    }
}
