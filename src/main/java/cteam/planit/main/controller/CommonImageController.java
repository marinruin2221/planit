package cteam.planit.main.controller;

import cteam.planit.main.entity.CommonImage;
import cteam.planit.main.repository.CommonImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
public class CommonImageController {

  private final CommonImageRepository commonImageRepository;

  @GetMapping("/fallback/{category}")
  public ResponseEntity<byte[]> getFallbackImage(@PathVariable String category) {
    return commonImageRepository.findByCategory(category)
        .map(image -> ResponseEntity.ok()
            .contentType(MediaType.IMAGE_PNG) // Assuming PNG for now, can be dynamic if needed
            .body(image.getImageData()))
        .orElse(ResponseEntity.notFound().build());
  }
}
