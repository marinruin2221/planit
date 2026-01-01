package cteam.planit.main.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

@Service
public class GeminiFileService {

  @Value("${gemini.api.key}")
  private String apiKey;

  private final ObjectMapper objectMapper = new ObjectMapper();

  public String uploadFile(String filePath, String mimeType) {
    String safeKey = getSafeKey();

    // 1. Verify API Key first
    verifyApiKey(safeKey);

    File file = new File(filePath);
    if (!file.exists()) {
      throw new RuntimeException("File not found: " + filePath);
    }

    try {
      // 2. Start Resumable Upload Session
      String uploadUrl = startResumableUpload(safeKey, mimeType, file.length());
      System.out.println("[GeminiFileService] Resumable upload session started. URL obtained.");

      // 3. Upload File Content
      return performUpload(uploadUrl, file);

    } catch (Exception e) {
      throw new RuntimeException("Failed to upload file to Gemini via HttpURLConnection: " + e.getMessage(), e);
    }
  }

  private String getSafeKey() {
    String safeKey = (apiKey != null) ? apiKey.trim() : "";
    if (safeKey.isEmpty()) {
      System.err.println("[GeminiFileService] API Key is NULL or EMPTY! Check gemini.api.key");
      throw new RuntimeException("API Key is missing");
    }
    System.out.println("[GeminiFileService] API Key loaded: ["
        + (safeKey.length() > 5 ? safeKey.substring(0, 5) + "..." : safeKey) + "] Length: " + safeKey.length());
    return safeKey;
  }

  private void verifyApiKey(String key) {
    System.out.println("[GeminiFileService] Verifying API Key...");
    HttpURLConnection connection = null;
    try {
      URL url = new URL("https://generativelanguage.googleapis.com/v1beta/models?key=" + key);
      connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("GET");
      connection.setRequestProperty("x-goog-api-key", key);

      int responseCode = connection.getResponseCode();
      if (responseCode == 200) {
        System.out.println("[GeminiFileService] API Key verification SUCCESSFUL.");
      } else {
        String error = readErrorStream(connection);
        System.err
            .println("[GeminiFileService] API Key verification FAILED. Status: " + responseCode + ", Error: " + error);
        // We don't throw exception here to allow upload attempt to proceed (or maybe we
        // should?)
        // Let's throw to fail fast.
        throw new RuntimeException("API Key verification failed with status " + responseCode);
      }
    } catch (Exception e) {
      System.err.println("[GeminiFileService] API Key verification error: " + e.getMessage());
      if (e instanceof RuntimeException)
        throw (RuntimeException) e;
    } finally {
      if (connection != null)
        connection.disconnect();
    }
  }

  private String startResumableUpload(String key, String mimeType, long fileLength) throws IOException {
    HttpURLConnection connection = null;
    try {
      URL url = new URL("https://generativelanguage.googleapis.com/upload/v1beta/files?key=" + key);
      connection = (HttpURLConnection) url.openConnection();
      connection.setDoOutput(true);
      connection.setRequestMethod("POST");

      // Standard headers for resumable upload start
      connection.setRequestProperty("X-Goog-Upload-Protocol", "resumable");
      connection.setRequestProperty("X-Goog-Upload-Command", "start");
      connection.setRequestProperty("X-Goog-Upload-Header-Content-Length", String.valueOf(fileLength));
      connection.setRequestProperty("X-Goog-Upload-Header-Content-Type", mimeType);
      connection.setRequestProperty("Content-Type", "application/json");
      connection.setRequestProperty("x-goog-api-key", key);

      // Metadata body
      String metadata = "{\"file\": {\"display_name\": \"accommodations_db\"}}";
      try (OutputStream os = connection.getOutputStream()) {
        os.write(metadata.getBytes(StandardCharsets.UTF_8));
      }

      int responseCode = connection.getResponseCode();
      if (responseCode != 200) {
        String error = readErrorStream(connection);
        throw new RuntimeException("Resumable upload start failed. Status: " + responseCode + ", Error: " + error);
      }

      // Extract upload URL from headers
      String uploadUrl = connection.getHeaderField("x-goog-upload-url");
      if (uploadUrl == null) {
        // Sometimes it's in Location?
        uploadUrl = connection.getHeaderField("Location");
      }

      if (uploadUrl == null) {
        throw new RuntimeException("Failed to retrieve upload URL from response headers.");
      }

      return uploadUrl;

    } finally {
      if (connection != null)
        connection.disconnect();
    }
  }

  private String performUpload(String uploadUrl, File file) throws IOException {
    HttpURLConnection connection = null;
    try {
      URL url = new URL(uploadUrl);
      connection = (HttpURLConnection) url.openConnection();
      connection.setDoOutput(true);
      connection.setRequestMethod("PUT"); // Usually PUT for content

      long fileLength = file.length();
      connection.setRequestProperty("Content-Length", String.valueOf(fileLength));
      connection.setRequestProperty("X-Goog-Upload-Offset", "0");
      connection.setRequestProperty("X-Goog-Upload-Command", "upload, finalize");

      try (OutputStream os = connection.getOutputStream()) {
        Files.copy(file.toPath(), os);
      }

      int responseCode = connection.getResponseCode();
      if (responseCode != 200) {
        String error = readErrorStream(connection);
        throw new RuntimeException("File upload failed. Status: " + responseCode + ", Error: " + error);
      }

      try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
          response.append(line);
        }

        JsonNode root = objectMapper.readTree(response.toString());
        return root.path("file").path("uri").asText();
      }

    } finally {
      if (connection != null)
        connection.disconnect();
    }
  }

  private String readErrorStream(HttpURLConnection connection) throws IOException {
    try (InputStream errorStream = connection.getErrorStream()) {
      if (errorStream == null)
        return "No error details available";
      try (BufferedReader reader = new BufferedReader(new InputStreamReader(errorStream))) {
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
          sb.append(line);
        }
        return sb.toString();
      }
    }
  }
}
