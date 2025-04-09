package idea_service.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import idea_service.dao.ImplementationDAO;
import idea_service.models.Idea;
import idea_service.services.IdeaService;

@CrossOrigin
@RestController
@RequestMapping("/idea")
public class IdeaController {

    @Autowired IdeaService ideaService;
    @Autowired ImplementationDAO implementationDAO;

    @GetMapping(path="getIdeas")
    public List<Idea> getIdeas(@RequestParam final String categoryName) {
        return ideaService.getIdeas(categoryName);
    }

    @GetMapping(path = "getIdea")
    public Idea getIdea(@RequestParam final String summary) {
        return ideaService.getIdea(summary);
    }

    @PostMapping(path = "postIdea")
    public Idea postIdea(@RequestBody final Idea idea) {
        return ideaService.postIdea(idea);
    }

    @DeleteMapping(path = "deleteIdea")
    public boolean deleteIdea(@RequestParam final String summary) {
        return ideaService.deleteIdea(summary);
    }
}