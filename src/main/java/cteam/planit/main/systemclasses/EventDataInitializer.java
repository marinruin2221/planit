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
        // 중복 방지: 이미 데이터가 있으면 삽입하지 않음
        if (eventRepository.count() > 0) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();

        Event e1 = new Event();
        e1.setTitle("겨울 특별전");
        e1.setImageUrl("https://image6.yanolja.com/cx-ydm/kzYJoRc7Eo9itmL8");
        e1.setCategory("모텔");
        e1.setStartAt(now.minusDays(1));
        e1.setEndAt(now.plusDays(7));
        e1.setDescription("서버 시작 시 자동 삽입되는 테스트 이벤트입니다.");
        e1.setActive(true);
        e1.setSortOrder(0);

        Event e2 = new Event();
        e2.setTitle("신년 맞이 할인");
        e2.setImageUrl("https://image6.yanolja.com/cx-ydm/qQ2a8GlG3dDs2sv0");
        e2.setCategory("호텔리조트");
        e2.setStartAt(now.minusDays(2));
        e2.setEndAt(now.plusDays(10));
        e2.setDescription("신년 기념 할인 이벤트입니다.");
        e2.setActive(true);
        e2.setSortOrder(1);

        eventRepository.save(e1);
        eventRepository.save(e2);
    }
}
