package cteam.planit.main.controller;

import cteam.planit.main.dto.EventDetailDto;
import cteam.planit.main.dto.EventPageResponse;
import cteam.planit.main.dto.EventSummaryDto;
import cteam.planit.main.services.EventService;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    // GET /api/events?category=모텔&q=겨울&page=1&size=9
    @GetMapping
    public EventPageResponse list(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "1") int page,   // 1-based
            @RequestParam(defaultValue = "9") int size
    ) {
        Page<EventSummaryDto> result = eventService.list(category, q, page, size);

        return new EventPageResponse(
                result.getContent(),
                page,
                size,
                result.getTotalElements(),
                result.getTotalPages()
        );
    }

    // GET /api/events/{id}
    @GetMapping("/{id}")
    public EventDetailDto detail(@PathVariable long id) {
        return eventService.detail(id);
    }
}

