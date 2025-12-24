package cteam.planit.main.dto;

import java.time.LocalDateTime;

public record EventDetailDto(
        Long id,
        String title,
        String imageUrl,
        String category,
        LocalDateTime startAt,
        LocalDateTime endAt,
        String description
) {}