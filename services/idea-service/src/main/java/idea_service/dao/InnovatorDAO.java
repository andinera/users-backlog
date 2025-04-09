package idea_service.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import idea_service.models.Innovator;

@Repository
public class InnovatorDAO {

    final private String GET_ALL_INNOVATORS = 
        "SELECT " +
            "inv.id, " +
            "inv.email_address " +
        "FROM innovator inv";

    @Autowired DataSource dataSource;

    final private String GET_INNOVATOR_BY_ID = 
        GET_ALL_INNOVATORS + " " +
        "WHERE inv.id = ?";

    public Innovator getInnovator(final long id) {
        Innovator innovator = null;
        try (PreparedStatement ps = dataSource.getConnection().prepareStatement(GET_INNOVATOR_BY_ID)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    innovator = new Innovator();
                    innovator.setId(rs.getLong("id"));
                    innovator.setEmailAddress(rs.getString("email_address"));
                }
            }
        } catch (final Exception e) {
            innovator = null;
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return innovator;
    }

    final private String GET_INNOVATOR_BY_EMAIL_ADDRESS = 
        GET_ALL_INNOVATORS + " " +
        "WHERE inv.email_address = ?";

    public Innovator getInnovator(final String emailAddress) {
        Innovator innovator = null;
        try (PreparedStatement ps = dataSource.getConnection().prepareStatement(GET_INNOVATOR_BY_EMAIL_ADDRESS)) {
            ps.setString(1, emailAddress);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    innovator = new Innovator();
                    innovator.setId(rs.getLong("id"));
                    innovator.setEmailAddress(rs.getString("email_address"));
                }
            }
        } catch (final Exception e) {
            innovator = null;
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return innovator;
    }

    final private String POST_INNOVATOR = 
        "INSERT " +
        "INTO innovator (email_address) " +
        "VALUES (?)";

    public Innovator postInnovator(final Innovator innovator) {
        Innovator updatedInnovator = null;
        try (PreparedStatement ps = dataSource.getConnection().prepareStatement(POST_INNOVATOR)) {
            ps.setString(1, innovator.getEmailAddress());
            if (ps.executeUpdate() != 0) {
                updatedInnovator = innovator;
            }
        } catch (final Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return updatedInnovator;
    }

}