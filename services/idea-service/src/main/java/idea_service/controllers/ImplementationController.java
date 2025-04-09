
package idea_service.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import idea_service.models.Implementation;
import idea_service.models.Recommendation;
import idea_service.services.ImplementationService;

@CrossOrigin
@RestController
@RequestMapping("/implementation")
public class ImplementationController {

    @Autowired ImplementationService implementationService;

    @GetMapping(path="getImplementations")
    public List<Implementation> getImplementations(
        @RequestParam(required = false) final String categoryName
    ) {
        return implementationService.getImplementations(categoryName);
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
    public Long postVote(
        @RequestParam final Long implementationId,
        @RequestParam final Long innovatorId,
        @RequestParam final Boolean up
    ) {
        return implementationService.postVote(implementationId, innovatorId, up);
    }

    @PostMapping(path = "postRecommendation")
    public Recommendation postRecommendation(
        @RequestBody final Recommendation recommendation
    ) {
        return implementationService.postRecommendation(recommendation);
    }
}