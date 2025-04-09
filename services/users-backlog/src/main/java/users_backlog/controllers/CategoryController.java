package users_backlog.controllers;

import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import users_backlog.models.Category;
import users_backlog.services.CategoryService;

@RestController
@RequestMapping("/category")
public class CategoryController {

    private static final Logger log = Logger.getLogger(CategoryController.class.getName());

    @Autowired CategoryService categoryService;

    @GetMapping(path="getAllCategories")
    public List<Category> getAllCategories() {
        return categoryService.getAllCategories();
    }
}