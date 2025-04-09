
package users_backlog.controllers;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import users_backlog.services.DevelopmentService;
import users_backlog.services.ElasticSearchService;


@RestController
@RequestMapping("/development")
public class DevelopmentController {

    private static final Logger log = Logger.getLogger(DevelopmentController.class.getName());

    @Autowired DevelopmentService developmentService;
    @Autowired ElasticSearchService elasticSearchService;

    @DeleteMapping(path="deleteEverything")
    public ResponseEntity<?> deleteEverything(
    ) {
        String environment = System.getenv("environment");
        if (environment != null && environment.equals("development")) {
            try {
                developmentService.deleteEverything();
                elasticSearchService.deleteEverything();
                return ResponseEntity.ok("Deleted everything.");
            } catch (Exception e) {
                log.severe(e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
            }
        } else {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("This endpoint is only functional in development.");
        }
    }
}