package propensi.pmosystem.service;

import propensi.pmosystem.model.CompanyModel;
import propensi.pmosystem.model.EventModel;

import java.util.List;

public interface EventService {

    EventModel addEvent(EventModel event);

    EventModel updateEvent(EventModel event);

    EventModel deleteEvent(EventModel event);

    List<EventModel> getListEvent();
    List<EventModel> getListEventByProjectId(Long projectId);
    EventModel getEventById(Long id);
}
