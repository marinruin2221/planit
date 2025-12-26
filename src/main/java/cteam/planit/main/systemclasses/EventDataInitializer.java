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
        // eventRepository.deleteAll(); // 이벤드 디비 초기화 코드 한번 초기화 시면 반드시 주석처리 할것!
        LocalDateTime now = LocalDateTime.now();

        seedIfNotExists("겨울 특별전", "https://image6.yanolja.com/cx-ydm/kzYJoRc7Eo9itmL8", "모텔",
                "서버 시작 시 자동 삽입되는 테스트 이벤트입니다.", 0, now);

        seedIfNotExists("신년 맞이 할인", "https://image6.yanolja.com/cx-ydm/qQ2a8GlG3dDs2sv0", "호텔리조트",
                "신년 기념 할인 이벤트입니다.", 1, now);

        seedIfNotExists("겨울 특별전1", "https://image6.yanolja.com/cx-ydm/gfgPQFZbAVMKIUQD", "모텔",
                "테스트 이벤트입니다.", 2, now);

        seedIfNotExists("신년 맞이 할인2", "https://image6.yanolja.com/cx-ydm/Tv7mRL1cDUvQlWxD", "호텔리조트",
                "테스트 이벤트입니다.", 3, now);

        seedIfNotExists("겨울 특별전3", "https://image6.yanolja.com/cx-ydm/3Ehmj6Xymtn4lfQx", "모텔",
                "테스트 이벤트입니다.", 4, now);

        seedIfNotExists("신년 맞이 할인4", "https://image6.yanolja.com/cx-ydm/duh8D212NSjf1Q1K", "호텔리조트",
                "테스트 이벤트입니다.", 5, now);

        seedIfNotExists("겨울 특별전5", "https://image6.yanolja.com/cx-ydm/jeVrxenjd1xR137u", "모텔",
                "테스트 이벤트입니다.", 6, now);

        seedIfNotExists("신년 맞이 할인6", "https://image6.yanolja.com/cx-ydm/CsCRwwdDz9kO2WF0", "호텔리조트",
                "테스트 이벤트입니다.", 7, now);

        seedIfNotExists("겨울 특별전7", "https://image6.yanolja.com/cx-ydm/KGGPSZgFdjMdmVKL", "모텔",
                "테스트 이벤트입니다.", 8, now);

        seedIfNotExists("신년 맞이 할인8", "https://image6.yanolja.com/cx-ydm/PrAdVg6gNOTXh4Ie", "호텔리조트",
                "테스트 이벤트입니다.", 9, now);

        seedIfNotExists("겨울 특별전9", "https://image6.yanolja.com/cx-ydm/MAh7l0Is8geJ6kOQ", "모텔",
                "테스트 이벤트입니다.", 10, now);

        seedIfNotExists("신년 맞이 할인10", "https://image6.yanolja.com/cx-ydm/5B72JloE6fLwYMzG", "호텔리조트",
                "테스트 이벤트입니다.", 11, now);
    }

    private void seedIfNotExists(
            String title,
            String imageUrl,
            String category,
            String description,
            int sortOrder,
            LocalDateTime now
    ) {
        // ⭐ 핵심: 이 title이 이미 있으면 삽입 스킵 (추가한 것만 새로 들어감)
        if (eventRepository.existsByTitle(title)) return;

        Event e = new Event();
        e.setTitle(title);
        e.setImageUrl(imageUrl);
        e.setCategory(category);
        e.setDescription(description);

        // ⭐ “종료된 건 안 보이게” 필터에 안 걸리도록 미래로 설정
        e.setStartAt(now.minusDays(1));
        e.setEndAt(now.plusDays(30));

        e.setActive(true);
        e.setSortOrder(sortOrder);

        eventRepository.save(e);
    }
}
