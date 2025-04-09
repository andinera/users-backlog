package idea_service.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import org.springframework.stereotype.Repository;

import idea_service.models.Category;
import idea_service.models.Idea;
import idea_service.models.Implementation;
import idea_service.models.Innovator;
import idea_service.models.Recommendation;

@Repository
public class ImplementationDAO extends DAO {

    private Calendar UTC_CALENDAR = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

    private final String GET_ALL_IMPLEMENTATIONS = 
        "SELECT " +
            "impl.id, " +
            "impl.name, " +
            "impl.description, " +
            "inv.id AS innovator_id, " +
            "inv.email_address, " +
            "iv.votes " +
        "FROM implementation impl " +
        "INNER JOIN innovator inv " +
            "ON inv.id = impl.innovator_id " +
        "LEFT OUTER JOIN " +
            "(" +
                "SELECT " +
                    "implementation_id, " +
                    "innovator_id, " +
                    "SUM(vote) votes " +
                "FROM implementation_vote " +
                "GROUP BY " +
                    "implementation_id, " +
                    "innovator_id" +
            ") iv " +
            "ON (impl.id = iv.implementation_id " +
                "AND inv.id = iv.innovator_id)";

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
        try (PreparedStatement ps = dataSource.getConnection().prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    final Implementation implementation = implementationMapper(rs);
                    implementations.add(implementation);
                }
            }
        } catch (final Exception e) {
            implementations = null;
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return implementations;
    }

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
                    implementation.setRecommendations(this.getRecommendations(implementation));
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
                    implementation.setRecommendations(this.getRecommendations(implementation));
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

    private Implementation implementationMapper(ResultSet rs) throws Exception {
        Implementation implementation = new Implementation();
        implementation.setId(rs.getLong("id"));
        implementation.setName(rs.getString("name"));
        implementation.setDescription(rs.getString("description"));

        final Innovator innovator = new Innovator();
        innovator.setId(rs.getLong("innovator_id"));
        innovator.setEmailAddress(rs.getString("email_address"));
        implementation.setInnovator(innovator);
        
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

        try (PreparedStatement ps = dataSource.getConnection().prepareStatement(sql)) {
            int i = 1;
            ps.setLong(i++, implementation.getInnovator().getId());
            ps.setString(i++, implementation.getName());
            ps.setString(i++, implementation.getDescription());
            if (sql.equals(UPDATE_IMPLEMENTATION)) {
                ps.setLong(i++, implementation.getId());
            }
            ps.executeUpdate();
        } catch (final Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
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
        try (PreparedStatement ps = dataSource.getConnection().prepareStatement(DISASSOCIATE_IMPLEMENTATION_WITH_CATEGORY)) {
            ps.setLong(1, implementation.getId());
            deleted = (ps.executeUpdate() != 0);
        } catch (final Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return deleted;
    }

    private final String ASSOCIATE_IMPLEMENTATION_WITH_CATEGORY =
        "INSERT " +
        "INTO implementation_category (implementation_id, category_id) " +
        "VALUES (?, ?)";

    private Implementation associateCategoriesWithImplementation(final Implementation implementation) {
        try (PreparedStatement ps = dataSource.getConnection().prepareStatement(ASSOCIATE_IMPLEMENTATION_WITH_CATEGORY)) {
            for (Category category : implementation.getCategories()) {
                int i = 1;
                ps.setLong(i++, implementation.getId());
                ps.setLong(i++, category.getId());
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (final Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return implementation;
    }

    private final String ASSOCIATE_IMPLEMENTATION_WITH_IDEA =
        "INSERT " +
        "INTO idea_implementation (idea_id, implementation_id) " +
        "VALUES (?, ?)";

    private Implementation associateImplementationWithIdea(final Implementation implementation) {
        List<Idea> updatedIdeas = null;

        if (implementation.getIdeas() != null) {
            try (PreparedStatement ps = dataSource.getConnection().prepareStatement(ASSOCIATE_IMPLEMENTATION_WITH_IDEA)) {
                for (Idea idea : implementation.getIdeas()) {
                    int i = 1;
                    ps.setLong(i++, idea.getId());
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
        }

        implementation.setIdeas(updatedIdeas);
        return implementation;
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

    public Long postVote(final Long implementationId, final Long innovatorId, final Boolean up) {
        String sql = null;
        if (this.getVote(implementationId, innovatorId) == null) {
            sql = INSERT_VOTE;
        } else {
            sql = UPDATE_VOTE;
        }

        try (PreparedStatement ps = dataSource.getConnection().prepareStatement(sql)) {
            int i = 1;
            ps.setLong(i++, up ? 1 : -1);
            ps.setLong(i++, implementationId);
            ps.setLong(i++, innovatorId);
            ps.executeUpdate();
        } catch (final Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

        return this.getVotes(implementationId, innovatorId);
    }

    private final String GET_VOTES = 
        "SELECT " +
            "SUM(vote) votes " +
        "FROM implementation_vote " +
        "WHERE (implementation_id = ? " +
            "AND innovator_id = ?) " +
        "GROUP BY " +
            "implementation_id, " +
            "innovator_id";

    public Long getVotes(final Long implementationId, final Long innovatorId) {
        Long votes = 0L;
        try (PreparedStatement ps = dataSource.getConnection().prepareStatement(GET_VOTES)) {
            int i = 1;
            ps.setLong(i++, implementationId);
            ps.setLong(i++, innovatorId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    votes = rs.getLong("votes");
                }
            }
        } catch (final Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return votes;
    }

    private final String GET_VOTE = 
        "SELECT " +
            "vote " +
        "FROM implementation_vote iv " +
        "WHERE (implementation_id = ? " +
            "AND innovator_id = ?)";

    public Boolean getVote(final Long implementationId, final Long innovatorId) {
        Boolean vote = null;
        try (PreparedStatement ps = dataSource.getConnection().prepareStatement(GET_VOTE)) {
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
            System.out.println(e.getMessage());
            e.printStackTrace();
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

    public Recommendation postRecommendation(final Recommendation recommendation) {
        String sql = null;
        ZonedDateTime dateTime = ZonedDateTime.now();
        recommendation.setDateTimeModified(dateTime);
        if (recommendation.getId() == 0) {
            sql = INSERT_RECOMMENDATION;
            recommendation.setDateTimeCreated(dateTime);
        } else {
            sql = UPDATE_RECOMMENDATION;
        }

        try (PreparedStatement ps = dataSource.getConnection().prepareStatement(sql)) {
            int i = 1;
            ps.setString(i++, recommendation.getMessage());
            ps.setTimestamp(i++, Timestamp.from(dateTime.toInstant()), UTC_CALENDAR);
            ps.setLong(i++, recommendation.getImplementation().getId());
            ps.setLong(i++, recommendation.getInnovator().getId());
            if (sql.equals(INSERT_RECOMMENDATION)) {
                ps.setTimestamp(i++, Timestamp.from(dateTime.toInstant()), UTC_CALENDAR);
            } else {
                ps.setLong(i++, recommendation.getId());
            }
            ps.executeUpdate();
        } catch (final Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

        return this.getRecommendation(recommendation.getImplementation().getId(), recommendation.getInnovator().getId(), recommendation.getDateTimeCreated());
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

    public List<Recommendation> getRecommendations(final Implementation implementation) {
        List<Recommendation> recommendations = new ArrayList<>();
        try (PreparedStatement ps = dataSource.getConnection().prepareStatement(GET_RECOMMENDATIONS_BY_IMPLEMENTATION)) {
            ps.setLong(1, implementation.getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    recommendations.add(recommendationMapper(rs));
                }
            }
        } catch (final Exception e) {
            recommendations = null;
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return recommendations;
    }

    private final String GET_RECOMMENDATION = 
        GET_RECOMMENDATIONS + " " +
        "WHERE (ir.implementation_id = ? " +
            "AND ir.innovator_id = ? " +
            "AND ir.date_time_created = ?)";

    public Recommendation getRecommendation(final Long implementationId, final Long innovatorId, final ZonedDateTime dateTimeCreated) {
        Recommendation recommendation = null;
        try (PreparedStatement ps = dataSource.getConnection().prepareStatement(GET_RECOMMENDATION)) {
            int i = 1;
            ps.setLong(i++, implementationId);
            ps.setLong(i++, innovatorId);
            ps.setTimestamp(i++, Timestamp.from(dateTimeCreated.toInstant()), UTC_CALENDAR);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    recommendation = recommendationMapper(rs);
                }
            }
        } catch (final Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return recommendation;
    }

    private Recommendation recommendationMapper(ResultSet rs) throws Exception {
        Recommendation recommendation = new Recommendation();
        recommendation.setId(rs.getLong("id"));
        recommendation.setMessage(rs.getString("message"));
        recommendation.setDateTimeCreated(ZonedDateTime.from(rs.getTimestamp("date_time_created", UTC_CALENDAR).toInstant().atZone(ZoneId.of("UTC"))));
        recommendation.setDateTimeModified(ZonedDateTime.from(rs.getTimestamp("date_time_modified", UTC_CALENDAR).toInstant().atZone(ZoneId.of("UTC"))));

        Innovator innovator = new Innovator();
        innovator.setDisplayName(rs.getString("innovator_id"));
        innovator.setDisplayName(rs.getString("display_name"));
        recommendation.setInnovator(innovator);

        return recommendation;
    }

}