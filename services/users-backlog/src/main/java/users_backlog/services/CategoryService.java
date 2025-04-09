package users_backlog.services;

import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import users_backlog.dao.CategoryDAO;
import users_backlog.models.Category;

@Service
public class CategoryService {
    
    private static final Logger log = Logger.getLogger(CategoryService.class.getName());

    @Autowired CategoryDAO categoryDAO;

    public List<Category> getAllCategories() {
        return categoryDAO.getAllCategories();
    }

}