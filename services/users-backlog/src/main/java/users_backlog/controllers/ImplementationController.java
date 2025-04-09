
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

import users_backlog.models.Implementation;
import users_backlog.models.Innovator;
import users_backlog.models.Product;
import users_backlog.models.Recommendation;
import users_backlog.models.Reply;
import users_backlog.services.FirebaseService;
import users_backlog.services.ImplementationService;
import users_backlog.services.InnovatorService;


@RestController
@RequestMapping("/implementation")
public class ImplementationController {

    private static final Logger log = Logger.getLogger(ImplementationController.class.getName());

    @Autowired ImplementationService implementationService;
    @Autowired InnovatorService innovatorService;
    @Autowired FirebaseService firebaseService;

    @GetMapping(path="getImplementations")
    public ResponseEntity<?> getImplementations(
        @RequestParam(required = false) final String categoryName
    ) {
        try {
            return ResponseEntity.ok(implementationService.getImplementations(categoryName));
        } catch (Exception e) {
            log.severe(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping(path= "getImplementation")
    public ResponseEntity<?> getImplementation(
        @RequestParam(required = false) final Long id,
        @RequestParam(required = false) final String name
    ) {
        try {
            if (id != null) {
                return ResponseEntity.ok(implementationService.getImplementation(id));
            } else if (name != null) {
                return ResponseEntity.ok(implementationService.getImplementation(name));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Missing 'id' and 'name' parameter.");
            }
        } catch (Exception e) {
            log.severe(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping(path = "postImplementation")
    public ResponseEntity<?> postImplementation(
        @RequestBody final Implementation implementation
    ) {
        try {
            String emailAddress = firebaseService.verifyToken(implementation.getIdToken());
            if (implementation.getInnovator() != null) {
                Innovator innovator = innovatorService.getInnovator(emailAddress);
                if (innovator.getId() != implementation.getInnovator().getId()) {
                    throw new SecurityException("Unauthorized to create/modify an implementation with another user's ID.");
                }
            }
            return ResponseEntity.ok(implementationService.postImplementation(implementation));
        } catch (SecurityException | IllegalArgumentException | FirebaseAuthException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            log.severe(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // @PostMapping(path = "deleteImplementation")
    // public ResponseEntity<?> deleteImplementation(
    //     @RequestBody final Implementation implementation
    // ) {
    //     try {
    //         firebaseService.verifyToken(implementation.getIdToken());
    //         return ResponseEntity.ok(implementationService.deleteImplementation(implementation));
    //     } catch (SecurityException | IllegalArgumentException | FirebaseAuthException e) {
    //         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
    //     } catch (Exception e) {
    //         log.severe(e.getMessage());
    //         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    //     }
    // }

    @PostMapping(path = "postProduct")
    public ResponseEntity<?> postProduct(
        @RequestBody final Product product
    ) {
        try {
            String emailAddress = firebaseService.verifyToken(product.getIdToken());
            if (product.getImplementation().getInnovator() != null) {
                Innovator innovator = innovatorService.getInnovator(emailAddress);
                if (innovator.getId() != product.getImplementation().getInnovator().getId()) {
                    throw new SecurityException("Unauthorized to add a product to an implementation owned by another user.");
                }
            }
            return ResponseEntity.ok(implementationService.postProduct(product));
        } catch (SecurityException | IllegalArgumentException | FirebaseAuthException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            log.severe(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping(path = "deleteProduct")
    public ResponseEntity<?> deleteProduct(
        @RequestBody final Product product
    ) {
        try {
            String emailAddress = firebaseService.verifyToken(product.getIdToken());
            if (product.getImplementation().getInnovator() != null) {
                Innovator innovator = innovatorService.getInnovator(emailAddress);
                if (innovator.getId() != product.getImplementation().getInnovator().getId()) {
                    throw new SecurityException("Unauthorized to delete a product from an implementation owned by another user.");
                }
            }
            return ResponseEntity.ok(implementationService.deleteProduct(product));
        } catch (SecurityException | IllegalArgumentException | FirebaseAuthException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            log.severe(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping(path = "associateWithIdea")
    public ResponseEntity<?> associateWithIdea(
        @RequestBody final Implementation implementation,
        @RequestParam final Long ideaId
    ) {
        try {
            String emailAddress = firebaseService.verifyToken(implementation.getIdToken());
            if (implementation.getInnovator() != null) {
                Innovator innovator = innovatorService.getInnovator(emailAddress);
                if (innovator.getId() != implementation.getInnovator().getId()) {
                    throw new SecurityException("Unauthorized to associate an idea with an implementation owned by another user.");
                }
            }
            return ResponseEntity.ok(implementationService.associateWithIdea(implementation, ideaId));
        } catch (SecurityException | IllegalArgumentException | FirebaseAuthException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            log.severe(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping(path = "disassociateWithIdea")
    public ResponseEntity<?> disassociateWithIdea(
        @RequestBody final Implementation implementation,
        @RequestParam final Long ideaId
    ) {
        try {
            String emailAddress = firebaseService.verifyToken(implementation.getIdToken());
            if (implementation.getInnovator() != null) {
                Innovator innovator = innovatorService.getInnovator(emailAddress);
                if (innovator.getId() != implementation.getInnovator().getId()) {
                    throw new SecurityException("Unauthorized to disassociate an idea from an implementation owned by another user.");
                }
            }
            return ResponseEntity.ok(implementationService.disassociateWithIdea(implementation, ideaId));
        } catch (SecurityException | IllegalArgumentException | FirebaseAuthException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            log.severe(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping(path = "postVote")
    public ResponseEntity<?> postVote(
        @RequestParam final Boolean up,
        @RequestBody final Implementation implementation
    ) {
        try {
            String emailAddress = firebaseService.verifyToken(implementation.getIdToken());
            Innovator innovator = innovatorService.getInnovator(emailAddress);
            return ResponseEntity.ok(implementationService.postVote(implementation, innovator, up));
        } catch (SecurityException | IllegalArgumentException | FirebaseAuthException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            log.severe(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping(path = "postRecommendation")
    public ResponseEntity<?> postRecommendation(
        @RequestBody final Recommendation<Implementation> recommendation
    ) {
        try {
            String emailAddress = firebaseService.verifyToken(recommendation.getIdToken());
            recommendation.setInnovator(innovatorService.getInnovator(emailAddress));
            return ResponseEntity.ok(implementationService.postRecommendation(recommendation));
        } catch (SecurityException | IllegalArgumentException | FirebaseAuthException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            log.severe(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping(path = "deleteRecommendation")
    public ResponseEntity<?> deleteRecommendation(
        @RequestBody final Recommendation<Implementation> recommendation
    ) {
        try {
            String emailAddress = firebaseService.verifyToken(recommendation.getIdToken());
            Innovator innovator = innovatorService.getInnovator(emailAddress);
            if (innovator.getId() != recommendation.getInnovator().getId()) {
                throw new SecurityException("Unauthorized to delete this recommendation.");
            }
            return ResponseEntity.ok(implementationService.deleteRecommendation(recommendation));
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
        @RequestBody final Recommendation<Implementation> recommendation
    ) {
        try {
            String emailAddress = firebaseService.verifyToken(recommendation.getIdToken());
            Innovator innovator = innovatorService.getInnovator(emailAddress);
            return ResponseEntity.ok(implementationService.postRecommendationVote(recommendation, innovator, up));
        } catch (SecurityException | IllegalArgumentException | FirebaseAuthException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            log.severe(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping(path = "postRecommendationReply")
    public ResponseEntity<?> postRecommendationReply(
        @RequestBody final Reply<Implementation> reply
    ) {
        try {
            String emailAddress = firebaseService.verifyToken(reply.getIdToken());
            reply.setInnovator(innovatorService.getInnovator(emailAddress));
            return ResponseEntity.ok(implementationService.postRecommendationReply(reply));
        } catch (SecurityException | IllegalArgumentException | FirebaseAuthException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            log.severe(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping(path = "deleteRecommendationReply")
    public ResponseEntity<?> deleteRecommendationReply(
        @RequestBody final Reply<Implementation> reply
    ) {
        try {
            String emailAddress = firebaseService.verifyToken(reply.getIdToken());
            Innovator innovator = innovatorService.getInnovator(emailAddress);
            if (innovator.getId() != reply.getInnovator().getId()) {
                throw new SecurityException("Unauthorized to delete this reply.");
            }
            return ResponseEntity.ok(implementationService.deleteRecommendationReply(reply));
        } catch (SecurityException | IllegalArgumentException | FirebaseAuthException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            log.severe(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}