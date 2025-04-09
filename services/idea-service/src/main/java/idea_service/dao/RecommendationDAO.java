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

import idea_service.models.Recommendation;
import idea_service.models.Idea;
import idea_service.models.Innovator;

@Repository
public class RecommendationDAO extends DAO {

    private final String GET_ALL_RECOMMENDATIONS = 
        "SELECT " +
            "r.id, " +
            "r.message, " +
            "r.date_time_created, " +
            "inv.email_address " +
        "FROM recommendation r " +
        "INNER JOIN innovator inv " +
            "ON inv.id = r.innovator_id";

    private final String GET_RECOMMENDATION_BY_ID = 
        GET_ALL_RECOMMENDATIONS + " " +
        "WHERE r.id = ?";

    public Recommendation getRecommendation(final long id) {
        Recommendation recommendation = null;
        try (PreparedStatement ps = dataSource.getConnection().prepareStatement(GET_RECOMMENDATION_BY_ID)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    recommendation = recommendationMapper(rs);
                }
            }
        } catch (final Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return recommendation;
    }

    private final String GET_RECOMMENDATIONS_BY_IDEA = 
        GET_ALL_RECOMMENDATIONS + " " +
        "WHERE r.idea_id = ?";

    public List<Recommendation> getRecommendations(final Idea idea) {
        List<Recommendation> recommendations = new ArrayList<>();
        try (PreparedStatement ps = dataSource.getConnection().prepareStatement(GET_RECOMMENDATIONS_BY_IDEA)) {
            ps.setLong(1, idea.getId());
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

    private final String GET_RECOMMENDATION_BY_INNOVATOR_AND_DATETIME = 
        GET_ALL_RECOMMENDATIONS + " " +
        "WHERE (r.innovator_id = ? " +
            "AND r.date_time_created = ?)";

    public Recommendation getRecommendation(final Innovator innovator, final ZonedDateTime dateTime) {
        Recommendation recommendation = null;
        try (PreparedStatement ps = dataSource.getConnection().prepareStatement(GET_RECOMMENDATION_BY_INNOVATOR_AND_DATETIME)) {
            int i = 1;
            ps.setLong(i++, innovator.getId());
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            ps.setTimestamp(i++, Timestamp.from(dateTime.toInstant()), calendar);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    recommendation = recommendationMapper(rs);
                }
            }
        } catch (final Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return recommendation;
    }

    private final String INSERT_RECOMMENDATION = 
        "INSERT " +
        "INTO recommendation (idea_id, message, date_time_created, innovator_id) " +
        "VALUES (?, ?, ?, ?)";
    private final String UPDATE_RECOMMENDATION = 
        "UPDATE idea " +
        "SET idea_id = ?, message = ?, date_time_created = ?, innovator_id = ? " +
        "WHERE id = ?";

    public Recommendation postRecommendation(final Recommendation recommendation) {
        String sql = null;
        if (this.getRecommendation(recommendation.getId()) == null) {
            sql = INSERT_RECOMMENDATION;
        } else {
            sql = UPDATE_RECOMMENDATION;
        }
        try (PreparedStatement ps = dataSource.getConnection().prepareStatement(sql)) {
            int i = 1;
            ps.setLong(i++, recommendation.getIdea().getId());
            ps.setString(i++, recommendation.getMessage());
            recommendation.setDateTimeCreated(ZonedDateTime.now(ZoneId.of("UTC")));
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            ps.setTimestamp(i++, Timestamp.from(recommendation.getDateTimeCreated().toInstant()), calendar);
            ps.setLong(i++, recommendation.getInnovator().getId());
            if (sql.equals(UPDATE_RECOMMENDATION)) {
                ps.setLong(i++, recommendation.getId());
            }
            ps.executeUpdate();
        } catch (final Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        recommendation.setId(this.getRecommendation(recommendation.getInnovator(), recommendation.getDateTimeCreated()).getId());
        return recommendation;
    }

    private Recommendation recommendationMapper(ResultSet rs) throws Exception {
        final Recommendation recommendation = new Recommendation();
        recommendation.setId(rs.getLong("id"));
        recommendation.setMessage(rs.getString("message"));
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        recommendation.setDateTimeCreated(ZonedDateTime.from(rs.getTimestamp("date_time_created", calendar).toInstant().atZone(ZoneId.of("UTC"))));

        final Innovator innovator = new Innovator();
        innovator.setEmailAddress(rs.getString("email_address"));
        recommendation.setInnovator(innovator);

        return recommendation;
    }
}