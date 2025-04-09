package users_backlog.models;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class Innovator extends Model {

    private String emailAddress;
    private String displayName;
    private List<Idea> ideas;
    private List<Implementation> implementations;

    public Innovator() {
    }

    public static Innovator fromMap(Map<String, Object> map) {
        return null;
    }

    public Map<String, Object> toMap() {
        return null;
    }

    public String getEmailAddress() {
        return this.emailAddress;
    }

    public void setEmailAddress(final String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public void setDisplayName(final String displayName) {
        this.displayName = displayName;
    }

    public List<Idea> getIdeas() {
        return this.ideas;
    }

    public void setIdeas(final List<Idea> ideas) {
        this.ideas = ideas;
    }

    public List<Implementation> getImplementations() {
        return this.implementations;
    }

    public void setImplementations(final List<Implementation> implementations) {
        this.implementations = implementations;
    }
}