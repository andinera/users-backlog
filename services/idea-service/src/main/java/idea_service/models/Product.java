package idea_service.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class Product {

    private String url;
    private String description;

    public Product() {
    }

    public String getURL() {
        return this.url;
    }

    public void setURL(String url) {
        this.url = url;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}