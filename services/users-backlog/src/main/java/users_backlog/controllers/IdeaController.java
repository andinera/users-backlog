package users_backlog.controllers;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import users_backlog.models.Idea;
import users_backlog.models.Model;
import users_backlog.services.FirebaseService;
import users_backlog.services.IdeaService;

@RestController
@RequestMapping("/idea")
public class IdeaController {
    
    private static final Logger log = Logger.getLogger(IdeaController.class.getName());

    @Autowired IdeaService ideaService;
    @Autowired FirebaseService firebaseService;

    @GetMapping(path="getIdeas")
    public ResponseEntity<?> getIdeas(@RequestParam final String categoryName) {
        try {
            return ResponseEntity.ok(ideaService.getIdeas(categoryName));
        } catch (Exception e) {
            log.severe(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping(path = "getIdea")
    public ResponseEntity<?> getIdea(
        @RequestParam(required = false) final Long id,
        @RequestParam(required = false) final String summary
    ) {
        try {
            if (id != null) {
                return ResponseEntity.ok(ideaService.getIdea(id));
            } else if (summary != null) {
                return ResponseEntity.ok(ideaService.getIdea(summary));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Missing 'id' and 'summary' parameter.");
            }
        } catch (Exception e) {
            log.severe(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping(path = "postIdea")
    public ResponseEntity<?> postIdea(
        @RequestBody final Idea idea
    ) {
        try {
            firebaseService.verifyToken(idea.getIdToken());
            return ResponseEntity.ok(ideaService.postIdea(idea));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            log.severe(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping(path = "deleteIdea")
    public ResponseEntity<?> deleteIdea(
        @RequestParam final long id,
        @RequestBody final Model model
    ) {
        try {
            firebaseService.verifyToken(model.getIdToken());
            return ResponseEntity.ok(ideaService.deleteIdea(id));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            log.severe(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}