package users_backlog.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import users_backlog.dao.DevelopmentDAO;


@Service
public class DevelopmentService {

    @Autowired DevelopmentDAO developmentDAO;

    public void deleteEverything() throws Exception {
        developmentDAO.deleteEverything();
    }

}