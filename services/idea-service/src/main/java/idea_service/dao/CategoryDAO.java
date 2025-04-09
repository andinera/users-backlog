package idea_service.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import idea_service.models.Category;
import idea_service.models.Idea;
import idea_service.models.Implementation;

@Repository
public class CategoryDAO extends DAO {

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

    private final String GET_CATEGORIES_BY_IMPLEMENTATION = 
        GET_ALL_CATEGORIES + " " +
        "INNER JOIN implementation_category ic " +
            "ON (ic.category_id = category.id " +
            "AND ic.implementation_id = ?)";

    public List<Category> getCategories(final Implementation implementation) {
        List<Category> categories = new ArrayList<>();
        try (PreparedStatement ps = dataSource.getConnection().prepareStatement(GET_CATEGORIES_BY_IMPLEMENTATION)) {
            ps.setLong(1, implementation.getId());
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

    private final String GET_CATEGORY_BY_NAME = 
        GET_ALL_CATEGORIES + " " +
        "WHERE category.name = ?";

    public Category getCategoryByName(final String name) {
        Category category = null;
        try (PreparedStatement ps = dataSource.getConnection().prepareStatement(GET_CATEGORY_BY_NAME)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    category = categoryMapper(rs);
                }
            }
        } catch (final Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return category;
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
            for (Category category: categories) {
                category.setId(this.getCategoryByName(category.getName()).getId());
            }
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