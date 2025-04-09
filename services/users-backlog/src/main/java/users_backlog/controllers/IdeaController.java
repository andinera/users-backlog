package users_backlog.controllers;

import java.util.logging.Logger;

import com.google.firebase.auth.FirebaseAuthException;

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
import users_backlog.models.Innovator;
import users_backlog.models.Recommendation;
import users_backlog.models.Reply;
import users_backlog.services.FirebaseService;
import users_backlog.services.IdeaService;
import users_backlog.services.InnovatorService;

@RestController
@RequestMapping("/idea")
public class IdeaController {
    
    private static final Logger log = Logger.getLogger(IdeaController.class.getName());

    @Autowired IdeaService ideaService;
    @Autowired InnovatorService innovatorService;
    @Autowired FirebaseService firebaseService;

    @GetMapping(path="getIdeas")
    public ResponseEntity<?> getIdeas(
        @RequestParam(required = false) final String categoryName
    ) {
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
        @RequestBody final Idea idea
    ) {
        try {
            firebaseService.verifyToken(idea.getIdToken());
            return ResponseEntity.ok(ideaService.deleteIdea(idea));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            log.severe(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping(path = "postVote")
    public ResponseEntity<?> postVote(
        @RequestParam final Boolean up,
        @RequestBody final Idea idea
    ) {
        try {
            String emailAddress = firebaseService.verifyToken(idea.getIdToken());
            Innovator innovator = innovatorService.getInnovator(emailAddress);
            return ResponseEntity.ok(ideaService.postVote(idea, innovator, up));
        } catch (SecurityException | IllegalArgumentException | FirebaseAuthException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            log.severe(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping(path = "postRecommendation")
    public ResponseEntity<?> postRecommendation(
        @RequestBody final Recommendation<Idea> recommendation
    ) {
        try {
            firebaseService.verifyToken(recommendation.getIdToken());
            return ResponseEntity.ok(ideaService.postRecommendation(recommendation));
        } catch (SecurityException | IllegalArgumentException | FirebaseAuthException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            log.severe(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping(path = "deleteRecommendation")
    public ResponseEntity<?> deleteRecommendation(
        @RequestBody final Recommendation<Idea> recommendation
    ) {
        try {
            firebaseService.verifyToken(recommendation.getIdToken());
            return ResponseEntity.ok(ideaService.deleteRecommendation(recommendation));
        } catch (SecurityException | IllegalArgumentException | FirebaseAuthException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            log.severe(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping(path = "postRecommendationVote")
    public ResponseEntity<?> postRecommendationVote(
        @RequestParam final Boolean up,
        @RequestBody final Recommendation<Idea> recommendation
    ) {
        try {
            String emailAddress = firebaseService.verifyToken(recommendation.getIdToken());
            Innovator innovator = innovatorService.getInnovator(emailAddress);
            return ResponseEntity.ok(ideaService.postRecommendationVote(recommendation, innovator, up));
        } catch (SecurityException | IllegalArgumentException | FirebaseAuthException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            log.severe(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping(path = "postRecommendationReply")
    public ResponseEntity<?> postRecommendationReply(
        @RequestBody final Reply<Idea> reply
    ) {
        try {
            firebaseService.verifyToken(reply.getIdToken());
            return ResponseEntity.ok(ideaService.postRecommendationReply(reply));
        } catch (SecurityException | IllegalArgumentException | FirebaseAuthException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            log.severe(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping(path = "deleteRecommendationReply")
    public ResponseEntity<?> deleteRecommendationReply(
        @RequestBody final Reply<Idea> reply
    ) {
        try {
            firebaseService.verifyToken(reply.getIdToken());
            return ResponseEntity.ok(ideaService.deleteRecommendationReply(reply));
        } catch (SecurityException | IllegalArgumentException | FirebaseAuthException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            log.severe(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}