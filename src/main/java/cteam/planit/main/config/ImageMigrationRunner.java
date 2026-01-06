package cteam.planit.main.config;

import cteam.planit.main.entity.CommonImage;
import cteam.planit.main.repository.CommonImageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class ImageMigrationRunner implements CommandLineRunner {

  private final CommonImageRepository commonImageRepository;

  @Override
  public void run(String... args) throws Exception {
    log.info("Starting Image Migration...");

    try {
      // Map category to filename
      Map<String, String> imageMap = new HashMap<>();
      imageMap.put("hotel", "city.png"); // Temporary mapping as per plan
      imageMap.put("pension", "beach.png");
      imageMap.put("camping", "mountain.png");
      imageMap.put("guesthouse", "jeju.png");
      imageMap.put("resort", "city.png"); // Defaulting
      imageMap.put("default", "jeju.png");

      PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

      for (Map.Entry<String, String> entry : imageMap.entrySet()) {
        String category = entry.getKey();
        String fileName = entry.getValue();

        if (commonImageRepository.findByCategory(category).isPresent()) {
          log.info("Image for category '{}' already exists. Skipping.", category);
          continue;
        }

        try {
          // Load from classpath
          Resource resource = resolver.getResource("classpath:static/images/" + fileName);
          if (!resource.exists()) {
            log.warn("Image file '{}' not found in classpath.", fileName);
            continue;
          }

          byte[] imageData = StreamUtils.copyToByteArray(resource.getInputStream());

          CommonImage commonImage = CommonImage.builder()
              .category(category)
              .fileName(fileName)
              .imageData(imageData)
              .build();

          commonImageRepository.save(commonImage);
          log.info("Saved image for category '{}' ({})", category, fileName);

        } catch (IOException e) {
          log.error("Failed to load image '{}': {}", fileName, e.getMessage());
        }
      }
      log.info("Image Migration Completed.");
    } catch (Exception e) {
      log.warn("Image Migration skipped due to error: {}. Server will continue starting.", e.getMessage());
    }
  }
}
