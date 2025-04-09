package idea_service.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import idea_service.models.Category;
import idea_service.models.Idea;
import idea_service.models.Innovator;

@Repository
public class IdeaDAO {

    @Autowired DataSource dataSource;

    private final String GET_ALL_IDEAS = 
        "SELECT idea.summary, " +
            "idea.description, " +
            "idea.innovator_email_address " +
        "FROM idea";
    private final String GET_IDEAS_BY_CATEGORY_NAME = 
        GET_ALL_IDEAS + " " +
        "JOIN idea_category ic " +
            "ON idea.summary = ic.idea_summary " +
        "WHERE ic.category_name = ?";
    private final String GET_IDEAS_BY_INNOVATOR = 
        GET_ALL_IDEAS + " " +
        "WHERE idea.innovator_email_address = ?";
    private final String GET_IDEA = 
        GET_ALL_IDEAS + " " + 
        "WHERE idea.summary = ?";
    private final String POST_IDEA = 
        "INSERT INTO idea (summary, description, innovator_email_address) " +
        "VALUES (?, ?, ?)";
    private final String ASSOCIATE_IDEA_WITH_CATEGORY =
        "INSERT INTO idea_category (idea_summary, category_name) " +
        "VALUES (?, ?)";
    private final String DELETE_IDEA = 
        "DELETE FROM idea " +
        "WHERE idea.summary = ?";

    public List<Idea> getIdeas(final String categoryName) {
        List<Idea> ideas = new ArrayList<>();
        try (PreparedStatement ps = dataSource.getConnection().prepareStatement(GET_IDEAS_BY_CATEGORY_NAME)) {
            int i = 1;
            ps.setString(i++, categoryName);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    final Idea idea = new Idea();
                    idea.setSummary(rs.getString("summary"));
                    ideas.add(idea);
                }
            }
        } catch (final SQLException e) {
            ideas = null;
            System.out.println(e.getMessage());
        }
        return ideas;
    }

    public List<Idea> getIdeas(final Innovator innovator) {
        List<Idea> ideas = new ArrayList<>();
        try (PreparedStatement ps = dataSource.getConnection().prepareStatement(GET_IDEAS_BY_INNOVATOR)) {
            int i = 1;
            ps.setString(i++, innovator.getEmailAddress());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    final Idea idea = new Idea();
                    idea.setSummary(rs.getString("summary"));
                    idea.setDescription(rs.getString("description"));
                    ideas.add(idea);
                }
            }
        } catch (final SQLException e) {
            ideas = null;
            System.out.println(e.getMessage());
        }
        return ideas;
    }

    public Idea getIdea(final String summary) {
        Idea idea = null;
        try (PreparedStatement ps = dataSource.getConnection().prepareStatement(GET_IDEA)) {
            int i = 1;
            ps.setString(i++, summary);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    idea = new Idea();
                    idea.setSummary(rs.getString("summary"));
                    idea.setDescription(rs.getString("description"));
                    final Innovator innovator = new Innovator();
                    innovator.setEmailAddress(rs.getString("innovator_email_address"));
                    idea.setInnovator(innovator);
                }
            }
        } catch (final SQLException e) {
            idea = null;
            System.out.println(e.getMessage());
        }
        return idea;
    }

    public Idea postIdea(final Idea idea) {
        Idea updatedIdea = null;
        try (PreparedStatement ps = dataSource.getConnection().prepareStatement(POST_IDEA)) {
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
        return associateIdeaWithCategory(updatedIdea);
    }

    private Idea associateIdeaWithCategory(final Idea idea) {
        List<Category> updatedCategories = null;
        try (PreparedStatement ps = dataSource.getConnection().prepareStatement(ASSOCIATE_IDEA_WITH_CATEGORY)) {
            for (Category category : idea.getCategories()) {
                int i = 1;
                ps.setString(i++, idea.getSummary());
                ps.setString(i++, category.getName());
                ps.addBatch();
            }
            int[] updatedCount = ps.executeBatch();
            updatedCategories = new ArrayList<>();
            for (int i = 0; i < updatedCount.length; i++) {
                if (updatedCount[i] > 0) {
                    updatedCategories.add(idea.getCategories().get(i));
                }
            }
        } catch (final SQLException e) {
            System.out.println(e.getMessage());
        }
        idea.setCategories(updatedCategories);
        return idea;
    }

    public boolean deleteIdea(final String summary) {
        boolean deleted = false;
        try (PreparedStatement ps = dataSource.getConnection().prepareStatement(DELETE_IDEA)) {
            int i = 1;
            ps.setString(i++, summary);
            deleted = (ps.executeUpdate() != 0);
        } catch (final SQLException e) {
            System.out.println(e.getMessage());
        }
        return deleted;
    }
}