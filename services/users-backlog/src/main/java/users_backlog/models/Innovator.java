package users_backlog.models;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class Innovator extends Model {

    private String emailAddress;
    private Boolean hideEmailAddress;
    private String displayName;
    private List<Idea> ideas;
    private List<Implementation> implementations;

    public Innovator() {
    }

    public static Innovator fromMap(Map<String, Object> map) {
        Innovator innovator = new Innovator();
        innovator.setId(Long.parseLong((String)map.get("id")));
        innovator.setDisplayName((String)map.get("displayName"));
        return innovator;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", String.valueOf(this.getId()));
        map.put("displayName", this.displayName);
        return map;
    }

    public String getEmailAddress() {
        return this.emailAddress;
    }

    public void setEmailAddress(final String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public Boolean getHideEmailAddress() {
        return this.hideEmailAddress;
    }

    public void setHideEmailAddress(final Boolean hideEmailAddress) {
        this.hideEmailAddress = hideEmailAddress;
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