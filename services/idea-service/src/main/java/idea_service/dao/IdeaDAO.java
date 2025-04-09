package idea_service.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import idea_service.models.Category;
import idea_service.models.Idea;
import idea_service.models.Implementation;
import idea_service.models.Innovator;

@Repository
public class IdeaDAO extends DAO {

    private final String GET_ALL_IDEAS = 
        "SELECT " +
            "idea.id, " +
            "idea.summary, " +
            "idea.description, " +
            "inv.email_address " +
        "FROM idea " +
        "INNER JOIN innovator inv " +
            "ON inv.id = idea.innovator_id";

    private final String GET_IDEAS_BY_CATEGORY = 
        GET_ALL_IDEAS + " " +
        "INNER JOIN idea_category ic " +
            "INNER JOIN category cat " +
                "ON (cat.id = ic.category_id " +
                    "AND cat.name = ?) " +
            "ON ic.idea_id = idea.id";

    public List<Idea> getIdeas(final String categoryName) {
        List<Idea> ideas = new ArrayList<>();
        try (PreparedStatement ps = dataSource.getConnection().prepareStatement(GET_IDEAS_BY_CATEGORY)) {
            ps.setString(1, categoryName);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    final Idea idea = ideaMapper(rs);
                    ideas.add(idea);
                }
            }
        } catch (final Exception e) {
            ideas = null;
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return ideas;
    }

    private final String GET_IDEAS_BY_IMPLEMENTATION = 
        GET_ALL_IDEAS + " " +
        "INNER JOIN idea_implementation ii " +
            "ON ii.idea_id = idea.id " +
            "AND ii.implementation_id = ?";

    public List<Idea> getIdeas(final Implementation implementation) {
        List<Idea> ideas = new ArrayList<>();
        try (PreparedStatement ps = dataSource.getConnection().prepareStatement(GET_IDEAS_BY_IMPLEMENTATION)) {
            ps.setLong(1, implementation.getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    final Idea idea = ideaMapper(rs);
                    ideas.add(idea);
                }
            }
        } catch (final Exception e) {
            ideas = null;
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return ideas;
    }

    private final String GET_IDEAS_BY_INNOVATOR = 
        GET_ALL_IDEAS + " " +
        "WHERE idea.innovator_id = ?";

    public List<Idea> getIdeas(final Innovator innovator) {
        List<Idea> ideas = new ArrayList<>();
        try (PreparedStatement ps = dataSource.getConnection().prepareStatement(GET_IDEAS_BY_INNOVATOR)) {
            ps.setLong(1, innovator.getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    final Idea idea = ideaMapper(rs);
                    ideas.add(idea);
                }
            }
        } catch (final Exception e) {
            ideas = null;
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return ideas;
    }

    private final String GET_IDEA_BY_SUMMARY = 
        GET_ALL_IDEAS + " " + 
        "WHERE idea.summary = ?";

    public Idea getIdea(final String summary) {
        Idea idea = null;
        try (PreparedStatement ps = dataSource.getConnection().prepareStatement(GET_IDEA_BY_SUMMARY)) {
            ps.setString(1, summary);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    idea = ideaMapper(rs);
                }
            }
        } catch (final Exception e) {
            idea = null;
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return idea;
    }

    private final String GET_IDEA_BY_ID = 
        GET_ALL_IDEAS + " " + 
        "WHERE idea.id = ?";

    public Idea getIdea(final long id) {
        Idea idea = null;
        try (PreparedStatement ps = dataSource.getConnection().prepareStatement(GET_IDEA_BY_ID)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    idea = ideaMapper(rs);
                }
            }
        } catch (final Exception e) {
            idea = null;
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return idea;
    }

    private final String INSERT_IDEA = 
        "INSERT " +
        "INTO idea (summary, description, innovator_id) " +
        "VALUES (?, ?, ?)";
    private final String UPDATE_IDEA = 
        "UPDATE idea " +
        "SET summary = ?, description = ?, innovator_id = ? " +
        "WHERE id = ?";

    public Idea postIdea(final Idea idea) {
        disassociateCategoriesWithIdea(idea);

        String sql = null;
        if (this.getIdea(idea.getId()) == null) {
            sql = INSERT_IDEA;
        } else {
            sql = UPDATE_IDEA;
        }
        try (PreparedStatement ps = dataSource.getConnection().prepareStatement(sql)) {
            int i = 1;
            ps.setString(i++, idea.getSummary());
            ps.setString(i++, idea.getDescription());
            ps.setLong(i++, idea.getInnovator().getId());
            if (sql.equals(UPDATE_IDEA)) {
                ps.setLong(i++, idea.getId());
            }
            ps.executeUpdate();
        } catch (final Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

        idea.setId(this.getIdea(idea.getSummary()).getId());
        return associateCategoriesWithIdea(idea);
    }

    private final String DISASSOCIATE_IDEA_WITH_CATEGORY =
        "DELETE " +
        "FROM idea_category ic " +
        "WHERE ic.idea_id = ?";

    private boolean disassociateCategoriesWithIdea(final Idea idea) {
        boolean deleted = false;
        try (PreparedStatement ps = dataSource.getConnection().prepareStatement(DISASSOCIATE_IDEA_WITH_CATEGORY)) {
            ps.setLong(1, idea.getId());
            deleted = (ps.executeUpdate() != 0);
        } catch (final Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return deleted;
    }

    private final String ASSOCIATE_IDEA_WITH_CATEGORY =
        "INSERT " +
        "INTO idea_category (idea_id, category_id) " +
        "VALUES (?, ?)";

    private Idea associateCategoriesWithIdea(final Idea idea) {
        try (PreparedStatement ps = dataSource.getConnection().prepareStatement(ASSOCIATE_IDEA_WITH_CATEGORY)) {
            for (Category category : idea.getCategories()) {
                int i = 1;
                ps.setLong(i++, idea.getId());
                ps.setLong(i++, category.getId());
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (final Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return idea;
    }

    private final String DELETE_IDEA = 
        "DELETE " +
        "FROM idea " +
        "WHERE idea.id = ?";

    public boolean deleteIdea(final long id) {
        boolean deleted = false;
        try (PreparedStatement ps = dataSource.getConnection().prepareStatement(DELETE_IDEA)) {
            ps.setLong(1, id);
            deleted = (ps.executeUpdate() != 0);
        } catch (final Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return deleted;
    }

    private Idea ideaMapper(ResultSet rs) throws Exception {
        Idea idea = new Idea();
        idea.setId(rs.getLong("id"));
        idea.setSummary(rs.getString("summary"));
        idea.setDescription(rs.getString("description"));

        final Innovator innovator = new Innovator();
        innovator.setEmailAddress(rs.getString("email_address"));
        idea.setInnovator(innovator);

        return idea;
    }
}