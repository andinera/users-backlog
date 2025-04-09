package users_backlog.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import users_backlog.models.Innovator;
import users_backlog.services.InnovatorService;

@CrossOrigin
@RestController
@RequestMapping("/innovator")
public class InnovatorController {

    @Autowired InnovatorService innovatorService;

    @GetMapping(path="getInnovator")
    public Innovator getInnovator(
        @RequestParam(required = false) final Long id,
        @RequestParam(required = false) final String emailAddress
    ) {
        if (id != null) {
            return innovatorService.getInnovator(id);
        } else if (emailAddress != null) {
            return innovatorService.getInnovator(emailAddress);
        } else {
            return null;
            // throw error, identical to if no parameters are passed
        }
        
    }

    @PostMapping(path = "postInnovator")
    public Innovator postInnovator(@RequestBody final Innovator innovator) throws Exception {
        return innovatorService.postInnovator(innovator);
    }
}