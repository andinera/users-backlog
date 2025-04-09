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
import idea_service.models.Innovator;

@Repository
public class IdeaDAO {

    @Autowired DataSource dataSource;
    @Autowired ImplementationDAO implementationDAO;

    private final String GET_ALL_IDEAS_SQL = "SELECT idea.summary, idea.description, idea.innovator " + "FROM idea";
    private final String GET_IDEA_SQL = GET_ALL_IDEAS_SQL + " " + "WHERE idea.summary = ?";
    private final String POST_IDEA_SQL = "INSERT INTO idea (summary, description, innovator) " + "VALUES (?, ?, ?)";
    private final String DELETE_IDEA_SQL = "DELETE " +
                                        "FROM idea " +
                                        "WHERE idea.summary = ?";

    public List<Idea> getAllIdeas() {
        final List<Idea> ideas = new ArrayList<>();
        try (PreparedStatement ps = dataSource.getConnection().prepareStatement(GET_ALL_IDEAS_SQL)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    final Idea idea = new Idea();
                    idea.setSummary(rs.getString("summary"));
                    idea.setDescription(rs.getString("description"));
                    final Innovator innovator = new Innovator();
                    innovator.setEmailAddress(rs.getString("innovator"));
                    idea.setInnovator(innovator);
                    ideas.add(idea);
                }
            }
        } catch (final SQLException e) {
            System.out.println(e.getMessage());
        }
        return ideas;
    }

    public Idea getIdea(final String summary) {
        Idea idea = null;
        try (PreparedStatement ps = dataSource.getConnection().prepareStatement(GET_IDEA_SQL)) {
            int i = 1;
            ps.setString(i++, summary);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    idea = new Idea();
                    idea.setSummary(rs.getString("summary"));
                    idea.setDescription(rs.getString("description"));
                    final Innovator innovator = new Innovator();
                    innovator.setEmailAddress(rs.getString("innovator"));
                    idea.setInnovator(innovator);
                    idea.setImplementations(implementationDAO.getImplementations(summary));
                }
            }
        } catch (final SQLException e) {
            System.out.println(e.getMessage());
        }
        return idea;
    }

    public Idea postIdea(final Idea idea) {
        Idea updatedIdea = null;
        try (PreparedStatement ps = dataSource.getConnection().prepareStatement(POST_IDEA_SQL)) {
            int i = 1;
            ps.setString(i++, idea.getSummary());
            ps.setString(i++, idea.getDescription());
            ps.setString(i++, idea.getInnovator().getEmailAddress());
            if (ps.executeUpdate() != 0) {
                updatedIdea = idea;
            }
        } catch (final SQLException e) {
            System.out.println(e.getMessage());
        }
        return updatedIdea;
    }

    public boolean deleteIdea(final String summary) {
        boolean deleted = false;
        try (PreparedStatement ps = dataSource.getConnection().prepareStatement(DELETE_IDEA_SQL)) {
            int i = 1;
            ps.setString(i++, summary);
            deleted = (ps.executeUpdate() != 0);
        } catch (final SQLException e) {
            System.out.println(e.getMessage());
        }
        return deleted;
    }
}