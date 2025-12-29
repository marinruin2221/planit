package cteam.planit.main.services;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import cteam.planit.main.dao.Event;
import cteam.planit.main.dao.EventRepository;

@Service
public class MainService
{
	@Autowired
    public EventRepository eventRepository;

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
}