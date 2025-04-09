package idea_service.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import idea_service.models.Innovator;

@Repository
public class InnovatorDAO {

    final private String GET_INNOVATOR = 
        "SELECT inv.email_address " +
        "FROM innovator inv " +
        "WHERE inv.email_address = ?";
    final private String POST_INNOVATOR = 
        "INSERT INTO innovator (email_address) " +
        "VALUES (?)";

    @Autowired DataSource dataSource;

    public Innovator getInnovator(final String emailAddress) {
        Innovator innovator = null;
        try (PreparedStatement ps = dataSource.getConnection().prepareStatement(GET_INNOVATOR)) {
            int i = 1;
            ps.setString(i++, emailAddress);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    innovator = new Innovator();
                    innovator.setEmailAddress(rs.getString("email_address"));
                }
            }
        } catch (final SQLException e) {
            innovator = null;
            System.out.println(e.getMessage());
        }
        return innovator;
    }

    public Innovator postInnovator(final Innovator innovator) {
        Innovator updatedInnovator = null;
        try (PreparedStatement ps = dataSource.getConnection().prepareStatement(POST_INNOVATOR)) {
            int i = 1;
            ps.setString(i++, innovator.getEmailAddress());
            if (ps.executeUpdate() != 0) {
                updatedInnovator = innovator;
            }
        } catch (final SQLException e) {
            System.out.println(e.getMessage());
        }
        return updatedInnovator;
    }

}