package idea_service.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import idea_service.models.Idea;
import idea_service.models.Implementation;
import idea_service.models.Innovator;

@Repository
public class ImplementationDAO {
    
    private final String GET_IMPLEMENTATION_SQL = "SELECT impl.innovator, impl.name, impl.description FROM implementation impl WHERE impl.name = ?";
    private final String GET_IMPLEMENTATIONS_BY_IDEA_SQL = "SELECT impl.innovator, impl.name FROM implementation impl WHERE impl.idea = ?";
    private final String GET_IMPLEMENTATIONS_BY_INNOVATOR_SQL = "SELECT impl.innovator, impl.name FROM implementation impl WHERE impl.innovator = ?";
    private final String POST_IMPLEMENTATION_SQL = "INSERT INTO implementation (innovator, idea, name) " + "VALUES (?, ?, ?)";

    @Autowired DataSource dataSource;

    public Implementation getImplementation(final String name) {
        Implementation implementation = null;
        try (PreparedStatement ps = dataSource.getConnection().prepareStatement(GET_IMPLEMENTATION_SQL)) {
            int i = 1;
            ps.setString(i++, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    implementation = new Implementation();
                    Innovator innovator = new Innovator();
                    innovator.setEmailAddress(rs.getString("innovator"));
                    implementation.setInnovator(innovator);
                    implementation.setName(rs.getString("name"));
                    implementation.setDescription(rs.getString("description"));
                }
            }
        } catch (final SQLException e) {
            implementation = null;
            System.out.println(e.getMessage());
        }
        return implementation;
    }

    public List<Implementation> getImplementations(final Idea idea) {
        List<Implementation> implementations = new ArrayList<>();
        try (PreparedStatement ps = dataSource.getConnection().prepareStatement(GET_IMPLEMENTATIONS_BY_IDEA_SQL)) {
            int i = 1;
            ps.setString(i++, idea.getSummary());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    final Implementation implementation = new Implementation();
                    Innovator innovator = new Innovator();
                    innovator.setEmailAddress(rs.getString("innovator"));
                    implementation.setInnovator(innovator);
                    implementation.setName(rs.getString("name"));
                    implementations.add(implementation);
                }
            }
        } catch (final SQLException e) {
            implementations = null;
            System.out.println(e.getMessage());
        }
        return implementations;
    }

    public List<Implementation> getImplementations(final Innovator innovator) {
        List<Implementation> implementations = new ArrayList<>();
        try (PreparedStatement ps = dataSource.getConnection().prepareStatement(GET_IMPLEMENTATIONS_BY_INNOVATOR_SQL)) {
            int i = 1;
            ps.setString(i++, innovator.getEmailAddress());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    final Implementation implementation = new Implementation();
                    Innovator queriedInnovator = new Innovator();
                    queriedInnovator.setEmailAddress(rs.getString("innovator"));
                    implementation.setInnovator(queriedInnovator);
                    implementation.setName(rs.getString("name"));
                    implementations.add(implementation);
                }
            }
        } catch (final SQLException e) {
            implementations = null;
            System.out.println(e.getMessage());
        }
        return implementations;
    }

    public Implementation postImplementation(final Implementation implementation) {
        Implementation updatedImplementation = null;
        try (PreparedStatement ps = dataSource.getConnection().prepareStatement(POST_IMPLEMENTATION_SQL)) {
            int i = 1;
            ps.setString(i++, implementation.getInnovator().getEmailAddress());
            ps.setString(i++, implementation.getIdea().getSummary());
            ps.setString(i++, implementation.getName());
            if (ps.executeUpdate() == 0) {
                updatedImplementation = implementation;
            }
        } catch (final SQLException e) {
            System.out.println(e.getMessage());
        }
        return updatedImplementation;
    }

}