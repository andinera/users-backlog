package users_backlog.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;


@Repository
public class DevelopmentDAO extends DAO {

    private final String DELETE_EVERYTHING = 
        "DELETE FROM category;" +
        "DELETE FROM idea;" +
        "DELETE FROM idea_category;" +
        "DELETE FROM idea_implementation;" +
        "DELETE FROM idea_recommendation;" +
        "DELETE FROM idea_vote;" +
        "DELETE FROM idea_recommendation_vote;" +
        "DELETE FROM idea_recommendation_reply;" +
        "DELETE FROM implementation;" +
        "DELETE FROM implementation_category;" +
        "DELETE FROM implementation_recommendation;" +
        "DELETE FROM implementation_recommendation_reply;" +
        "DELETE FROM implementation_recommendation_vote;" +
        "DELETE FROM implementation_vote;" +
        "DELETE FROM innovator;" +
        "DELETE FROM product;";

    public void deleteEverything() throws Exception {

        try (Connection connection = dataSource.getConnection(); PreparedStatement ps = connection.prepareStatement(DELETE_EVERYTHING)) {
            ps.executeUpdate();
        }
    }

}