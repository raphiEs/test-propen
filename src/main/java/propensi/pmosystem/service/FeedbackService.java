package propensi.pmosystem.service;

import propensi.pmosystem.model.EventModel;
import propensi.pmosystem.model.FeedbackModel;

import java.util.List;

public interface FeedbackService {
    FeedbackModel addFeedback(FeedbackModel feedback);

    void deleteFeedback(FeedbackModel feedback);

    List<FeedbackModel> getListFeedback();

    FeedbackModel getFeedbackById(Long id);
}
