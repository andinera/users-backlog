package users_backlog.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.springframework.stereotype.Repository;

import users_backlog.models.Innovator;

@Repository
public class InnovatorDAO extends DAO {

    final private String GET_ALL_INNOVATORS = 
        "SELECT " +
            "inv.id, " +
            "inv.email_address, " +
            "inv.display_name " +
        "FROM innovator inv";

    final private String GET_INNOVATOR_BY_ID = 
        GET_ALL_INNOVATORS + " " +
        "WHERE inv.id = ?";

    public Innovator getInnovator(final long id) {
        Innovator innovator = null;
        try (Connection connection = dataSource.getConnection(); PreparedStatement ps = connection.prepareStatement(GET_INNOVATOR_BY_ID)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    innovator = new Innovator();
                    innovator.setId(rs.getLong("id"));
                    innovator.setEmailAddress(rs.getString("email_address"));
                    innovator.setDisplayName(rs.getString("display_name"));
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
        try (Connection connection = dataSource.getConnection(); PreparedStatement ps = connection.prepareStatement(GET_INNOVATOR_BY_EMAIL_ADDRESS)) {
            ps.setString(1, emailAddress);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    innovator = new Innovator();
                    innovator.setId(rs.getLong("id"));
                    innovator.setEmailAddress(rs.getString("email_address"));
                    innovator.setDisplayName(rs.getString("display_name"));
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
        "INTO innovator (" +
            "email_address," +
            "display_name" +
        ") " +
        "VALUES (?, ?)";

    public Innovator postInnovator(final Innovator innovator) throws Exception {
        Innovator updatedInnovator = null;
        try (Connection connection = dataSource.getConnection(); PreparedStatement ps = connection.prepareStatement(POST_INNOVATOR)) {
            if (innovator.getDisplayName() == null) {
                innovator.setDisplayName(innovator.getEmailAddress());
            }
            int i = 1;
            ps.setString(i++, innovator.getEmailAddress());
            ps.setString(i++, innovator.getDisplayName());
            if (ps.executeUpdate() != 0) {
                updatedInnovator = innovator;
            }
        } catch (final Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            throw e;
        }
        return updatedInnovator;
    }

}