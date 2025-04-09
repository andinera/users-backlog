package idea_service.models;

import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class Recommendation extends Model {

    private Idea idea;
    private Implementation implementation;
    private String message;
    private ZonedDateTime dateTimeCreated;
    private ZonedDateTime dateTimeModified;
    private Innovator innovator;

    public Recommendation() {
    }

    public Idea getIdea() {
        return this.idea;
    }

    public void setIdea(Idea idea) {
        this.idea = idea;
    }

    public Implementation getImplementation() {
        return this.implementation;
    }

    public void setImplementation(Implementation implementation) {
        this.implementation = implementation;
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