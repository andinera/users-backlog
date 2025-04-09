package users_backlog.models;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class Idea extends Model {
    
    private String summary;
    private String description;
    private Innovator innovator;
    private List<Implementation> implementations;
    private List<Category> categories;
    private Long votes;
    private List<Recommendation<Idea>> recommendations;

    public Idea() {
    }

    public static Idea fromMap(Map<String, Object> map) {
        Idea idea = new Idea();
        idea.setId(Long.parseLong((String)map.get("id")));
        idea.setSummary((String)map.get("summary"));
        return idea;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", String.valueOf(this.getId()));
        map.put("summary", this.summary);
        return map;
    }

    public String getSummary() {
        return this.summary;
    }

    public void setSummary(final String summary) {
        this.summary = summary;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public Innovator getInnovator() {
        return this.innovator;
    }

    public void setInnovator(final Innovator innovator) {
        this.innovator = innovator;
    }

    public List<Implementation> getImplementations() {
        return this.implementations;
    }

    public void setImplementations(final List<Implementation> implementations) {
        this.implementations = implementations;
    }

    public List<Category> getCategories() {
        return this.categories;
    }

    public void setCategories(final List<Category> categories) {
        this.categories = categories;
    }

    public Long getVotes() {
        return this.votes;
    }

    public void setVotes(final Long votes) {
        this.votes = votes;
    }

    public List<Recommendation<Idea>> getRecommendations() {
        return this.recommendations;
    }

    public void setRecommendations(final List<Recommendation<Idea>> recommendations) {
        this.recommendations = recommendations;
    }
}