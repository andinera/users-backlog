package users_backlog.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Logger;

import org.springframework.stereotype.Repository;

import users_backlog.models.Innovator;

@Repository
public class InnovatorDAO extends DAO {

    private static final Logger log = Logger.getLogger(InnovatorDAO.class.getName());

    final private String GET_ALL_INNOVATORS = 
        "SELECT " +
            "inv.id, " +
            "inv.email_address, " +
            "inv.hide_email_address, " +
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
                    innovator = innovatorMapper(rs);
                }
            }
        } catch (final Exception e) {
            innovator = null;
            log.severe(e.getMessage());
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
                    innovator = innovatorMapper(rs);
                }
            }
        } catch (final Exception e) {
            innovator = null;
            log.severe(e.getMessage());
        }
        return innovator;
    }

    private Innovator innovatorMapper(ResultSet rs) throws Exception {
        Innovator innovator = new Innovator();
        innovator.setId(rs.getLong("id"));
        innovator.setEmailAddress(rs.getString("email_address"));
        innovator.setHideEmailAddress(rs.getInt("hide_email_address") == 1);
        innovator.setDisplayName(rs.getString("display_name"));
        return innovator;
    }

    private final String INSERT_INNOVATOR = 
        "INSERT " +
        "INTO innovator (" +
            "email_address," +
            "hide_email_address, " +
            "display_name" +
        ") " +
        "VALUES (?, ?, ?)";
    private final String UPDATE_INNOVATOR = 
        "UPDATE innovator " +
        "SET email_address = ?, hide_email_address = ?, display_name = ? " +
        "WHERE id = ?";

    public Innovator postInnovator(final Innovator innovator) throws Exception {

        String sql = null;
        if (this.getInnovator(innovator.getId()) == null) {
            sql = INSERT_INNOVATOR;
        } else {
            sql = UPDATE_INNOVATOR;
        }

        Innovator updatedInnovator = null;
        try (Connection connection = dataSource.getConnection(); PreparedStatement ps = connection.prepareStatement(sql)) {
            if (innovator.getDisplayName() == null) {
                innovator.setDisplayName(innovator.getEmailAddress());
            }
            int i = 1;
            ps.setString(i++, innovator.getEmailAddress());
            ps.setInt(i++, innovator.getHideEmailAddress() ? 1 : 0);
            ps.setString(i++, innovator.getDisplayName());
            if (sql.equals(UPDATE_INNOVATOR)) {
                ps.setLong(i++, innovator.getId());
            }
            if (ps.executeUpdate() != 0) {
                updatedInnovator = innovator;
            }
        } catch (final Exception e) {
            log.severe(e.getMessage());
            throw e;
        }
        return updatedInnovator;
    }

}