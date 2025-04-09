package users_backlog.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import users_backlog.dao.CategoryDAO;
import users_backlog.dao.IdeaDAO;
import users_backlog.dao.ImplementationDAO;
import users_backlog.models.Idea;
import users_backlog.models.Innovator;
import users_backlog.models.Recommendation;
import users_backlog.models.Reply;

@Service
public class IdeaService {

    @Autowired IdeaDAO ideaDAO;
    @Autowired ImplementationDAO implementationDAO;
    @Autowired CategoryDAO categoryDAO;
    @Autowired ElasticSearchService elasticSearchService;

    public List<Idea> getIdeas(String categoryName) {
        return ideaDAO.getIdeas(categoryName);
    }

    public Idea getIdea(final long id) {
        Idea idea = ideaDAO.getIdea(id);
        if (idea != null) {
            idea.setCategories(categoryDAO.getCategories(idea));
            // idea.setImplementations(implementationDAO.getImplementations(idea));
        }
        return idea;
    }

    public Idea getIdea(final String summary) {
        Idea idea = ideaDAO.getIdea(summary);
        if (idea != null) {
            idea.setCategories(categoryDAO.getCategories(idea));
            // idea.setImplementations(implementationDAO.getImplementations(idea));
        }
        return idea;
    }

    public Idea postIdea(final Idea idea) {
        categoryDAO.postCategories(idea.getCategories());
        Idea updatedIdea = ideaDAO.postIdea(idea);
        elasticSearchService.index(updatedIdea);
        idea.setCategories(categoryDAO.getCategories(updatedIdea));
        return updatedIdea;
    }

    public boolean deleteIdea(final Idea idea) {
        boolean deleted = ideaDAO.deleteIdea(idea);
        if (deleted) {
            elasticSearchService.delete(idea);
        }
        return deleted;
    }

    public Long postVote(Idea idea, Innovator innovator, Boolean up) {
        return ideaDAO.postVote(idea, innovator, up);
    }

    public Recommendation<Idea> postRecommendation(Recommendation<Idea> recommendation) {
        return ideaDAO.postRecommendation(recommendation);
    }

    public boolean deleteRecommendation(final Recommendation<Idea> recommendation) {
        return ideaDAO.deleteRecommendation(recommendation);
    }

    public Long postRecommendationVote(Recommendation<Idea> recommendation, Innovator innovator, Boolean up) {
        return ideaDAO.postRecommendationVote(recommendation, innovator, up);
    }

    public Reply<Idea> postRecommendationReply(Reply<Idea> reply) {
        return ideaDAO.postRecommendationReply(reply);
    }

    public boolean deleteRecommendationReply(final Reply<Idea> reply) {
        return ideaDAO.deleteRecommendationReply(reply);
    }

}