
package idea_service.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import idea_service.models.Implementation;
import idea_service.services.ImplementationService;

@CrossOrigin
@RestController
@RequestMapping("/implementation")
public class ImplementationController {

    @Autowired ImplementationService implementationService;

    @GetMapping(path= "getImplementation")
    public Implementation getImplementation(@RequestParam final String name) {
        return implementationService.getImplementation(name);
    }

    @PostMapping(path = "postImplementation")
    public Implementation postImplementation(@RequestBody final Implementation implementation) {
        return implementationService.postImplementation(implementation);
    }
}