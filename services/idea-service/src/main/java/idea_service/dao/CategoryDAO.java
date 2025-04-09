package idea_service.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import idea_service.models.Category;
import idea_service.models.Idea;

@Repository
public class CategoryDAO {

    @Autowired DataSource dataSource;

    private final String GET_ALL_CATEGORIES = 
        "SELECT " +
            "category.name " +
        "FROM category";
    private final String GET_CATEGORIES_BY_IDEA = 
        GET_ALL_CATEGORIES + " " +
        "INNER JOIN idea_category ic " +
            "ON ic.category_name = category.name " +
        "WHERE ic.idea_summary = ?";
    private final String POST_CATEGORY =
        "INSERT " +
        "INTO category (name) " +
        "VALUES (?)";

    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();
        try (PreparedStatement ps = dataSource.getConnection().prepareStatement(GET_ALL_CATEGORIES)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    final Category category = new Category();
                    category.setName(rs.getString("name"));
                    categories.add(category);
                }
            }
        } catch (final SQLException e) {
            categories = null;
            System.out.println(e.getMessage());
        }
        return categories;
    }

    public List<Category> getCategories(final Idea idea) {
        List<Category> categories = new ArrayList<>();
        try (PreparedStatement ps = dataSource.getConnection().prepareStatement(GET_CATEGORIES_BY_IDEA)) {
            int i = 1;
            ps.setString(i++, idea.getSummary());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    final Category category = new Category();
                    category.setName(rs.getString("name"));
                    categories.add(category);
                }
            }
        } catch (final SQLException e) {
            categories = null;
            System.out.println(e.getMessage());
        }
        return categories;
    }

    public List<Category> postCategories(final List<Category> categories) {
        try (PreparedStatement ps = dataSource.getConnection().prepareStatement(POST_CATEGORY)) {
            for (Category category : categories) {
                int i = 1;
                ps.setString(i++, category.getName());
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (final SQLException e) {
            System.out.println(e.getMessage());
        }
        return categories;
    }

}