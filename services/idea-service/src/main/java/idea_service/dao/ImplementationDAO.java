package idea_service.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
            "impl.id, " +
            "impl.name, " +
            "impl.description, " +
            "inv.email_address " +
        "FROM implementation impl " +
        "INNER JOIN innovator inv " +
            "ON inv.id = impl.innovator_id";

    @Autowired DataSource dataSource;

    private final String GET_IMPLEMENTATION_BY_NAME = 
        GET_ALL_IMPLEMENTATIONS + " " +
        "WHERE impl.name = ?";

    public Implementation getImplementation(final String name) {
        Implementation implementation = null;
        try (PreparedStatement ps = dataSource.getConnection().prepareStatement(GET_IMPLEMENTATION_BY_NAME)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    implementation = implementationMapper(rs);
                }
            }
        } catch (final Exception e) {
            implementation = null;
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return implementation;
    }

    private final String GET_IMPLEMENTATION_BY_ID = 
        GET_ALL_IMPLEMENTATIONS + " " +
        "WHERE impl.id = ?";

    public Implementation getImplementation(final long id) {
        Implementation implementation = null;
        try (PreparedStatement ps = dataSource.getConnection().prepareStatement(GET_IMPLEMENTATION_BY_ID)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    implementation = implementationMapper(rs);
                }
            }
        } catch (final Exception e) {
            implementation = null;
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return implementation;
    }

    private final String GET_IMPLEMENTATIONS_BY_IDEA = 
        GET_ALL_IMPLEMENTATIONS + " " +
        "INNER JOIN idea_implementation ii " +
            "ON (ii.implementation_id = impl.id " +
                "AND ii.idea_id = ?)";

    public List<Implementation> getImplementations(final Idea idea) {
        List<Implementation> implementations = new ArrayList<>();
        try (PreparedStatement ps = dataSource.getConnection().prepareStatement(GET_IMPLEMENTATIONS_BY_IDEA)) {
            ps.setLong(1, idea.getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    implementations.add(implementationMapper(rs));
                }
            }
        } catch (final Exception e) {
            implementations = null;
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return implementations;
    }

    private final String GET_IMPLEMENTATIONS_BY_INNOVATOR = 
        GET_ALL_IMPLEMENTATIONS + " " +
        "WHERE impl.innovator_id = ?";

    public List<Implementation> getImplementations(final Innovator innovator) {
        List<Implementation> implementations = new ArrayList<>();
        try (PreparedStatement ps = dataSource.getConnection().prepareStatement(GET_IMPLEMENTATIONS_BY_INNOVATOR)) {
            ps.setLong(1, innovator.getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    implementations.add(implementationMapper(rs));
                }
            }
        } catch (final Exception e) {
            implementations = null;
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return implementations;
    }

    private final String POST_IMPLEMENTATION = 
        "INSERT " +
        "INTO implementation (innovator_id, name) " +
        "VALUES (?, ?)";

    public Implementation postImplementation(final Implementation implementation) {
        Implementation updatedImplementation = null;
        try (PreparedStatement ps = dataSource.getConnection().prepareStatement(POST_IMPLEMENTATION)) {
            int i = 1;
            ps.setLong(i++, implementation.getInnovator().getId());
            ps.setString(i++, implementation.getName());
            if (ps.executeUpdate() == 0) {
                updatedImplementation = implementation;
            }
        } catch (final Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return associateImplementatioWithIdea(updatedImplementation);
    }

    private final String ASSOCIATE_IMPLEMENTATION_WITH_IDEA =
        "INSERT " +
        "INTO idea_implementation (idea_summary, implementation_id) " +
        "VALUES (?, ?)";

    private Implementation associateImplementatioWithIdea(final Implementation implementation) {
        List<Idea> updatedIdeas = null;
        try (PreparedStatement ps = dataSource.getConnection().prepareStatement(ASSOCIATE_IMPLEMENTATION_WITH_IDEA)) {
            for (Idea idea : implementation.getIdeas()) {
                int i = 1;
                ps.setString(i++, idea.getSummary());
                ps.setLong(i++, implementation.getId());
                ps.addBatch();
            }
            int[] updatedCount = ps.executeBatch();
            updatedIdeas = new ArrayList<>();
            for (int i = 0; i < updatedCount.length; i++) {
                if (updatedCount[i] > 0) {
                    updatedIdeas.add(implementation.getIdeas().get(i));
                }
            }
        } catch (final Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        implementation.setIdeas(updatedIdeas);
        return implementation;
    }

    private Implementation implementationMapper(ResultSet rs) throws Exception {
        Implementation implementation = new Implementation();
        implementation.setId(rs.getLong("id"));
        implementation.setName(rs.getString("name"));
        implementation.setDescription(rs.getString("description"));

        final Innovator innovator = new Innovator();
        innovator.setEmailAddress(rs.getString("email_address"));
        implementation.setInnovator(innovator);

        return implementation;
    }

}