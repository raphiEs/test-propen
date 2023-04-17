package propensi.pmosystem.service;

import propensi.pmosystem.model.TimelineModel;

import java.sql.Time;
import java.util.List;

public interface TimelineService {
    List<TimelineModel> findAllByProjectId(Long id);
    TimelineModel addTimeline(TimelineModel timeline);
    TimelineModel findById(Long id);
    public void deleteTimeline(TimelineModel timeline);
    TimelineModel updateTimeline(TimelineModel timeline);

    Integer cekCurrWeight(Long id);
}
