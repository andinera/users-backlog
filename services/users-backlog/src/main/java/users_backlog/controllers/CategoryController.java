package users_backlog.controllers;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import users_backlog.services.CategoryService;

@RestController
@RequestMapping("/category")
public class CategoryController {

    private static final Logger log = Logger.getLogger(CategoryController.class.getName());

    @Autowired CategoryService categoryService;

    @GetMapping(path="getAllCategories")
    public ResponseEntity<?> getAllCategories() {
        try {
            return ResponseEntity.ok(categoryService.getAllCategories());
        } catch (Exception e) {
            log.severe(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}