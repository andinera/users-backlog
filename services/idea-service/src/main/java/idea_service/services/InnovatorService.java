package idea_service.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import idea_service.dao.IdeaDAO;
import idea_service.dao.ImplementationDAO;
import idea_service.dao.InnovatorDAO;
import idea_service.models.Innovator;

@Service
public class InnovatorService {

    @Autowired InnovatorDAO innovatorDAO;
    @Autowired IdeaDAO ideaDAO;
    @Autowired ImplementationDAO implementationDAO;

    public Innovator getInnovator(final String emailAddress) {
        Innovator innovator = innovatorDAO.getInnovator(emailAddress);
        innovator.setIdeas(ideaDAO.getIdeas(innovator));
        innovator.setImplementations(implementationDAO.getImplementations(innovator));
        return innovator;
    }

    public Innovator postInnovator(final Innovator innovator) {
        return innovatorDAO.postInnovator(innovator);
    }

}