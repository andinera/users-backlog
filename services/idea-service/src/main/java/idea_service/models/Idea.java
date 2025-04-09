package idea_service.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class Idea extends Model {
    
    private String summary;
    private String description;
    private Innovator innovator;
    private List<Implementation> implementations;
    private List<Category> categories;
    private List<Recommendation> recommendations;

    public Idea() {
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

    public List<Recommendation> getRecommendations() {
        return this.recommendations;
    }

    public void setRecommendations(final List<Recommendation> recommendations) {
        this.recommendations = recommendations;
    }
}