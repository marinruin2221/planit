package cteam.planit.main.controller;

import cteam.planit.main.dto.AIRequestDTO;
import cteam.planit.main.dto.AIResponseDTO;
import cteam.planit.main.services.AIService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AIController {

  private final AIService aiService;

  @PostMapping("/chat")
  public Mono<AIResponseDTO> chat(@RequestBody AIRequestDTO requestDTO) {
    return aiService.getChatResponse(requestDTO);
  }
}
