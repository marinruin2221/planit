package cteam.planit.main.systemclasses;

import cteam.planit.main.dao.Event;
import cteam.planit.main.dao.EventRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class EventDataInitializer implements CommandLineRunner {

    private final EventRepository eventRepository;

    public EventDataInitializer(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Override
    public void run(String... args) {
        //eventRepository.deleteAll(); // 이벤드 디비 초기화 코드 한번 초기화 시면 반드시 주석처리 할것!
        LocalDateTime now = LocalDateTime.now();

        seedIfNotExists("연말 특별전", "https://image6.yanolja.com/cx-ydm/kzYJoRc7Eo9itmL8", "모텔",
                "연말 할인 이벤트 입니다.", 0, 
                LocalDateTime.of(2025, 12, 29, 0, 0),
                LocalDateTime.of(2026, 1, 31, 23, 59));

        seedIfNotExists("새해 특별전", "https://image6.yanolja.com/cx-ydm/e1ExAKpDrJuYPfSN", "호텔리조트",
                "신년 기념 할인 이벤트입니다.", 1, 
                LocalDateTime.of(2026, 1, 1, 0, 0),
                LocalDateTime.of(2026, 1, 15, 23, 59));

        seedIfNotExists("스파 새해특가", "https://image6.yanolja.com/cx-ydm/AjStIk5Ekd7uYsKo", "펜션·캠핑·게하",
                "새해 기념 스파 특가 이벤트입니다.", 2, 
                LocalDateTime.of(2026, 1, 1, 0, 0),
                LocalDateTime.of(2026, 1, 31, 23, 59));

        seedIfNotExists("홀리데이 공간대여 할인", "https://image6.yanolja.com/cx-ydm/Tv7mRL1cDUvQlWxD", "공간대여",
                "홀리데이 공간대여 특별 할인가 이벤트 입니다.", 3, 
                LocalDateTime.of(2025, 12, 25, 0, 0),
                LocalDateTime.of(2026, 1, 10, 23, 59));

        seedIfNotExists("호텔 최대 25% 혜택", "https://image6.yanolja.com/cx-ydm/3Ehmj6Xymtn4lfQx", "호텔리조트",
                "호텔 최대 할인가 이벤트 입니다.", 4, 
                LocalDateTime.of(2026, 1, 1, 0, 0),
                LocalDateTime.of(2026, 1, 31, 23, 59));

        seedIfNotExists("크리스마스 공연 최대 50%", "https://image6.yanolja.com/cx-ydm/duh8D212NSjf1Q1K", "공간대여",
                "크리스마스 기념 공연 최대 할인 이벤트 입니다.", 5, 
                LocalDateTime.of(2025, 12, 23, 0, 0),
                LocalDateTime.of(2026, 1, 10, 23, 59));

        seedIfNotExists("2026 BEST 펜션", "https://image6.yanolja.com/cx-ydm/jeVrxenjd1xR137u", "펜션·캠핑·게하",
                "올해 최고의 펜션을 최대 할인가로 만나볼 수 있는 이벤트 입니다.", 6, 
                LocalDateTime.of(2026, 1, 1, 0, 0),
                LocalDateTime.of(2026, 1, 31, 23, 59));

        seedIfNotExists("오션뷰 호텔최대 83%", "https://image6.yanolja.com/cx-ydm/CsCRwwdDz9kO2WF0", "호텔리조트",
                "오션뷰 호텔 최저가 이벤트 입니다.", 7, 
                LocalDateTime.of(2025, 12, 31, 0, 0),
                LocalDateTime.of(2026, 1, 31, 23, 59));

        seedIfNotExists("댕대이와 함께하는 특별한 시간", "https://image6.yanolja.com/cx-ydm/KGGPSZgFdjMdmVKL", "모텔",
                "반려동물과 함께는 숙소 특별가 이벤트입니다.", 8, 
                LocalDateTime.of(2025, 12, 15, 0, 0),
                LocalDateTime.of(2026, 1, 15, 23, 59));

        seedIfNotExists("신년맞이 호텔 초특가", "https://image6.yanolja.com/cx-ydm/WpwIAMX1bYQiEahY", "호텔리조트",
                "신년 맞이 기념 호텔 초특별 할인가 이벤트 입니다.", 9, 
                LocalDateTime.of(2026, 1, 1, 0, 0),
                LocalDateTime.of(2026, 1, 15, 23, 59));

        seedIfNotExists("숙소 묶음 구매 최대 20% 할인", "https://image6.yanolja.com/cx-ydm/QLQlDJmUSCjdfDVA", "펜션·캠핑·게하",
                "묶음 숙소 구매시 최대 할인가를 적용할 수 있는 이벤트 입니다.", 10, 
                LocalDateTime.of(2025, 12, 15, 0, 0),
                LocalDateTime.of(2026, 1, 15, 23, 59));

        seedIfNotExists("미식 가이드", "https://image6.yanolja.com/cx-ydm/5B72JloE6fLwYMzG", "공간대여",
                "미식과 함께 하는 여행을 초특가로 만날 수 있는 이벤트 입니다.", 11, 
                LocalDateTime.of(2025, 12, 1, 0, 0),
                LocalDateTime.of(2026, 1, 31, 23, 59));
    }

    private void seedIfNotExists(
            String title,
            String imageUrl,
            String category,
            String description,
            int sortOrder,
            LocalDateTime startAt,
            LocalDateTime endAt
    ) {
        // ⭐ 핵심: 이 title이 이미 있으면 삽입 스킵 (추가한 것만 새로 들어감)
        if (eventRepository.existsByTitle(title)) return;

        Event e = new Event();
        e.setTitle(title);
        e.setImageUrl(imageUrl);
        e.setCategory(category);
        e.setDescription(description);

        // ⭐ “종료된 건 안 보이게” 필터에 안 걸리도록 미래로 설정
        e.setStartAt(startAt);
        e.setEndAt(endAt);

        e.setActive(true);
        e.setSortOrder(sortOrder);

        eventRepository.save(e);
    }
}
