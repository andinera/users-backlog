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

    private final String GET_ALL_IMPLEMENTATIONS = 
        "SELECT " +
            "impl.innovator_email_address, " +
            "impl.name, " +
            "impl.description " +
        "FROM implementation impl";
    private final String GET_IMPLEMENTATION_BY_NAME = 
        GET_ALL_IMPLEMENTATIONS + " " +
        "WHERE impl.name = ?";
    private final String GET_IMPLEMENTATIONS_BY_IDEA = 
        GET_ALL_IMPLEMENTATIONS + " " +
        "INNER JOIN idea_implementation ii " +
            "ON ii.implementation_name = impl.name " +
            "AND ii.idea_summary = ?";
    private final String GET_IMPLEMENTATIONS_BY_INNOVATOR = 
        GET_ALL_IMPLEMENTATIONS + " " +
        "WHERE impl.innovator_email_address = ?";
    private final String POST_IMPLEMENTATION = 
        "INSERT " +
        "INTO implementation (innovator_email_address, name) " +
        "VALUES (?, ?)";
    private final String ASSOCIATE_IMPLEMENTATION_WITH_IDEA =
        "INSERT " +
        "INTO idea_implementation (idea_summary, implementation_name) " +
        "VALUES (?, ?)";

    @Autowired DataSource dataSource;

    public Implementation getImplementation(final String name) {
        Implementation implementation = null;
        try (PreparedStatement ps = dataSource.getConnection().prepareStatement(GET_IMPLEMENTATION_BY_NAME)) {
            int i = 1;
            ps.setString(i++, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    implementation = new Implementation();
                    Innovator innovator = new Innovator();
                    innovator.setEmailAddress(rs.getString("innovator_email_address"));
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
        try (PreparedStatement ps = dataSource.getConnection().prepareStatement(GET_IMPLEMENTATIONS_BY_IDEA)) {
            int i = 1;
            ps.setString(i++, idea.getSummary());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    final Implementation implementation = new Implementation();
                    Innovator innovator = new Innovator();
                    innovator.setEmailAddress(rs.getString("innovator_email_address"));
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
        try (PreparedStatement ps = dataSource.getConnection().prepareStatement(GET_IMPLEMENTATIONS_BY_INNOVATOR)) {
            int i = 1;
            ps.setString(i++, innovator.getEmailAddress());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    final Implementation implementation = new Implementation();
                    Innovator queriedInnovator = new Innovator();
                    queriedInnovator.setEmailAddress(rs.getString("innovator_email_address"));
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
        try (PreparedStatement ps = dataSource.getConnection().prepareStatement(POST_IMPLEMENTATION)) {
            int i = 1;
            ps.setString(i++, implementation.getInnovator().getEmailAddress());
            ps.setString(i++, implementation.getName());
            if (ps.executeUpdate() == 0) {
                updatedImplementation = implementation;
            }
        } catch (final SQLException e) {
            System.out.println(e.getMessage());
        }
        return associateImplementatioWithIdea(updatedImplementation);
    }

    private Implementation associateImplementatioWithIdea(final Implementation implementation) {
        List<Idea> updatedIdeas = null;
        try (PreparedStatement ps = dataSource.getConnection().prepareStatement(ASSOCIATE_IMPLEMENTATION_WITH_IDEA)) {
            for (Idea idea : implementation.getIdeas()) {
                int i = 1;
                ps.setString(i++, idea.getSummary());
                ps.setString(i++, implementation.getName());
                ps.addBatch();
            }
            int[] updatedCount = ps.executeBatch();
            updatedIdeas = new ArrayList<>();
            for (int i = 0; i < updatedCount.length; i++) {
                if (updatedCount[i] > 0) {
                    updatedIdeas.add(implementation.getIdeas().get(i));
                }
            }
        } catch (final SQLException e) {
            System.out.println(e.getMessage());
        }
        implementation.setIdeas(updatedIdeas);
        return implementation;
    }

}