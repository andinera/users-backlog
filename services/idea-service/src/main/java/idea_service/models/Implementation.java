
package idea_service.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class Implementation extends Model {
    
    private Innovator innovator;
    private List<Idea> ideas;
    private String name;
    private String description;
    private List<Category> categories;
    private List<Product> products;

    public Implementation() {
    }

    public Innovator getInnovator() {
        return this.innovator;
    }

    public void setInnovator(final Innovator innovator) {
        this.innovator = innovator;
    }

    public List<Idea> getIdeas() {
        return this.ideas;
    }

    public void setIdeas(List<Idea> ideas) {
        this.ideas = ideas;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Category> getCategories() {
        return this.categories;
    }

    public void setCategories(final List<Category> categories) {
        this.categories = categories;
    }

    public List<Product> getProducts() {
        return this.products;
    }

    public void setProducts(final List<Product> products) {
        this.products = products;
    }
}