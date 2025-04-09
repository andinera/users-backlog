package users_backlog.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import users_backlog.models.Category;
import users_backlog.models.Idea;
import users_backlog.models.Implementation;
import users_backlog.models.Innovator;
import users_backlog.models.Recommendation;
import users_backlog.models.Reply;

@Repository
public class ImplementationDAO extends DAO {
    
    private static final Logger log = Logger.getLogger(ImplementationDAO.class.getName());

    private final String GET_ALL_IMPLEMENTATIONS = 
        "SELECT " +
            "impl.id, " +
            "impl.name, " +
            "impl.description, " +
            "inv.id AS innovator_id, " +
            "inv.email_address, " +
            "inv.display_name, " +
            "COALESCE(iv.votes, 0) votes " +
        "FROM implementation impl " +
        "LEFT OUTER JOIN innovator inv " +
            "ON inv.id = impl.innovator_id " +
        "LEFT OUTER JOIN " +
            "(" +
                "SELECT " +
                    "implementation_id, " +
                    "SUM(vote) votes " +
                "FROM implementation_vote " +
                "GROUP BY " +
                    "implementation_id" +
            ") iv " +
            "ON impl.id = iv.implementation_id";

    public List<Implementation> getImplementations(final String categoryName) {
        String sql = GET_ALL_IMPLEMENTATIONS;
        if (categoryName != null) {
            sql += " " +
            "INNER JOIN implementation_category ic " +
                "INNER JOIN category cat " +
                    "ON (cat.id = ic.category_id " +
                        "AND cat.name = '" + categoryName + "') " +
                "ON ic.implementation_id = impl.id";
        }
        List<Implementation> implementations = new ArrayList<>();
        try (Connection connection = dataSource.getConnection(); PreparedStatement ps = connection.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    final Implementation implementation = implementationMapper(rs);
                    implementations.add(implementation);
                }
            }
        } catch (final Exception e) {
            implementations = null;
            log.severe(e.getMessage());
        }
        return implementations;
    }

    private final String GET_IMPLEMENTATION_BY_NAME = 
        GET_ALL_IMPLEMENTATIONS + " " +
        "WHERE impl.name = ?";

    public Implementation getImplementation(final String name) {
        Implementation implementation = null;
        try (Connection connection = dataSource.getConnection(); PreparedStatement ps = connection.prepareStatement(GET_IMPLEMENTATION_BY_NAME)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    implementation = implementationMapper(rs);
                    implementation.setRecommendations(this.getRecommendations(implementation));
                }
            }
        } catch (final Exception e) {
            implementation = null;
            log.severe(e.getMessage());
        }
        return implementation;
    }

    private final String GET_IMPLEMENTATION_BY_ID = 
        GET_ALL_IMPLEMENTATIONS + " " +
        "WHERE impl.id = ?";

    public Implementation getImplementation(final long id) {
        Implementation implementation = null;
        try (Connection connection = dataSource.getConnection(); PreparedStatement ps = connection.prepareStatement(GET_IMPLEMENTATION_BY_ID)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    implementation = implementationMapper(rs);
                    implementation.setRecommendations(this.getRecommendations(implementation));
                }
            }
        } catch (final Exception e) {
            implementation = null;
            log.severe(e.getMessage());
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
        try (Connection connection = dataSource.getConnection(); PreparedStatement ps = connection.prepareStatement(GET_IMPLEMENTATIONS_BY_IDEA)) {
            ps.setLong(1, idea.getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    implementations.add(implementationMapper(rs));
                }
            }
        } catch (final Exception e) {
            implementations = null;
            log.severe(e.getMessage());
        }
        return implementations;
    }

    private final String GET_IMPLEMENTATIONS_BY_INNOVATOR = 
        GET_ALL_IMPLEMENTATIONS + " " +
        "WHERE impl.innovator_id = ?";

    public List<Implementation> getImplementations(final Innovator innovator) {
        List<Implementation> implementations = new ArrayList<>();
        try (Connection connection = dataSource.getConnection(); PreparedStatement ps = connection.prepareStatement(GET_IMPLEMENTATIONS_BY_INNOVATOR)) {
            ps.setLong(1, innovator.getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    implementations.add(implementationMapper(rs));
                }
            }
        } catch (final Exception e) {
            implementations = null;
            log.severe(e.getMessage());
        }
        return implementations;
    }

    private final String GET_IMPLEMENTATIONS_BY_ID = 
        GET_ALL_IMPLEMENTATIONS + " " +
        "WHERE impl.id IN (?)";

    public List<Implementation> getImplementations(final List<Long> ids) {
        List<Implementation> implementations = new ArrayList<>();
        try (Connection connection = dataSource.getConnection(); PreparedStatement ps = connection.prepareStatement(GET_IMPLEMENTATIONS_BY_ID)) {
            ps.setString(1, ids.stream().map(String::valueOf).collect(Collectors.joining(",")));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    implementations.add(implementationMapper(rs));
                }
            }
        } catch (final Exception e) {
            implementations = null;
            log.severe(e.getMessage());
        }
        return implementations;
    }

    private Implementation implementationMapper(ResultSet rs) throws Exception {
        Implementation implementation = new Implementation();
        implementation.setId(rs.getLong("id"));
        implementation.setName(rs.getString("name"));
        implementation.setDescription(rs.getString("description"));

        if (rs.getLong("innovator_id") != 0) {
            final Innovator innovator = new Innovator();
            innovator.setId(rs.getLong("innovator_id"));
            innovator.setEmailAddress(rs.getString("email_address"));
            innovator.setDisplayName(rs.getString("display_name"));
            implementation.setInnovator(innovator);
        }
        
        implementation.setVotes(rs.getLong("votes"));

        return implementation;
    }

    private final String INSERT_IMPLEMENTATION = 
        "INSERT " +
        "INTO implementation (innovator_id, name, description) " +
        "VALUES (?, ?, ?)";
    private final String UPDATE_IMPLEMENTATION = 
        "UPDATE implementation " +
        "SET innovator_id = ?, name = ?, description = ? " +
        "WHERE id = ?";

    public Implementation postImplementation(final Implementation implementation) {
        disassociateCategoriesWithImplementation(implementation);

        String sql = null;
        if (this.getImplementation(implementation.getId()) == null) {
            sql = INSERT_IMPLEMENTATION;
        } else {
            sql = UPDATE_IMPLEMENTATION;
        }

        try (Connection connection = dataSource.getConnection(); PreparedStatement ps = connection.prepareStatement(sql)) {
            int i = 1;
            if (implementation.getInnovator() != null) {
                ps.setLong(i++, implementation.getInnovator().getId());
            } else {
                ps.setNull(i++, Types.INTEGER);
            }
            ps.setString(i++, implementation.getName());
            ps.setString(i++, implementation.getDescription());
            if (sql.equals(UPDATE_IMPLEMENTATION)) {
                ps.setLong(i++, implementation.getId());
            }
            ps.executeUpdate();
        } catch (final Exception e) {
            log.severe(e.getMessage());
        }

        implementation.setId(this.getImplementation(implementation.getName()).getId());
        associateCategoriesWithImplementation(implementation);
        return implementation;
    }

    private final String DISASSOCIATE_IMPLEMENTATION_WITH_CATEGORY =
        "DELETE " +
        "FROM implementation_category ic " +
        "WHERE ic.implementation_id = ?";

    private boolean disassociateCategoriesWithImplementation(final Implementation implementation) {
        boolean deleted = false;
        try (Connection connection = dataSource.getConnection(); PreparedStatement ps = connection.prepareStatement(DISASSOCIATE_IMPLEMENTATION_WITH_CATEGORY)) {
            ps.setLong(1, implementation.getId());
            deleted = (ps.executeUpdate() != 0);
        } catch (final Exception e) {
            log.severe(e.getMessage());
        }
        return deleted;
    }

    private final String ASSOCIATE_IMPLEMENTATION_WITH_CATEGORY =
        "INSERT " +
        "INTO implementation_category (implementation_id, category_id) " +
        "VALUES (?, ?)";

    private Implementation associateCategoriesWithImplementation(final Implementation implementation) {
        try (Connection connection = dataSource.getConnection(); PreparedStatement ps = connection.prepareStatement(ASSOCIATE_IMPLEMENTATION_WITH_CATEGORY)) {
            for (Category category : implementation.getCategories()) {
                int i = 1;
                ps.setLong(i++, implementation.getId());
                ps.setLong(i++, category.getId());
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (final Exception e) {
            log.severe(e.getMessage());
        }
        return implementation;
    }

    private final String ASSOCIATE_IMPLEMENTATION_WITH_IDEA =
        "INSERT " +
        "INTO idea_implementation (implementation_id, idea_id) " +
        "VALUES (?, ?)";

    public Boolean associateWithIdea(final Implementation implementation, final Long ideaId) {
        Boolean associated = false;

        try (Connection connection = dataSource.getConnection(); PreparedStatement ps = connection.prepareStatement(ASSOCIATE_IMPLEMENTATION_WITH_IDEA)) {
            int i = 1;
            ps.setLong(i++, implementation.getId());
            ps.setLong(i++, ideaId);
            associated = ps.executeUpdate() > 0;
        } catch (final Exception e) {
            log.severe(e.getMessage());
        }

        return associated;
    }

    private final String DISASSOCIATE_IMPLEMENTATION_WITH_IDEA =
        "DELETE " +
        "FROM idea_implementation ii " +
        "WHERE (ii.implementation_id = ? " +
            "AND ii.idea_id = ?)";

    public Boolean disassociateWithIdea(final Implementation implementation, final Long ideaId) {
        Boolean disassociated = false;

        try (Connection connection = dataSource.getConnection(); PreparedStatement ps = connection.prepareStatement(DISASSOCIATE_IMPLEMENTATION_WITH_IDEA)) {
            int i = 1;
            ps.setLong(i++, implementation.getId());
            ps.setLong(i++, ideaId);
            disassociated = ps.executeUpdate() > 0;
        } catch (final Exception e) {
            log.severe(e.getMessage());
        }

        return disassociated;
    }

    private final String DELETE_IMPLEMENTATION = 
        "DELETE " +
        "FROM implementation impl " +
        "WHERE impl.id = ?";

    public boolean deleteImplementation(final Implementation implementation) {

        try (Connection connection = dataSource.getConnection(); PreparedStatement ps = connection.prepareStatement(DELETE_IMPLEMENTATION)) {
            ps.setLong(1, implementation.getId());
            return ps.executeUpdate() > 0;
        } catch (final Exception e) {
            log.severe(e.getMessage());
        }

        return false;
    }

    private final String INSERT_VOTE =
        "INSERT " +
        "INTO implementation_vote (vote, implementation_id, innovator_id) " +
        "VALUES (?, ?, ?)";
    private final String UPDATE_VOTE = 
        "UPDATE implementation_vote " +
        "SET vote = ? " +
        "WHERE implementation_id = ? " +
            "AND innovator_id = ?";

    public Long postVote(final Implementation implementation, final Innovator innovator, final Boolean up) {
        String sql = null;
        if (this.getVote(implementation, innovator) == null) {
            sql = INSERT_VOTE;
        } else {
            sql = UPDATE_VOTE;
        }

        try (Connection connection = dataSource.getConnection(); PreparedStatement ps = connection.prepareStatement(sql)) {
            int i = 1;
            ps.setLong(i++, up ? 1 : -1);
            ps.setLong(i++, implementation.getId());
            ps.setLong(i++, innovator.getId());
            ps.executeUpdate();
        } catch (final Exception e) {
            log.severe(e.getMessage());
        }

        return this.getVotes(implementation);
    }

    private final String GET_VOTES = 
        "SELECT " +
            "SUM(vote) votes " +
        "FROM implementation_vote " +
        "WHERE implementation_id = ? " +
        "GROUP BY " +
            "implementation_id";

    private Long getVotes(final Implementation implementation) {
        Long votes = 0L;
        try (Connection connection = dataSource.getConnection(); PreparedStatement ps = connection.prepareStatement(GET_VOTES)) {
            int i = 1;
            ps.setLong(i++, implementation.getId());
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
        "FROM implementation_vote iv " +
        "WHERE (implementation_id = ? " +
            "AND innovator_id = ?)";

    public Boolean getVote(final Implementation implementation, final Innovator innovator) {
        return getVote(implementation.getId(), innovator.getId());
    }

    public Boolean getVote(final Long implementationId, final Long innovatorId) {
        Boolean vote = null;
        try (Connection connection = dataSource.getConnection(); PreparedStatement ps = connection.prepareStatement(GET_VOTE)) {
            int i = 1;
            ps.setLong(i++, implementationId);
            ps.setLong(i++, innovatorId);
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
        "INTO implementation_recommendation (" +
            "message, " +
            "date_time_created, " +
            "implementation_id, " +
            "innovator_id, " +
            "date_time_modified " +
        ") VALUES (?, ?, ?, ?, ?)";
    private final String UPDATE_RECOMMENDATION = 
        "UPDATE implementation_recommendation " +
        "SET message = ?, date_time_modified = ? " +
        "WHERE (implementation_id = ? " +
            "AND innovator_id = ? " +
            "AND id = ?)";

    public Recommendation<Implementation> postRecommendation(final Recommendation<Implementation> recommendation) {
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
        "FROM implementation_recommendation ir " +
        "WHERE ir.id = ?";

    public boolean deleteRecommendation(final Recommendation<Implementation> recommendation) {

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
        "FROM implementation_recommendation ir " +
        "INNER JOIN innovator inno " +
            "ON inno.id = ir.innovator_id";

    private final String GET_RECOMMENDATIONS_BY_IMPLEMENTATION = 
        GET_RECOMMENDATIONS + " " +
        "WHERE ir.implementation_id = ?";

    public List<Recommendation<Implementation>> getRecommendations(final Implementation implementation) {
        List<Recommendation<Implementation>> recommendations = new ArrayList<>();
        try (Connection connection = dataSource.getConnection(); PreparedStatement ps = connection.prepareStatement(GET_RECOMMENDATIONS_BY_IMPLEMENTATION)) {
            ps.setLong(1, implementation.getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Recommendation<Implementation> recommendation = recommendationMapper(rs);
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
        "WHERE (ir.implementation_id = ? " +
            "AND ir.innovator_id = ? " +
            "AND ir.date_time_created = ?)";

    public Recommendation<Implementation> getRecommendation(final Long implementationId, final Long innovatorId, final ZonedDateTime dateTimeCreated) {
        Recommendation<Implementation> recommendation = null;
        try (Connection connection = dataSource.getConnection(); PreparedStatement ps = connection.prepareStatement(GET_RECOMMENDATION)) {
            int i = 1;
            ps.setLong(i++, implementationId);
            ps.setLong(i++, innovatorId);
            ps.setTimestamp(i++, Timestamp.from(dateTimeCreated.toInstant()), UTC_CALENDAR);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    recommendation = recommendationMapper(rs);
                    recommendation.setVotes(this.getRecommendationVotes(recommendation.getId()));
                    recommendation.setReplies(this.getRecommendationReplies(recommendation));
                }
            }
        } catch (final Exception e) {
            log.severe(e.getMessage());
        }
        return recommendation;
    }

    private Recommendation<Implementation> recommendationMapper(ResultSet rs) throws Exception {
        Recommendation<Implementation> recommendation = new Recommendation<>();
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
        "INTO implementation_recommendation_vote (vote, implementation_recommendation_id, innovator_id) " +
        "VALUES (?, ?, ?)";
    private final String UPDATE_RECOMMENDATION_VOTE = 
        "UPDATE implementation_recommendation_vote " +
        "SET vote = ? " +
        "WHERE implementation_recommendation_id = ? " +
            "AND innovator_id = ?";

    public Long postRecommendationVote(final Recommendation<Implementation> recommendation, final Innovator innovator, final Boolean up) {
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
        "FROM implementation_recommendation_vote " +
        "WHERE implementation_recommendation_id = ? " +
        "GROUP BY " +
            "implementation_recommendation_id";

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
        "FROM implementation_recommendation_vote iv " +
        "WHERE (implementation_recommendation_id = ? " +
            "AND innovator_id = ?)";

    public Boolean getRecommendationVote(final Recommendation<Implementation> recommendation, final Innovator innovator) {
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

    private final String GET_RECOMMENDATIONS_VOTES = 
        "SELECT " +
            "ir.id, " +
            "iv.vote " +
        "FROM implementation_recommendation ir " +
        "INNER JOIN implementation_recommendation_vote iv " +
            "ON iv.implementation_recommendation_id = ir.id " +
        "WHERE (ir.implementation_id = ? " +
            "AND ir.innovator_id = ?)";

    public Map<Long, Boolean> getRecommendationsVotes(final Long implementationId, final Long innovatorId) {
        Map<Long, Boolean> votes = new HashMap<>();
        try (Connection connection = dataSource.getConnection(); PreparedStatement ps = connection.prepareStatement(GET_RECOMMENDATIONS_VOTES)) {
            int i = 1;
            ps.setLong(i++, implementationId);
            ps.setLong(i++, innovatorId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    votes.put(rs.getLong("id"), rs.getLong("vote") == 1);
                }
            }
        } catch (final Exception e) {
            votes = null;
            log.severe(e.getMessage());
        }
        return votes;
    }

    private final String INSERT_RECOMMENDATION_REPLY =
        "INSERT " +
        "INTO implementation_recommendation_reply (" +
            "message, " +
            "date_time_created, " +
            "implementation_recommendation_id, " +
            "innovator_id, " +
            "date_time_modified " +
        ") VALUES (?, ?, ?, ?, ?)";
    private final String UPDATE_RECOMMENDATION_REPLY = 
        "UPDATE implementation_recommendation_reply " +
        "SET message = ?, date_time_modified = ? " +
        "WHERE (implementation_recommendation_id = ? " +
            "AND innovator_id = ? " +
            "AND id = ?)";

    public Reply<Implementation> postRecommendationReply(final Reply<Implementation> reply) {
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
        "FROM implementation_recommendation_reply irr " +
        "INNER JOIN innovator inno " +
            "ON inno.id = irr.innovator_id";

    private final String GET_RECOMMENDATION_REPLIES_BY_RECOMMENDATION = 
        GET_RECOMMENDATION_REPLIES + " " +
        "WHERE irr.implementation_recommendation_id = ?";

    public List<Reply<Implementation>> getRecommendationReplies(final Recommendation<Implementation> recommendation) {
        List<Reply<Implementation>> replies = new ArrayList<>();
        try (Connection connection = dataSource.getConnection(); PreparedStatement ps = connection.prepareStatement(GET_RECOMMENDATION_REPLIES_BY_RECOMMENDATION)) {
            ps.setLong(1, recommendation.getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Reply<Implementation> reply = replyMapper(rs);
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
        "WHERE (irr.implementation_recommendation_id = ? " +
            "AND irr.innovator_id = ? " +
            "AND irr.date_time_created = ?)";

    public Reply<Implementation> getRecommendationReply(final Long recommendationId, final Long innovatorId, final ZonedDateTime dateTimeCreated) {
        Reply<Implementation> reply = null;
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

    private Reply<Implementation> replyMapper(ResultSet rs) throws Exception {
        Reply<Implementation> reply = new Reply<>();
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
        "FROM implementation_recommendation_reply irr " +
        "WHERE irr.id = ?";

    public boolean deleteRecommendationReply(final Reply<Implementation> reply) {

        try (Connection connection = dataSource.getConnection(); PreparedStatement ps = connection.prepareStatement(DELETE_RECOMMENDATION_REPLY)) {
            ps.setLong(1, reply.getId());
            return ps.executeUpdate() > 0;
        } catch (final Exception e) {
            log.severe(e.getMessage());
        }

        return false;
    }

}