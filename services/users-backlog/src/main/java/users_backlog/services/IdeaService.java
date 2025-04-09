package users_backlog.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import users_backlog.dao.CategoryDAO;
import users_backlog.dao.IdeaDAO;
import users_backlog.dao.ImplementationDAO;
import users_backlog.dao.RecommendationDAO;
import users_backlog.models.Idea;

@Service
public class IdeaService {

    @Autowired IdeaDAO ideaDAO;
    @Autowired ImplementationDAO implementationDAO;
    @Autowired CategoryDAO categoryDAO;
    @Autowired RecommendationDAO recommendationDAO;

    public List<Idea> getIdeas(String categoryName) {
        return ideaDAO.getIdeas(categoryName);
    }

    public Idea getIdea(final long id) {
        Idea idea = ideaDAO.getIdea(id);
        if (idea != null) {
            idea.setImplementations(implementationDAO.getImplementations(idea));
            idea.setCategories(categoryDAO.getCategories(idea));
        }
        return idea;
    }

    public Idea getIdea(final String summary) {
        Idea idea = ideaDAO.getIdea(summary);
        if (idea != null) {
            idea.setImplementations(implementationDAO.getImplementations(idea));
            idea.setCategories(categoryDAO.getCategories(idea));
            idea.setRecommendations(recommendationDAO.getRecommendations(idea));
        }
        return idea;
    }

    public Idea postIdea(final Idea idea) {
        categoryDAO.postCategories(idea.getCategories());
        ideaDAO.postIdea(idea);
        idea.setCategories(categoryDAO.getCategories(idea));
        return idea;
    }

    public boolean deleteIdea(final long id) {
        return ideaDAO.deleteIdea(id);
    }

}