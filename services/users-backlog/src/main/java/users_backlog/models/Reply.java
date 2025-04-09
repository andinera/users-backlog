package users_backlog.models;

import java.time.ZonedDateTime;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class Reply<T> extends Model {

    private Recommendation<T> recommendation;
    private String message;
    private ZonedDateTime dateTimeCreated;
    private ZonedDateTime dateTimeModified;
    private Innovator innovator;

    public Reply() {
    }

    public static Reply<?> fromMap(Map<String, Object> map) {
        return null;
    }

    public Map<String, Object> toMap() {
        return null;
    }

    public Recommendation<T> getRecommendation() {
        return this.recommendation;
    }

    public void setRecommendation(Recommendation<T> recommendation) {
        this.recommendation = recommendation;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ZonedDateTime getDateTimeCreated() {
        return this.dateTimeCreated;
    }

    public void setDateTimeCreated(ZonedDateTime dateTimeCreated) {
        this.dateTimeCreated = dateTimeCreated;
    }

    public ZonedDateTime getDateTimeModified() {
        return this.dateTimeModified;
    }

    public void setDateTimeModified(ZonedDateTime dateTimeModified) {
        this.dateTimeModified = dateTimeModified;
    }

    public Innovator getInnovator() {
        return this.innovator;
    }

    public void setInnovator(Innovator innovator) {
        this.innovator = innovator;
    }

}