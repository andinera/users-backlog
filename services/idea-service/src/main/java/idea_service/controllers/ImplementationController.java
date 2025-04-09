
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

import idea_service.dao.ImplementationDAO;
import idea_service.models.Implementation;

@CrossOrigin
@RestController
@RequestMapping("/implementation")
public class ImplementationController {

    @Autowired ImplementationDAO implementationDAO;

    @GetMapping(path= "getImplementation")
    public Implementation getImplementation(@RequestParam final String name) {
        return implementationDAO.getImplementation(name);
    }

    @GetMapping(path= "getImplementations")
    public List<Implementation> getImplementations(@RequestParam final String summary) {
        return implementationDAO.getImplementations(summary);
    } 

    @PostMapping(path = "postImplementation")
    public Implementation postImplementation(@RequestBody final Implementation implementation) {
        return implementationDAO.postImplementation(implementation);
    }
}