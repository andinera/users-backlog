package idea_service.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import idea_service.models.Implementation;
import idea_service.models.Innovator;

@Repository
public class ImplementationDAO {
    
    private final String GET_IMPLEMENTATIONS_SQL = "SELECT impl.source, impl.implementer FROM implementation impl WHERE impl.idea = ?";
    private final String POST_IMPLEMENTATION_SQL = "INSERT INTO implementation (source, implementer, idea) " + "VALUES (?, ?, ?)";

    @Autowired DataSource dataSource;

    public List<Implementation> getImplementations(final String summary) {
        final List<Implementation> implementations = new ArrayList<>();
        try (PreparedStatement ps = dataSource.getConnection().prepareStatement(GET_IMPLEMENTATIONS_SQL)) {
            int i = 1;
            ps.setString(i++, summary);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    final Implementation implementation = new Implementation();
                    implementation.setSource(rs.getString("source"));
                    Innovator innovator = new Innovator();
                    innovator.setEmailAddress(rs.getString("implementer"));
                    implementation.setImplementer(innovator);
                    implementations.add(implementation);
                }
            }
        } catch (final SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
        return implementations;
    }

    public Implementation postImplementation(final Implementation implementation) {
        try (PreparedStatement ps = dataSource.getConnection().prepareStatement(POST_IMPLEMENTATION_SQL)) {
            int i = 1;
            ps.setString(i++, implementation.getSource());
            ps.setString(i++, implementation.getImplementer().getEmailAddress());
            ps.setString(i++, implementation.getIdea().getSummary());
            if (ps.executeUpdate() == 0) {
                return null;
            }
        } catch (final SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
        return implementation;
    }

}