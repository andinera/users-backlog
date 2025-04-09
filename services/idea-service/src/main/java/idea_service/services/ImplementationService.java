package idea_service.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import idea_service.dao.IdeaDAO;
import idea_service.dao.ImplementationDAO;
import idea_service.dao.ProductDAO;
import idea_service.models.Implementation;

@Service
public class ImplementationService {

    @Autowired IdeaDAO ideaDAO;
    @Autowired ImplementationDAO implementationDAO;
    @Autowired ProductDAO productDAO;

    public Implementation getImplementation(final String name) {
        Implementation implementation = implementationDAO.getImplementation(name);
        if (implementation != null) {
            implementation.setProducts(productDAO.getProducts(implementation));
            implementation.setIdeas(ideaDAO.getIdeas(implementation));
        }
        return implementation;
    }

    public Implementation postImplementation(final Implementation implementation) {
        return implementationDAO.postImplementation(implementation);
    }

}