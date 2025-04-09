package idea_service.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
            "category.id, " +
            "category.name " +
        "FROM category";

    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();
        try (PreparedStatement ps = dataSource.getConnection().prepareStatement(GET_ALL_CATEGORIES)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    categories.add(categoryMapper(rs));
                }
            }
        } catch (final Exception e) {
            categories = null;
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return categories;
    }

    private final String GET_CATEGORIES_BY_IDEA = 
        GET_ALL_CATEGORIES + " " +
        "INNER JOIN idea_category ic " +
            "ON (ic.category_id = category.id " +
            "AND ic.idea_id = ?)";

    public List<Category> getCategories(final Idea idea) {
        List<Category> categories = new ArrayList<>();
        try (PreparedStatement ps = dataSource.getConnection().prepareStatement(GET_CATEGORIES_BY_IDEA)) {
            ps.setLong(1, idea.getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    categories.add(categoryMapper(rs));
                }
            }
        } catch (final Exception e) {
            categories = null;
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return categories;
    }

    private final String POST_CATEGORY =
        "INSERT " +
        "INTO category (name) " +
        "VALUES (?)";

    public List<Category> postCategories(final List<Category> categories) {
        List<Category> queriedCategories = this.getAllCategories();
        try (PreparedStatement ps = dataSource.getConnection().prepareStatement(POST_CATEGORY)) {
            for (Category category : categories) {
                if (queriedCategories.stream().noneMatch(cat -> cat.getName().equals(category.getName()))) {
                    ps.setString(1, category.getName());
                    ps.addBatch();
                }
            }
            ps.executeBatch();
        } catch (final Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return categories;
    }

    private Category categoryMapper(ResultSet rs) throws Exception {
        Category category = new Category();
        category.setId(rs.getLong("id"));
        category.setName(rs.getString("name"));

        return category;
    }

}