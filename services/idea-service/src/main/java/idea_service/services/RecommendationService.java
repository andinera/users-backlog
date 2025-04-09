package idea_service.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import idea_service.dao.RecommendationDAO;
import idea_service.models.Recommendation;

@Service
public class RecommendationService {

    @Autowired RecommendationDAO recommendationDAO;

    public Recommendation postRecommendation(final Recommendation recommendation) {
        recommendationDAO.postRecommendation(recommendation);
        return recommendation;
    }

}