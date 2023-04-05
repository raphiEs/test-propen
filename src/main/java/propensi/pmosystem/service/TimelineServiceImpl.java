package propensi.pmosystem.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import propensi.pmosystem.model.ProjectModel;
import propensi.pmosystem.model.ProjectUserModel;
import propensi.pmosystem.model.TimelineModel;
import propensi.pmosystem.repository.TimelineDb;

import java.util.List;
import java.util.Optional;

@Service
public class TimelineServiceImpl implements TimelineService{

@Autowired
    TimelineDb timelineDb;
    @Override
    public List<TimelineModel> findAllByProjectId(Long id){
        return timelineDb.findAllByProjectId(id);
    };

    @Override
    public TimelineModel findById(Long id){
        Optional<TimelineModel> timeline = timelineDb.findById(id);
        if(timeline.isPresent())
            return timeline.get();
        else
            return null;
    };
@Override
    public TimelineModel addTimeline(TimelineModel timeline) {
        return timelineDb.save(timeline);
    }

    @Override
    public void deleteTimeline(TimelineModel timeline){
        timelineDb.delete(timeline);
    }
    @Override
    public TimelineModel updateTimeline(TimelineModel timeline){
        timelineDb.save(timeline);
        return timeline;
    }
@Override
public Integer cekCurrWeight(Long id){
    List<TimelineModel> list = timelineDb.findAllByProjectId(id);
    Integer weight = 0;
    for(TimelineModel x : list){
        weight = weight + x.getWeight();
    }
    return weight;
}

}

