package propensi.pmosystem.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import propensi.pmosystem.model.EventModel;
import propensi.pmosystem.model.FeedbackModel;
import propensi.pmosystem.repository.FeedbackDb;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class FeedbackServiceImpl implements FeedbackService{

    @Autowired
    FeedbackDb feedbackDb;

    @Override
    public FeedbackModel addFeedback(FeedbackModel feedback) {
        return feedbackDb.save(feedback);
    }

    @Override
    public void deleteFeedback(FeedbackModel feedback) {
        feedbackDb.delete(feedback);
    }

    @Override
    public List<FeedbackModel> getListFeedback() {
        return feedbackDb.findAll();
    }

    @Override
    public FeedbackModel getFeedbackById(Long id) {
        Optional<FeedbackModel> feedback = feedbackDb.findById(id);
        if (feedback.isPresent()){
            return feedback.get();
        } else return null;
    }

}
