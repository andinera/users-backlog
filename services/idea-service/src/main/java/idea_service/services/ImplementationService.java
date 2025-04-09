package idea_service.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import idea_service.dao.CategoryDAO;
import idea_service.dao.IdeaDAO;
import idea_service.dao.ImplementationDAO;
import idea_service.dao.ProductDAO;
import idea_service.models.Implementation;

@Service
public class ImplementationService {

    @Autowired IdeaDAO ideaDAO;
    @Autowired ImplementationDAO implementationDAO;
    @Autowired CategoryDAO categoryDAO;
    @Autowired ProductDAO productDAO;

    public List<Implementation> getImplementations(String categoryName) {
        return implementationDAO.getImplementations(categoryName);
    }

    public Implementation getImplementation(final long id) {
        Implementation implementation = implementationDAO.getImplementation(id);
        if (implementation != null) {
            implementation.setProducts(productDAO.getProducts(implementation));
            implementation.setIdeas(ideaDAO.getIdeas(implementation));
        }
        return implementation;
    }

    public Implementation getImplementation(final String name) {
        Implementation implementation = implementationDAO.getImplementation(name);
        if (implementation != null) {
            implementation.setProducts(productDAO.getProducts(implementation));
            implementation.setIdeas(ideaDAO.getIdeas(implementation));
        }
        return implementation;
    }

    public Implementation postImplementation(final Implementation implementation) {
        categoryDAO.postCategories(implementation.getCategories());
        implementationDAO.postImplementation(implementation);
        implementation.setCategories(categoryDAO.getCategories(implementation));
        return implementation;
    }

}