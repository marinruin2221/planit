package cteam.planit.main.dto;

import java.util.List;

public record EventPageResponse(
        List<EventSummaryDto> content,
        int page,           // 1-based
        int size,
        long totalElements,
        int totalPages
) {}
