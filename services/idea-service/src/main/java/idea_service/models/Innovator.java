package idea_service.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class Innovator {

    private String emailAddress;
    private List<Idea> ideas;
    private List<Implementation> implementations;

    public Innovator() {
    }

    public String getEmailAddress() {
        return this.emailAddress;
    }

    public void setEmailAddress(final String emailAddress) {
        this.emailAddress = emailAddress;
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