package cteam.planit.main.dao;

import org.springframework.data.jpa.domain.Specification;
import java.time.LocalDateTime;

public class EventSpecs {

    public static Specification<Event> activeOnly(LocalDateTime now) {
        return (root, query, cb) -> cb.and(
                cb.isTrue(root.get("isActive")),
                cb.lessThanOrEqualTo(root.get("startAt"), now),
                cb.greaterThanOrEqualTo(root.get("endAt"), now)
        );
    }

    public static Specification<Event> categoryEq(String category) {
        return (root, query, cb) -> cb.equal(root.get("category"), category);
    }

    public static Specification<Event> titleContainsIgnoreCase(String q) {
        String keyword = "%" + q.toLowerCase() + "%";
        return (root, query, cb) -> cb.like(cb.lower(root.get("title")), keyword);
    }

    public static Specification<Event> idEq(long id) {
        return (root, query, cb) -> cb.equal(root.get("id"), id);
    }
}
