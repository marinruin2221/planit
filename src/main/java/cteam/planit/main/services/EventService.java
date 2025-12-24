package cteam.planit.main.services;

import cteam.planit.main.dao.Event;
import cteam.planit.main.dao.EventRepository;
import cteam.planit.main.dao.EventSpecs;
import cteam.planit.main.dto.EventDetailDto;
import cteam.planit.main.dto.EventSummaryDto;

import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class EventService {

    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    // 목록: category/q/page/size 기반 조회 (page는 1부터 들어옴)
    public Page<EventSummaryDto> list(String category, String q, int page, int size) {
        int page0 = Math.max(page, 1) - 1;     // 1-based -> 0-based
        int pageSize = Math.max(size, 1);

        Pageable pageable = PageRequest.of(
                page0,
                pageSize,
                Sort.by(Sort.Order.asc("sortOrder"), Sort.Order.desc("startAt"))
        );

        LocalDateTime now = LocalDateTime.now();

        // 기본 규칙: 종료/비활성 미노출
        Specification<Event> spec = Specification.where(EventSpecs.activeOnly(now));

        // 카테고리(전체면 무시)
        if (category != null && !category.isBlank() && !"전체".equals(category)) {
            spec = spec.and(EventSpecs.categoryEq(category));
        }

        // 검색어(제목)
        if (q != null && !q.isBlank()) {
            spec = spec.and(EventSpecs.titleContainsIgnoreCase(q.trim()));
        }

        return eventRepository.findAll(spec, pageable)
                .map(e -> new EventSummaryDto(
                        e.getId(),
                        e.getTitle(),
                        e.getImageUrl(),
                        e.getCategory(),
                        e.getStartAt(),
                        e.getEndAt()
                ));
    }

    // 상세: 종료/비활성은 404 취급(찾지 못함)
    public EventDetailDto detail(long id) {
        LocalDateTime now = LocalDateTime.now();

        Specification<Event> spec = Specification.where(EventSpecs.activeOnly(now))
                .and(EventSpecs.idEq(id));

        Event e = eventRepository.findOne(spec)
                .orElseThrow(() -> new IllegalArgumentException("EVENT_NOT_FOUND"));

        return new EventDetailDto(
                e.getId(),
                e.getTitle(),
                e.getImageUrl(),
                e.getCategory(),
                e.getStartAt(),
                e.getEndAt(),
                e.getDescription()
        );
    }
}
