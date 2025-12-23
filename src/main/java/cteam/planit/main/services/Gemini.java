package cteam.planit.main.services;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import cteam.planit.main.systemclasses.FileSearchStore;
import cteam.planit.main.systemclasses.FileSearchStoreDocument;
import cteam.planit.main.systemclasses.FileSearchStoreDocumentResult;
import cteam.planit.main.systemclasses.FileSearchStoreDocumentUpload;
import cteam.planit.main.systemclasses.FileSearchStoreRequest;
import cteam.planit.main.systemclasses.FileSearchStoreResult;
import cteam.planit.main.systemclasses.MultipartInputResource;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@Scope("singleton")
public class Gemini {
  @Autowired
  @Qualifier("gemini_generate_content")
  WebClient gemini;
  @Autowired
  @Qualifier("gemini_file_store")
  WebClient gemini_file_store;
  @Autowired
  @Qualifier("gemini_file_upload")
  WebClient gemini_file_upload;

  @Autowired
  public GeminiSchema schemas;

  public List<FileSearchStore> storeList() throws Exception {
    String result = gemini_file_store
        .get()
        .uri("fileSearchStores")
        .retrieve()
        .bodyToMono(String.class)
        .block();
    var fssr = new ObjectMapper().readValue(result, new TypeReference<FileSearchStoreResult>() {
    });
    return fssr.fileSearchStores;
  }

  public FileSearchStore storeCreate(String displayName) throws Exception {
    String result = gemini_file_store
        .post()
        .uri("fileSearchStores")
        .bodyValue(new FileSearchStoreRequest(displayName))
        .retrieve()
        .bodyToMono(String.class)
        .block();
    var fss = new ObjectMapper().readValue(result, new TypeReference<FileSearchStore>() {
    });
    return fss;
  }

  public FileSearchStore storeGet(String name) throws Exception {
    String result = gemini_file_store
        .get()
        .uri(name)
        .retrieve()
        .bodyToMono(String.class)
        .block();
    var fss = new ObjectMapper().readValue(result, new TypeReference<FileSearchStore>() {
    });
    return fss;
  }

  public void storeDelete(String name) {
    gemini_file_store
        .delete()
        .uri(name + "?force=true")
        .retrieve()
        .bodyToMono(Void.class)
        .block();
  }

  public FileSearchStoreDocument documentGet(String documentName) throws Exception {
    String result = gemini_file_store
        .get()
        .uri(documentName)
        .retrieve()
        .bodyToMono(String.class)
        .block();
    var fss = new ObjectMapper().readValue(result, new TypeReference<FileSearchStoreDocument>() {
    });
    return fss;
  }

  public List<FileSearchStoreDocument> documentList(String storeName) throws Exception {
    String result = gemini_file_store
        .get()
        .uri(storeName + "/documents")
        .retrieve()
        .bodyToMono(String.class)
        .block();
    var fss = new ObjectMapper().readValue(result, new TypeReference<FileSearchStoreDocumentResult>() {
    });
    return fss.documents;
  }

  public void documentDelete(String documentName) {
    gemini_file_store
        .delete()
        .uri(documentName + "?force=true")
        .retrieve()
        .bodyToMono(Void.class)
        .block();
  }

  public FileSearchStoreDocument documentUpload(String storeName, MultipartFile file) throws IOException, Exception {

    LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
    map.add("file", new MultipartInputResource(file));

    String result = gemini_file_upload
        .post()
        .uri(storeName + ":uploadToFileSearchStore")
        .contentType(MediaType.MULTIPART_FORM_DATA)
        .body(BodyInserters.fromMultipartData(map))
        .retrieve()
        .bodyToMono(String.class)
        .block();
    var fss = new ObjectMapper().readValue(result, new TypeReference<FileSearchStoreDocumentUpload>() {
    });
    return new FileSearchStoreDocument(fss.response.getDocumentName());
  }

  private String gemini_run_to_string(Map<String, Object> request) {
    return gemini
        .post()
        .bodyValue(request)
        .retrieve()
        .bodyToMono(Map.class)
        .map(response -> {
          @SuppressWarnings("unchecked")
          List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.get("candidates");
          if (candidates == null || candidates.isEmpty())
            return "No Result Error!";
          @SuppressWarnings("unchecked")
          Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
          @SuppressWarnings("unchecked")
          List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
          return parts.get(0).get("text").toString();
        })
        .block();
  }

  public String run(List<String> systems, String user, Map<String, Object> schema) {
    Map<String, Object> request = Map.of(
        "system_instruction", Map.of(
            "parts", systems.stream().map(system -> Map.of("text", system)).toList()),
        "contents", List.of(
            Map.of("parts", List.of(
                Map.of("text", user)))),
        "generationConfig", Map.of(
            "responseMimeType", "application/json",
            "responseSchema", schema));

    return gemini_run_to_string(request);
  }

  public String run(List<String> systems, String user, Map<String, Object> schema, List<String> stores) {
    Map<String, Object> request = Map.of(
        "system_instruction", Map.of(
            "parts", systems.stream().map(system -> Map.of("text", system)).toList()),
        "contents", List.of(
            Map.of("parts", List.of(
                Map.of("text", user)))),
        "generationConfig", Map.of(
            "responseMimeType", "application/json",
            "responseSchema", schema),
        "tools", List.of(
            Map.of("fileSearch", Map.of(
                "fileSearchStoreNames",
                stores))));

    return gemini_run_to_string(request);
  }

  public String run(List<String> systems, String user) {
    Map<String, Object> request = Map.of(
        "system_instruction", Map.of(
            "parts", systems.stream().map(system -> Map.of("text", system)).toList()),
        "contents", List.of(
            Map.of("parts", List.of(
                Map.of("text", user)))));

    return gemini_run_to_string(request);
  }

  public String run(List<String> systems, String user, List<String> stores) {
    Map<String, Object> request = Map.of(
        "system_instruction", Map.of(
            "parts", systems.stream().map(system -> Map.of("text", system)).toList()),
        "contents", List.of(
            Map.of("parts", List.of(
                Map.of("text", user)))),
        "tools", List.of(
            Map.of("fileSearch", Map.of(
                "fileSearchStoreNames",
                stores))));

    return gemini_run_to_string(request);
  }

  public String run(String user) {
    Map<String, Object> request = Map.of(
        "contents", List.of(
            Map.of("parts", List.of(
                Map.of("text", user)))));
    return gemini_run_to_string(request);
  }

  public String run(String user, List<String> stores) {
    Map<String, Object> request = Map.of(
        "contents", List.of(
            Map.of("parts", List.of(
                Map.of("text", user)))),
        "tools", List.of(
            Map.of("fileSearch", Map.of(
                "fileSearchStoreNames",
                stores))));

    return gemini_run_to_string(request);
  }
}
