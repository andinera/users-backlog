
package users_backlog.controllers;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import users_backlog.models.Idea;
import users_backlog.models.Implementation;
import users_backlog.models.Model;
import users_backlog.services.ElasticSearchService;
import users_backlog.services.IdeaService;
import users_backlog.services.ImplementationService;


@RestController
@RequestMapping("/search")
public class SearchController {

    private static final Logger log = Logger.getLogger(SearchController.class.getName());

    @Autowired ElasticSearchService elasticSearchService;
    @Autowired ImplementationService implementationService;
    @Autowired IdeaService ideaService;

    @GetMapping(path = "search")
    public ResponseEntity<?> search(
        @RequestParam final String criteria
    ) {
        try {
            List<Model> models = elasticSearchService.search(criteria);
            List<Implementation> updatedModels = implementationService.getImplementations(models.stream().filter(model -> model instanceof Implementation).map(model -> model.getId()).collect(Collectors.toList()));
            return ResponseEntity.ok(updatedModels);
        } catch (Exception e) {
            log.severe(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping(path = "searchForIdeas")
    public ResponseEntity<?> searchForIdeas(
        @RequestParam final String partialSummary
    ) {
        try {
            List<Idea> ideas = elasticSearchService.searchForIdeas(partialSummary);
            return ResponseEntity.ok(ideas);
        } catch (Exception e) {
            log.severe(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
