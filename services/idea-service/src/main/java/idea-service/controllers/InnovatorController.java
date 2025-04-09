package idea_service.controllers;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import idea_service.models.Innovator;

@CrossOrigin
@RestController
@RequestMapping("/innovator")
public class InnovatorController {

    @Autowired DataSource dataSource;

    @GetMapping(path="getInnovator")
    public Innovator getInnovator(@RequestParam final String emailAddress) {
        final String sql = "SELECT inv.email_address " + "FROM innovator inv " + "WHERE inv.email_address = '"
                + emailAddress + "'";
        try (PreparedStatement ps = dataSource.getConnection().prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                final Innovator innovator = new Innovator();
                innovator.setEmailAddress(rs.getString("email_address"));
                return innovator;
            }
        } catch (final SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
        return null;
    }

    @PostMapping(path = "postInnovator")
    public Innovator postInnovator(@RequestBody final Innovator innovator) {
        final String sql = "INSERT INTO innovator (email_address) " + "VALUES (?)";
        try (PreparedStatement ps = dataSource.getConnection().prepareStatement(sql)) {
            int i = 1;
            ps.setString(i++, innovator.getEmailAddress());
            if (ps.executeUpdate() == 0) {
                return null;
            }
        } catch (final SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
        return innovator;
    }
}