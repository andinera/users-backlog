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

import users_backlog.models.Innovator;
import users_backlog.services.FirebaseService;
import users_backlog.services.InnovatorService;

@RestController
@RequestMapping("/innovator")
public class InnovatorController {

    private static final Logger log = Logger.getLogger(InnovatorController.class.getName());

    @Autowired InnovatorService innovatorService;
    @Autowired FirebaseService firebaseService;

    @GetMapping(path="getInnovator")
    public ResponseEntity<?> getInnovator(
        @RequestParam(required = false) final Long id,
        @RequestParam(required = false) final String emailAddress
    ) {
        try {
            if (id != null) {
                return ResponseEntity.ok(innovatorService.getInnovator(id));
            } else if (emailAddress != null) {
                return ResponseEntity.ok(innovatorService.getInnovator(emailAddress));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Missing 'id' and 'emailAddress' parameter.");
            }
        } catch (Exception e) {
            log.severe(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        
    }

    @PostMapping(path = "postInnovator")
    public ResponseEntity<?> postInnovator(
        @RequestBody final Innovator innovator
    ) throws Exception {
        try {
            firebaseService.verifyToken(innovator.getIdToken());
            return ResponseEntity.ok(innovatorService.postInnovator(innovator));
        } catch (Exception e) {
            log.severe(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}