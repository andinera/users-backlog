package users_backlog.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import users_backlog.dao.CategoryDAO;
import users_backlog.dao.IdeaDAO;
import users_backlog.dao.ImplementationDAO;
import users_backlog.dao.ProductDAO;
import users_backlog.models.Implementation;
import users_backlog.models.Innovator;
import users_backlog.models.Product;
import users_backlog.models.Recommendation;
import users_backlog.models.Reply;

@Service
public class ImplementationService {

    @Autowired IdeaDAO ideaDAO;
    @Autowired ImplementationDAO implementationDAO;
    @Autowired CategoryDAO categoryDAO;
    @Autowired ProductDAO productDAO;
    @Autowired ElasticSearchService elasticSearchService;

    public List<Implementation> getImplementations(String categoryName) {
        List<Implementation> implementations = implementationDAO.getImplementations(categoryName);
        for (Implementation implementation: implementations) {
            implementation.setCategories(categoryDAO.getCategories(implementation));
        }
        return implementations;
    }

    public List<Implementation> getImplementations(List<Long> ids) {
        List<Implementation> implementations = implementationDAO.getImplementations(ids);
        for (Implementation implementation: implementations) {
            implementation.setCategories(categoryDAO.getCategories(implementation));
        }
        return implementations;
    }

    public Implementation getImplementation(final long id) {
        Implementation implementation = implementationDAO.getImplementation(id);
        if (implementation != null) {
            implementation.setCategories(categoryDAO.getCategories(implementation));
            implementation.setProducts(productDAO.getProducts(implementation));
            implementation.setIdeas(ideaDAO.getIdeas(implementation));
        }
        return implementation;
    }

    public Implementation getImplementation(final String name) {
        Implementation implementation = implementationDAO.getImplementation(name);
        if (implementation != null) {
            implementation.setCategories(categoryDAO.getCategories(implementation));
            implementation.setProducts(productDAO.getProducts(implementation));
            implementation.setIdeas(ideaDAO.getIdeas(implementation));
        }
        return implementation;
    }

    public Implementation postImplementation(final Implementation implementation) {
        categoryDAO.postCategories(implementation.getCategories());
        Implementation updatedImplementation = implementationDAO.postImplementation(implementation);
        elasticSearchService.index(updatedImplementation);
        updatedImplementation.setCategories(categoryDAO.getCategories(updatedImplementation));
        return updatedImplementation;
    }

    public boolean deleteImplementation(final Implementation implementation) {
        boolean deleted = implementationDAO.deleteImplementation(implementation);
        if (deleted) {
            elasticSearchService.delete(implementation);
        }
        return deleted;
    }

    public Product postProduct(final Product product) {
        return productDAO.postProduct(product);
    }

    public boolean deleteProduct(final Product product) {
        return productDAO.deleteProduct(product);
    }

    public Boolean associateWithIdea(final Implementation implementation, final Long ideaId) {
        return implementationDAO.associateWithIdea(implementation, ideaId);
    }

    public Boolean disassociateWithIdea(final Implementation implementation, final Long ideaId) {
        return implementationDAO.disassociateWithIdea(implementation, ideaId);
    }

    public Map<String, Map<Long, Boolean>> getInnovatorVotes(final Long implementationId, final Long innovatorId) {
        Map<String, Map<Long, Boolean>> votes = new HashMap<>();
        Map<Long, Boolean> implementationsVotes = new HashMap<>();
        votes.put("implementations", implementationsVotes);
        implementationsVotes.put(implementationId, implementationDAO.getVote(implementationId, innovatorId));
        votes.put("recommendations", implementationDAO.getRecommendationsVotes(implementationId, innovatorId));
        return votes;
    }

    public Long postVote(Implementation implementation, Innovator innovator, Boolean up) {
        return implementationDAO.postVote(implementation, innovator, up);
    }

    public Recommendation<Implementation> postRecommendation(Recommendation<Implementation> recommendation) {
        return implementationDAO.postRecommendation(recommendation);
    }

    public boolean deleteRecommendation(final Recommendation<Implementation> recommendation) {
        return implementationDAO.deleteRecommendation(recommendation);
    }

    public Long postRecommendationVote(Recommendation<Implementation> recommendation, Innovator innovator, Boolean up) {
        return implementationDAO.postRecommendationVote(recommendation, innovator, up);
    }

    public Reply<Implementation> postRecommendationReply(Reply<Implementation> reply) {
        return implementationDAO.postRecommendationReply(reply);
    }

    public boolean deleteRecommendationReply(final Reply<Implementation> reply) {
        return implementationDAO.deleteRecommendationReply(reply);
    }

}