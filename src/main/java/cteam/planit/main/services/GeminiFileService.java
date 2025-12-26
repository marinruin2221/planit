package cteam.planit.main.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.util.Map;

@Service
public class GeminiFileService {

  @Value("${gemini.api.key}")
  private String apiKey;

  private final RestTemplate restTemplate = new RestTemplate();

  public String uploadFile(String filePath, String mimeType) {
    String uploadUrl = "https://generativelanguage.googleapis.com/upload/v1beta/files?key=" + apiKey;

    File file = new File(filePath);
    if (!file.exists()) {
      throw new RuntimeException("File not found: " + filePath);
    }

    // 1. Initial Resumable Upload Request (Simplified to direct upload for small
    // files,
    // but for larger files > 2GB, resumable is needed.
    // However, Gemini File API supports multipart/related or separate
    // metadata+media upload.
    // Let's use the standard upload endpoint which is simpler for text files.)

    // Actually, for Gemini File API, we need to post the file metadata and content.
    // Let's use the simple upload method provided by Google's documentation pattern
    // if possible,
    // but standard multipart upload is often supported.

    // Correct endpoint for media upload:
    // POST
    // https://generativelanguage.googleapis.com/upload/v1beta/files?key=YOUR_API_KEY

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    // We need to send metadata first to get an upload URL, OR send multipart.
    // Let's try the multipart approach which is common.
    // Header: X-Goog-Upload-Protocol: multipart

    HttpHeaders uploadHeaders = new HttpHeaders();
    uploadHeaders.set("X-Goog-Upload-Protocol", "multipart");
    uploadHeaders.setContentType(MediaType.MULTIPART_RELATED);

    MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

    // Metadata part
    HttpHeaders metadataHeaders = new HttpHeaders();
    metadataHeaders.setContentType(MediaType.APPLICATION_JSON);
    String metadata = "{\"file\": {\"display_name\": \"accommodations_db\"}}";
    HttpEntity<String> metadataEntity = new HttpEntity<>(metadata, metadataHeaders);

    // File part
    HttpHeaders fileHeaders = new HttpHeaders();
    fileHeaders.setContentType(MediaType.parseMediaType(mimeType));
    FileSystemResource fileResource = new FileSystemResource(file);
    HttpEntity<FileSystemResource> fileEntity = new HttpEntity<>(fileResource, fileHeaders);

    body.add("metadata", metadataEntity);
    body.add("file", fileEntity);

    HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, uploadHeaders);

    try {
      ResponseEntity<Map> response = restTemplate.postForEntity(uploadUrl, requestEntity, Map.class);
      Map<String, Object> fileResponse = (Map<String, Object>) response.getBody().get("file");
      return (String) fileResponse.get("uri");
    } catch (Exception e) {
      throw new RuntimeException("Failed to upload file to Gemini: " + e.getMessage(), e);
    }
  }
}
