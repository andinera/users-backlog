
package idea_service.models;

public class Implementation {
    private String source;
    private Innovator implementer;
    private Idea idea;

    public Implementation() {
    }

    public String getSource() {
        return this.source;
    }

    public void setSource(final String source) {
        this.source = source;
    }

    public Innovator getImplementer() {
        return this.implementer;
    }

    public void setImplementer(final Innovator implementer) {
        this.implementer = implementer;
    }

    public Idea getIdea() {
        return this.idea;
    }

    public void setIdea(Idea idea) {
        this.idea = idea;
    }
}