package idea_service.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import idea_service.models.Innovator;
import idea_service.dao.InnovatorDAO;

@CrossOrigin
@RestController
@RequestMapping("/innovator")
public class InnovatorController {

    @Autowired InnovatorDAO innovatorDAO;

    @GetMapping(path="getInnovator")
    public Innovator getInnovator(@RequestParam final String emailAddress) {
        return innovatorDAO.getInnovator(emailAddress);
    }

    @PostMapping(path = "postInnovator")
    public Innovator postInnovator(@RequestBody final Innovator innovator) {
        return innovatorDAO.postInnovator(innovator);
    }
}