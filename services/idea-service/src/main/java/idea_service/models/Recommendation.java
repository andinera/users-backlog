package idea_service.models;

import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class Recommendation extends Model {

    private Idea idea;
    private String message;
    private ZonedDateTime dateTimeCreated;
    private Innovator innovator;

    public Recommendation() {
    }

    public Idea getIdea() {
        return this.idea;
    }

    public void setIdea(Idea idea) {
        this.idea = idea;
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

    public Innovator getInnovator() {
        return this.innovator;
    }

    public void setInnovator(Innovator innovator) {
        this.innovator = innovator;
    }

}