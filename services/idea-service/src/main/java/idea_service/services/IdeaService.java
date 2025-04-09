package idea_service.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import idea_service.dao.IdeaDAO;
import idea_service.dao.ImplementationDAO;
import idea_service.models.Idea;

@Service
public class IdeaService {

    @Autowired IdeaDAO ideaDAO;
    @Autowired ImplementationDAO implementationDAO;

    public List<Idea> getAllIdeas() {
        return ideaDAO.getAllIdeas();
    }

    public Idea getIdea(final String summary) {
        Idea idea = ideaDAO.getIdea(summary);
        if (idea != null) {
            idea.setImplementations(implementationDAO.getImplementations(idea));
        }
        return idea;
    }

    public Idea postIdea(final Idea idea) {
        return ideaDAO.postIdea(idea);
    }

    public boolean deleteIdea(final String summary) {
        return ideaDAO.deleteIdea(summary);
    }

}