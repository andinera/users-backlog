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

import idea_service.dao.IdeaDAO;
import idea_service.dao.ImplementationDAO;
import idea_service.models.Idea;

@CrossOrigin
@RestController
@RequestMapping("/idea")
public class IdeaController {

    @Autowired IdeaDAO ideaDAO;
    @Autowired ImplementationDAO implementationDAO;

    @GetMapping(path="getAllIdeas")
    public List<Idea> getAllIdeas() {
        return ideaDAO.getAllIdeas();
    }

    @GetMapping(path = "getIdea")
    public Idea getIdea(@RequestParam final String summary) {
        return ideaDAO.getIdea(summary);
    }

    @PostMapping(path = "postIdea")
    public Idea postIdea(@RequestBody final Idea idea) {
        return ideaDAO.postIdea(idea);
    }

    @DeleteMapping(path = "deleteIdea")
    public boolean deleteIdea(@RequestParam final String summary) {
        return ideaDAO.deleteIdea(summary);
    }
}