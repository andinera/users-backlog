package users_backlog.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.stereotype.Repository;

import users_backlog.models.Category;
import users_backlog.models.Idea;
import users_backlog.models.Implementation;
import users_backlog.models.Innovator;
import users_backlog.models.Recommendation;
import users_backlog.models.Reply;

@Repository
public class IdeaDAO extends DAO {

    private static final Logger log = Logger.getLogger(IdeaDAO.class.getName());

    private final String GET_ALL_IDEAS = 
        "SELECT " +
            "idea.id, " +
            "idea.summary, " +
            "idea.description, " +
            "inv.id AS innovator_id, " +
            "inv.email_address, " +
            "inv.display_name, " +
            "COALESCE(iv.votes, 0) votes " +
        "FROM idea " +
        "LEFT OUTER JOIN innovator inv " +
            "ON inv.id = idea.innovator_id " +
        "LEFT OUTER JOIN " +
            "(" +
                "SELECT " +
                    "idea_id, " +
                    "SUM(vote) votes " +
                "FROM idea_vote " +
                "GROUP BY " +
                    "idea_id" +
            ") iv " +
            "ON idea.id = iv.idea_id";

    public List<Idea> getIdeas(final String categoryName) {
        List<Idea> ideas = new ArrayList<>();

        String sql = GET_ALL_IDEAS;
        if(categoryName != null) {
            sql += " " +
            "INNER JOIN idea_category ic " +
                "INNER JOIN category cat " +
                    "ON (cat.id = ic.category_id " +
                        "AND cat.name = ?) " +
                "ON ic.idea_id = idea.id";
        }

        try (Connection connection = dataSource.getConnection(); PreparedStatement ps = connection.prepareStatement(sql)) {
            if (categoryName != null) {
                ps.setString(1, categoryName);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    final Idea idea = ideaMapper(rs);
                    ideas.add(idea);
                }
            }
        } catch (final Exception e) {
            ideas = null;
            log.severe(e.getMessage());
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
        try (Connection connection = dataSource.getConnection(); PreparedStatement ps = connection.prepareStatement(GET_IDEAS_BY_IMPLEMENTATION)) {
            ps.setLong(1, implementation.getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    final Idea idea = ideaMapper(rs);
                    ideas.add(idea);
                }
            }
        } catch (final Exception e) {
            ideas = null;
            log.severe(e.getMessage());
        }
        return ideas;
    }

    private final String GET_IDEAS_BY_INNOVATOR = 
        GET_ALL_IDEAS + " " +
        "WHERE idea.innovator_id = ?";

    public List<Idea> getIdeas(final Innovator innovator) {
        List<Idea> ideas = new ArrayList<>();
        try (Connection connection = dataSource.getConnection(); PreparedStatement ps = connection.prepareStatement(GET_IDEAS_BY_INNOVATOR)) {
            ps.setLong(1, innovator.getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    final Idea idea = ideaMapper(rs);
                    ideas.add(idea);
                }
            }
        } catch (final Exception e) {
            ideas = null;
            log.severe(e.getMessage());
        }
        return ideas;
    }

    private final String GET_IDEA_BY_SUMMARY = 
        GET_ALL_IDEAS + " " + 
        "WHERE idea.summary = ?";

    public Idea getIdea(final String summary) {
        Idea idea = null;
        try (Connection connection = dataSource.getConnection(); PreparedStatement ps = connection.prepareStatement(GET_IDEA_BY_SUMMARY)) {
            ps.setString(1, summary);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    idea = ideaMapper(rs);
                    idea.setRecommendations(this.getRecommendations(idea));
                }
            }
        } catch (final Exception e) {
            idea = null;
            log.severe(e.getMessage());
        }
        return idea;
    }

    private final String GET_IDEA_BY_ID = 
        GET_ALL_IDEAS + " " + 
        "WHERE idea.id = ?";

    public Idea getIdea(final long id) {
        Idea idea = null;
        try (Connection connection = dataSource.getConnection(); PreparedStatement ps = connection.prepareStatement(GET_IDEA_BY_ID)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    idea = ideaMapper(rs);
                    idea.setRecommendations(this.getRecommendations(idea));
                }
            }
        } catch (final Exception e) {
            idea = null;
            log.severe(e.getMessage());
        }
        return idea;
    }

    private Idea ideaMapper(ResultSet rs) throws Exception {
        Idea idea = new Idea();
        idea.setId(rs.getLong("id"));
        idea.setSummary(rs.getString("summary"));
        idea.setDescription(rs.getString("description"));

        if (rs.getLong("innovator_id") != 0) {
            final Innovator innovator = new Innovator();
            innovator.setId(rs.getLong("innovator_id"));
            innovator.setEmailAddress(rs.getString("email_address"));
            innovator.setDisplayName(rs.getString("display_name"));
            idea.setInnovator(innovator);
        }
        
        idea.setVotes(rs.getLong("votes"));

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
        try (Connection connection = dataSource.getConnection(); PreparedStatement ps = connection.prepareStatement(sql)) {
            int i = 1;
            ps.setString(i++, idea.getSummary());
            ps.setString(i++, idea.getDescription());
            ps.setLong(i++, idea.getInnovator().getId());
            if (sql.equals(UPDATE_IDEA)) {
                ps.setLong(i++, idea.getId());
            }
            ps.executeUpdate();
        } catch (final Exception e) {
            log.severe(e.getMessage());
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
        try (Connection connection = dataSource.getConnection(); PreparedStatement ps = connection.prepareStatement(DISASSOCIATE_IDEA_WITH_CATEGORY)) {
            ps.setLong(1, idea.getId());
            deleted = (ps.executeUpdate() != 0);
        } catch (final Exception e) {
            log.severe(e.getMessage());
        }
        return deleted;
    }

    private final String ASSOCIATE_IDEA_WITH_CATEGORY =
        "INSERT " +
        "INTO idea_category (idea_id, category_id) " +
        "VALUES (?, ?)";

    private Idea associateCategoriesWithIdea(final Idea idea) {
        try (Connection connection = dataSource.getConnection(); PreparedStatement ps = connection.prepareStatement(ASSOCIATE_IDEA_WITH_CATEGORY)) {
            for (Category category : idea.getCategories()) {
                int i = 1;
                ps.setLong(i++, idea.getId());
                ps.setLong(i++, category.getId());
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (final Exception e) {
            log.severe(e.getMessage());
        }
        return idea;
    }

    private final String DELETE_IDEA = 
        "DELETE " +
        "FROM idea " +
        "WHERE idea.id = ?";

    public boolean deleteIdea(final Idea idea) {
        boolean deleted = false;
        try (Connection connection = dataSource.getConnection(); PreparedStatement ps = connection.prepareStatement(DELETE_IDEA)) {
            ps.setLong(1, idea.getId());
            deleted = (ps.executeUpdate() != 0);
        } catch (final Exception e) {
            log.severe(e.getMessage());
        }
        return deleted;
    }

    private final String INSERT_VOTE =
        "INSERT " +
        "INTO idea_vote (vote, idea_id, innovator_id) " +
        "VALUES (?, ?, ?)";
    private final String UPDATE_VOTE = 
        "UPDATE idea_vote " +
        "SET vote = ? " +
        "WHERE idea_id = ? " +
            "AND innovator_id = ?";

    public Long postVote(final Idea idea, final Innovator innovator, final Boolean up) {
        String sql = null;
        if (this.getVote(idea, innovator) == null) {
            sql = INSERT_VOTE;
        } else {
            sql = UPDATE_VOTE;
        }

        try (Connection connection = dataSource.getConnection(); PreparedStatement ps = connection.prepareStatement(sql)) {
            int i = 1;
            ps.setLong(i++, up ? 1 : -1);
            ps.setLong(i++, idea.getId());
            ps.setLong(i++, innovator.getId());
            ps.executeUpdate();
        } catch (final Exception e) {
            log.severe(e.getMessage());
        }

        return this.getVotes(idea);
    }

    private final String GET_VOTES = 
        "SELECT " +
            "SUM(vote) votes " +
        "FROM idea_vote " +
        "WHERE idea_id = ? " +
        "GROUP BY " +
            "idea_id";

    private Long getVotes(final Idea idea) {
        Long votes = 0L;
        try (Connection connection = dataSource.getConnection(); PreparedStatement ps = connection.prepareStatement(GET_VOTES)) {
            int i = 1;
            ps.setLong(i++, idea.getId());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    votes = rs.getLong("votes");
                }
            }
        } catch (final Exception e) {
            log.severe(e.getMessage());
        }
        return votes;
    }

    private final String GET_VOTE = 
        "SELECT " +
            "vote " +
        "FROM idea_vote iv " +
        "WHERE (idea_id = ? " +
            "AND innovator_id = ?)";

    public Boolean getVote(final Idea idea, final Innovator innovator) {
        Boolean vote = null;
        try (Connection connection = dataSource.getConnection(); PreparedStatement ps = connection.prepareStatement(GET_VOTE)) {
            int i = 1;
            ps.setLong(i++, idea.getId());
            ps.setLong(i++, innovator.getId());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    vote = rs.getLong("vote") == 1;
                }
            }
        } catch (final Exception e) {
            vote = null;
            log.severe(e.getMessage());
        }
        return vote;
    }

    private final String INSERT_RECOMMENDATION =
        "INSERT " +
        "INTO idea_recommendation (" +
            "message, " +
            "date_time_created, " +
            "idea_id, " +
            "innovator_id, " +
            "date_time_modified " +
        ") VALUES (?, ?, ?, ?, ?)";
    private final String UPDATE_RECOMMENDATION = 
        "UPDATE idea_recommendation " +
        "SET message = ?, date_time_modified = ? " +
        "WHERE (idea_id = ? " +
            "AND innovator_id = ? " +
            "AND id = ?)";

    public Recommendation<Idea> postRecommendation(final Recommendation<Idea> recommendation) {
        String sql = null;
        ZonedDateTime dateTime = ZonedDateTime.now();
        recommendation.setDateTimeModified(dateTime);
        if (recommendation.getId() == 0) {
            sql = INSERT_RECOMMENDATION;
            recommendation.setDateTimeCreated(dateTime);
        } else {
            sql = UPDATE_RECOMMENDATION;
        }

        try (Connection connection = dataSource.getConnection(); PreparedStatement ps = connection.prepareStatement(sql)) {
            int i = 1;
            ps.setString(i++, recommendation.getMessage());
            ps.setTimestamp(i++, Timestamp.from(dateTime.toInstant()), UTC_CALENDAR);
            ps.setLong(i++, recommendation.getParent().getId());
            ps.setLong(i++, recommendation.getInnovator().getId());
            if (sql.equals(INSERT_RECOMMENDATION)) {
                ps.setTimestamp(i++, Timestamp.from(dateTime.toInstant()), UTC_CALENDAR);
            } else {
                ps.setLong(i++, recommendation.getId());
            }
            ps.executeUpdate();
        } catch (final Exception e) {
            log.severe(e.getMessage());
        }

        return this.getRecommendation(recommendation.getParent().getId(), recommendation.getInnovator().getId(), recommendation.getDateTimeCreated());
    }

    private final String DELETE_RECOMMENDATION = 
        "DELETE " +
        "FROM idea_recommendation ir " +
        "WHERE ir.id = ?";

    public boolean deleteRecommendation(final Recommendation<Idea> recommendation) {

        try (Connection connection = dataSource.getConnection(); PreparedStatement ps = connection.prepareStatement(DELETE_RECOMMENDATION)) {
            ps.setLong(1, recommendation.getId());
            return ps.executeUpdate() > 0;
        } catch (final Exception e) {
            log.severe(e.getMessage());
        }

        return false;
    }

    private final String GET_RECOMMENDATIONS = 
        "SELECT " +
            "ir.id, " +
            "ir.message, " +
            "ir.date_time_created, " +
            "ir.date_time_modified, " +
            "ir.innovator_id, " +
            "inno.display_name " +
        "FROM idea_recommendation ir " +
        "INNER JOIN innovator inno " +
            "ON inno.id = ir.innovator_id";

    private final String GET_RECOMMENDATIONS_BY_IDEA = 
        GET_RECOMMENDATIONS + " " +
        "WHERE ir.idea_id = ?";

    public List<Recommendation<Idea>> getRecommendations(final Idea idea) {
        List<Recommendation<Idea>> recommendations = new ArrayList<>();
        try (Connection connection = dataSource.getConnection(); PreparedStatement ps = connection.prepareStatement(GET_RECOMMENDATIONS_BY_IDEA)) {
            ps.setLong(1, idea.getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Recommendation<Idea> recommendation = recommendationMapper(rs);
                    recommendation.setVotes(this.getRecommendationVotes(recommendation.getId()));
                    recommendation.setReplies(this.getRecommendationReplies(recommendation));
                    recommendations.add(recommendation);
                }
            }
        } catch (final Exception e) {
            recommendations = null;
            log.severe(e.getMessage());
        }
        return recommendations;
    }

    private final String GET_RECOMMENDATION = 
        GET_RECOMMENDATIONS + " " +
        "WHERE (ir.idea_id = ? " +
            "AND ir.innovator_id = ? " +
            "AND ir.date_time_created = ?)";

    public Recommendation<Idea> getRecommendation(final Long ideaId, final Long innovatorId, final ZonedDateTime dateTimeCreated) {
        Recommendation<Idea> recommendation = null;
        try (Connection connection = dataSource.getConnection(); PreparedStatement ps = connection.prepareStatement(GET_RECOMMENDATION)) {
            int i = 1;
            ps.setLong(i++, ideaId);
            ps.setLong(i++, innovatorId);
            ps.setTimestamp(i++, Timestamp.from(dateTimeCreated.toInstant()), UTC_CALENDAR);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    recommendation = recommendationMapper(rs);
                    recommendation.setVotes(this.getRecommendationVotes(recommendation.getId()));
                }
            }
        } catch (final Exception e) {
            log.severe(e.getMessage());
        }
        return recommendation;
    }

    private Recommendation<Idea> recommendationMapper(ResultSet rs) throws Exception {
        Recommendation<Idea> recommendation = new Recommendation<>();
        recommendation.setId(rs.getLong("id"));
        recommendation.setMessage(rs.getString("message"));
        recommendation.setDateTimeCreated(ZonedDateTime.from(rs.getTimestamp("date_time_created", UTC_CALENDAR).toInstant().atZone(ZoneId.of("UTC"))));
        recommendation.setDateTimeModified(ZonedDateTime.from(rs.getTimestamp("date_time_modified", UTC_CALENDAR).toInstant().atZone(ZoneId.of("UTC"))));

        Innovator innovator = new Innovator();
        innovator.setId(rs.getLong("innovator_id"));
        innovator.setDisplayName(rs.getString("display_name"));
        recommendation.setInnovator(innovator);

        return recommendation;
    }

    private final String INSERT_RECOMMENDATION_VOTE =
        "INSERT " +
        "INTO idea_recommendation_vote (vote, idea_recommendation_id, innovator_id) " +
        "VALUES (?, ?, ?)";
    private final String UPDATE_RECOMMENDATION_VOTE = 
        "UPDATE idea_recommendation_vote " +
        "SET vote = ? " +
        "WHERE idea_recommendation_id = ? " +
            "AND innovator_id = ?";

    public Long postRecommendationVote(final Recommendation<Idea> recommendation, final Innovator innovator, final Boolean up) {
        String sql = null;
        if (this.getRecommendationVote(recommendation, innovator) == null) {
            sql = INSERT_RECOMMENDATION_VOTE;
        } else {
            sql = UPDATE_RECOMMENDATION_VOTE;
        }

        try (Connection connection = dataSource.getConnection(); PreparedStatement ps = connection.prepareStatement(sql)) {
            int i = 1;
            ps.setLong(i++, up ? 1 : -1);
            ps.setLong(i++, recommendation.getId());
            ps.setLong(i++, innovator.getId());
            ps.executeUpdate();
        } catch (final Exception e) {
            log.severe(e.getMessage());
        }

        return this.getRecommendationVotes(recommendation.getId());
    }

    private final String GET_RECOMMENDATION_VOTES = 
        "SELECT " +
            "SUM(vote) votes " +
        "FROM idea_recommendation_vote " +
        "WHERE idea_recommendation_id = ? " +
        "GROUP BY " +
            "idea_recommendation_id";

    private Long getRecommendationVotes(final Long recommendationId) {
        Long votes = 0L;
        try (Connection connection = dataSource.getConnection(); PreparedStatement ps = connection.prepareStatement(GET_RECOMMENDATION_VOTES)) {
            int i = 1;
            ps.setLong(i++, recommendationId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    votes = rs.getLong("votes");
                }
            }
        } catch (final Exception e) {
            log.severe(e.getMessage());
        }
        return votes;
    }

    private final String GET_RECOMMENDATION_VOTE = 
        "SELECT " +
            "vote " +
        "FROM idea_recommendation_vote iv " +
        "WHERE (idea_recommendation_id = ? " +
            "AND innovator_id = ?)";

    public Boolean getRecommendationVote(final Recommendation<Idea> recommendation, final Innovator innovator) {
        Boolean vote = null;
        try (Connection connection = dataSource.getConnection(); PreparedStatement ps = connection.prepareStatement(GET_RECOMMENDATION_VOTE)) {
            int i = 1;
            ps.setLong(i++, recommendation.getId());
            ps.setLong(i++, innovator.getId());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    vote = rs.getLong("vote") == 1;
                }
            }
        } catch (final Exception e) {
            vote = null;
            log.severe(e.getMessage());
        }
        return vote;
    }

    private final String INSERT_RECOMMENDATION_REPLY =
        "INSERT " +
        "INTO idea_recommendation_reply (" +
            "message, " +
            "date_time_created, " +
            "idea_recommendation_id, " +
            "innovator_id, " +
            "date_time_modified " +
        ") VALUES (?, ?, ?, ?, ?)";
    private final String UPDATE_RECOMMENDATION_REPLY = 
        "UPDATE idea_recommendation_reply " +
        "SET message = ?, date_time_modified = ? " +
        "WHERE (idea_recommendation_id = ? " +
            "AND innovator_id = ? " +
            "AND id = ?)";

    public Reply<Idea> postRecommendationReply(final Reply<Idea> reply) {
        String sql = null;
        ZonedDateTime dateTime = ZonedDateTime.now();
        reply.setDateTimeModified(dateTime);
        if (reply.getId() == 0) {
            sql = INSERT_RECOMMENDATION_REPLY;
            reply.setDateTimeCreated(dateTime);
        } else {
            sql = UPDATE_RECOMMENDATION_REPLY;
        }

        try (Connection connection = dataSource.getConnection(); PreparedStatement ps = connection.prepareStatement(sql)) {
            int i = 1;
            ps.setString(i++, reply.getMessage());
            ps.setTimestamp(i++, Timestamp.from(dateTime.toInstant()), UTC_CALENDAR);
            ps.setLong(i++, reply.getRecommendation().getId());
            ps.setLong(i++, reply.getInnovator().getId());
            if (sql.equals(INSERT_RECOMMENDATION_REPLY)) {
                ps.setTimestamp(i++, Timestamp.from(dateTime.toInstant()), UTC_CALENDAR);
            } else {
                ps.setLong(i++, reply.getId());
            }
            ps.executeUpdate();
        } catch (final Exception e) {
            log.severe(e.getMessage());
        }

        return this.getRecommendationReply(reply.getRecommendation().getId(), reply.getInnovator().getId(), reply.getDateTimeCreated());
    }

    private final String GET_RECOMMENDATION_REPLIES = 
        "SELECT " +
            "irr.id, " +
            "irr.message, " +
            "irr.date_time_created, " +
            "irr.date_time_modified, " +
            "irr.innovator_id, " +
            "inno.display_name " +
        "FROM idea_recommendation_reply irr " +
        "INNER JOIN innovator inno " +
            "ON inno.id = irr.innovator_id";

    private final String GET_RECOMMENDATION_REPLIES_BY_RECOMMENDATION = 
        GET_RECOMMENDATION_REPLIES + " " +
        "WHERE irr.idea_recommendation_id = ?";

    public List<Reply<Idea>> getRecommendationReplies(final Recommendation<Idea> recommendation) {
        List<Reply<Idea>> replies = new ArrayList<>();
        try (Connection connection = dataSource.getConnection(); PreparedStatement ps = connection.prepareStatement(GET_RECOMMENDATION_REPLIES_BY_RECOMMENDATION)) {
            ps.setLong(1, recommendation.getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Reply<Idea> reply = replyMapper(rs);
                    replies.add(reply);
                }
            }
        } catch (final Exception e) {
            replies = null;
            log.severe(e.getMessage());
        }
        return replies;
    }

    private final String GET_RECOMMENDATION_REPLY = 
        GET_RECOMMENDATION_REPLIES + " " +
        "WHERE (irr.idea_recommendation_id = ? " +
            "AND irr.innovator_id = ? " +
            "AND irr.date_time_created = ?)";

    public Reply<Idea> getRecommendationReply(final Long recommendationId, final Long innovatorId, final ZonedDateTime dateTimeCreated) {
        Reply<Idea> reply = null;
        try (Connection connection = dataSource.getConnection(); PreparedStatement ps = connection.prepareStatement(GET_RECOMMENDATION_REPLY)) {
            int i = 1;
            ps.setLong(i++, recommendationId);
            ps.setLong(i++, innovatorId);
            ps.setTimestamp(i++, Timestamp.from(dateTimeCreated.toInstant()), UTC_CALENDAR);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    reply = replyMapper(rs);
                }
            }
        } catch (final Exception e) {
            log.severe(e.getMessage());
        }
        return reply;
    }

    private Reply<Idea> replyMapper(ResultSet rs) throws Exception {
        Reply<Idea> reply = new Reply<>();
        reply.setId(rs.getLong("id"));
        reply.setMessage(rs.getString("message"));
        reply.setDateTimeCreated(ZonedDateTime.from(rs.getTimestamp("date_time_created", UTC_CALENDAR).toInstant().atZone(ZoneId.of("UTC"))));
        reply.setDateTimeModified(ZonedDateTime.from(rs.getTimestamp("date_time_modified", UTC_CALENDAR).toInstant().atZone(ZoneId.of("UTC"))));

        Innovator innovator = new Innovator();
        innovator.setId(rs.getLong("innovator_id"));
        innovator.setDisplayName(rs.getString("display_name"));
        reply.setInnovator(innovator);

        return reply;
    }

    private final String DELETE_RECOMMENDATION_REPLY = 
        "DELETE " +
        "FROM idea_recommendation_reply irr " +
        "WHERE irr.id = ?";

    public boolean deleteRecommendationReply(final Reply<Idea> reply) {

        try (Connection connection = dataSource.getConnection(); PreparedStatement ps = connection.prepareStatement(DELETE_RECOMMENDATION_REPLY)) {
            ps.setLong(1, reply.getId());
            return ps.executeUpdate() > 0;
        } catch (final Exception e) {
            log.severe(e.getMessage());
        }

        return false;
    }
}