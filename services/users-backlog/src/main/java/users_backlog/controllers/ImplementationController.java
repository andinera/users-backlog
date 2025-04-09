
package users_backlog.controllers;

import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

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
    public ResponseEntity<List<Implementation>> getImplementations(
        @RequestParam(required = false) final String categoryName
    ) {
        List<Implementation> implementations = implementationService.getImplementations(categoryName);
        return new ResponseEntity<List<Implementation>>(implementations, HttpStatus.OK);
    }

    @GetMapping(path= "getImplementation")
    public Implementation getImplementation(
        @RequestParam(required = false) final Long id,
        @RequestParam(required = false) final String name
    ) {
        if (id != null) {
            return implementationService.getImplementation(id);
        } else if (name != null) {
            return implementationService.getImplementation(name);
        } else {
            return null;
            // throw error, identical to if no parameters are passed
        }
    }

    @PostMapping(path = "postImplementation")
    public Implementation postImplementation(@RequestBody final Implementation implementation) {
        return implementationService.postImplementation(implementation);
    }

    @PostMapping(path = "postVote")
    public ResponseEntity<Long> postVote(
        @RequestParam final Long implementationId,
        @RequestParam final Long innovatorId,
        @RequestParam final Boolean up,
        HttpServletRequest request
    ) {
        String emailAddress;
        try {
            emailAddress = firebaseService.verifyToken(request.getHeader("ID-TOKEN"));
        } catch (Exception e) {
            return new ResponseEntity<Long>(0L, HttpStatus.UNAUTHORIZED);
        }
        
        if (innovatorService.getInnovator(emailAddress) != null) {
            return new ResponseEntity<Long>(implementationService.postVote(implementationId, innovatorId, up), HttpStatus.OK);
        } else {
            return new ResponseEntity<Long>(0L, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(path = "postRecommendation")
    public Recommendation postRecommendation(
        @RequestBody final Recommendation recommendation
    ) {
        return implementationService.postRecommendation(recommendation);
    }

    @PostMapping(path = "postRecommendationVote")
    public Long postRecommendationVote(
        @RequestParam final Long recommendationId,
        @RequestParam final Long innovatorId,
        @RequestParam final Boolean up
    ) {
        return implementationService.postRecommendationVote(recommendationId, innovatorId, up);
    }

    @PostMapping(path = "postRecommendationReply")
    public Reply postRecommendationReply(
        @RequestBody final Reply reply
    ) {
        return implementationService.postRecommendationReply(reply);
    }
}