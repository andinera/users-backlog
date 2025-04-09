package idea_service.controllers;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import idea_service.models.Idea;
import idea_service.models.Innovator;

@CrossOrigin
@RestController
@RequestMapping("/idea")
public class IdeaController {

    @Autowired DataSource dataSource;

    @GetMapping(path="getAllIdeas")
    public List<Idea> getAllIdeas() {
        final List<Idea> ideas = new ArrayList<>();
        final String sql = "SELECT i.summary, i.description, i.innovator " + "FROM idea i";
        try (PreparedStatement ps = dataSource.getConnection().prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                final Idea idea = new Idea();
                idea.setSummary(rs.getString("summary"));
                idea.setDescription(rs.getString("description"));
                final Innovator innovator = new Innovator();
                innovator.setEmailAddress(rs.getString("innovator"));
                idea.setInnovator(innovator);
                ideas.add(idea);
            }
        } catch (final SQLException e) {
            System.out.println(e.getMessage());
        }
        return ideas;
    }

    @GetMapping(path = "getIdea")
    public Idea getIdea(@RequestParam final String summary) {
        final Idea idea = new Idea();
        final String sql = "SELECT i.summary, i.description, i.innovator " + "FROM idea i " + "WHERE i.summary = '"
                + summary + "'";
        try (PreparedStatement ps = dataSource.getConnection().prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                idea.setSummary(rs.getString("summary"));
                idea.setDescription(rs.getString("description"));
                final Innovator innovator = new Innovator();
                innovator.setEmailAddress(rs.getString("innovator"));
                idea.setInnovator(innovator);
            }
        } catch (final SQLException e) {
            System.out.println(e.getMessage());
        }
        return idea;
    }

    @PostMapping(path = "postIdea")
    public Idea postIdea(@RequestBody final Idea idea) {
        final String sql = "INSERT INTO idea (summary, description, innovator) " + "VALUES (?, ?, ?)";
        try (PreparedStatement ps = dataSource.getConnection().prepareStatement(sql)) {
            int i = 1;
            ps.setString(i++, idea.getSummary());
            ps.setString(i++, idea.getDescription());
            ps.setString(i++, idea.getInnovator().getEmailAddress());
            if (ps.executeUpdate() == 0) {
                return null;
            }
        } catch (final SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
        return idea;
    }

    @DeleteMapping(path = "deleteIdea")
    public boolean deleteIdea(@RequestParam final String summary) {
        boolean deleted = false;
        final String sql = "DELETE " + "FROM idea i " + "WHERE i.summary = '" + summary + "'";
        try (PreparedStatement ps = dataSource.getConnection().prepareStatement(sql)) {
            deleted = (ps.executeUpdate() != 0);
        } catch (final SQLException e) {
            System.out.println(e.getMessage());
        }
        return deleted;
    }
}