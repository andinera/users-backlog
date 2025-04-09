package users_backlog.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import users_backlog.dao.IdeaDAO;
import users_backlog.dao.ImplementationDAO;
import users_backlog.dao.InnovatorDAO;
import users_backlog.models.Innovator;

@Service
public class InnovatorService {

    @Autowired InnovatorDAO innovatorDAO;
    @Autowired IdeaDAO ideaDAO;
    @Autowired ImplementationDAO implementationDAO;

    public Innovator getInnovator(final long id) {
        Innovator innovator = innovatorDAO.getInnovator(id);
        if (innovator != null) {
            innovator.setIdeas(ideaDAO.getIdeas(innovator));
            innovator.setImplementations(implementationDAO.getImplementations(innovator));
        }
        return innovator;
    }

    public Innovator getInnovator(final String emailAddress) {
        Innovator innovator = innovatorDAO.getInnovator(emailAddress);
        if (innovator != null) {
            innovator.setIdeas(ideaDAO.getIdeas(innovator));
            innovator.setImplementations(implementationDAO.getImplementations(innovator));
        }
        return innovator;
    }

    public Innovator postInnovator(final Innovator innovator) throws Exception {
        return innovatorDAO.postInnovator(innovator);
    }

}