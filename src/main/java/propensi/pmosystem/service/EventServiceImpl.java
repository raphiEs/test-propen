package propensi.pmosystem.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import propensi.pmosystem.model.CompanyModel;
import propensi.pmosystem.model.EventModel;
import propensi.pmosystem.repository.CompanyDb;
import propensi.pmosystem.repository.EventDb;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class EventServiceImpl implements EventService {

    @Autowired
    EventDb eventDb;

    @Override
    public EventModel addEvent(EventModel event) {
        eventDb.save(event);
        return event;
    }

    @Override
    public EventModel updateEvent(EventModel event) {
        eventDb.save(event);
        return event;
    }

    @Override
    public EventModel deleteEvent(EventModel event) {
        eventDb.delete(event);
        return event;
    }

    @Override
    public List<EventModel> getListEvent() {
        return eventDb.findAll();
    }

    @Override
    public EventModel getEventById(Long id) {
        Optional<EventModel> event = eventDb.findById(id);
        if (event.isPresent()){
            return event.get();
        } else return null;
    }

    @Override
    public List<EventModel> getListEventByProjectId(Long projectId){
        List<EventModel> events = eventDb.findByProjectId(projectId);
        return events;
    };

}
