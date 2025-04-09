package users_backlog.models;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class Category extends Model {

    private String name;

    public Category() {
    }

    public static Category fromMap(Map<String, Object> map) {
        return null;
    }

    public Map<String, Object> toMap() {
        return null;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }
}