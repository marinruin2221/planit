package cteam.planit.main.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import cteam.planit.main.dao.Event;
import cteam.planit.main.dao.EventRepository;
import cteam.planit.main.entity.Accommodation;
import cteam.planit.main.repository.AccommodationRepository;

@Service
public class MainService
{
	@Autowired
    public EventRepository eventRepository;

	@Autowired
	public AccommodationRepository accommodationRepository;

	public List<Event> eventSelect() throws Exception
	{
        return eventRepository.findAll(
			PageRequest.of
			(
				0,
				3,
				Sort.by("sortOrder").ascending()
			))
			.getContent();
	}

	public List<Accommodation> bestGoodStay() throws Exception
	{
		return accommodationRepository.findTop10ByOrderByMinPriceAsc();
	}
}