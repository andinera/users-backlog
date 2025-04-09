package users_backlog.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import users_backlog.models.Idea;
import users_backlog.services.IdeaService;

@RestController
@RequestMapping("/idea")
public class IdeaController {

    @Autowired IdeaService ideaService;

    @GetMapping(path="getIdeas")
    public List<Idea> getIdeas(@RequestParam final String categoryName) {
        return ideaService.getIdeas(categoryName);
    }

    @GetMapping(path = "getIdea")
    public Idea getIdea(
        @RequestParam(required = false) final Long id,
        @RequestParam(required = false) final String summary
    ) {
        if (id != null) {
            return ideaService.getIdea(id);
        } else if (summary != null) {
            return ideaService.getIdea(summary);
        } else {
            return null;
            // throw error, identical to if no parameters are passed
        }
    }

    @PostMapping(path = "postIdea")
    public Idea postIdea(@RequestBody final Idea idea) {
        return ideaService.postIdea(idea);
    }

    @DeleteMapping(path = "deleteIdea")
    public boolean deleteIdea(@RequestParam final long id) {
        return ideaService.deleteIdea(id);
    }
}