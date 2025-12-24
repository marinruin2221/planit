package cteam.planit.main.dto;

import java.time.LocalDateTime;

public record EventSummaryDto(
        Long id,
        String title,
        String imageUrl,
        String category,
        LocalDateTime startAt,
        LocalDateTime endAt
) {}
