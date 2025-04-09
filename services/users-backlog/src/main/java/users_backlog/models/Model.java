package users_backlog.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class Model {
    
    private long id;
    private String idToken;

    public Model() {
    }

    public long getId() {
        return this.id;
    }

    public void setId(final long id) {
        this.id = id;
    }

    public String getIdToken() {
        return this.idToken;
    }

    public void setIdToken(final String idToken) {
        this.idToken = idToken;
    }
}