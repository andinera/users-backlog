package idea_service.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import idea_service.models.Recommendation;
import idea_service.services.RecommendationService;

@CrossOrigin
@RestController
@RequestMapping("/recommendation")
public class RecommendationController {

    @Autowired RecommendationService recommendationService;

    @PostMapping(path = "postRecommendation")
    public Recommendation postRecommendation(@RequestBody final Recommendation recommendation) {
        return recommendationService.postRecommendation(recommendation);
    }
}