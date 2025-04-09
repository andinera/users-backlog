package users_backlog.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.stereotype.Repository;

import users_backlog.models.Implementation;
import users_backlog.models.Product;

@Repository
public class ProductDAO extends DAO {
    
    private static final Logger log = Logger.getLogger(ProductDAO.class.getName());

    private final String GET_PRODUCTS = 
        "SELECT " +
            "p.id, " +
            "p.url, " +
            "p.description, " +
            "p.implementation_id " +
        "FROM product p";

    private final String GET_PRODUCTS_BY_IMPLEMENTATION = 
        GET_PRODUCTS + " " +
        "WHERE p.implementation_id = ?";

    public List<Product> getProducts(final Implementation implementation) {
        List<Product> products = new ArrayList<>();
        try (Connection connection = dataSource.getConnection(); PreparedStatement ps = connection.prepareStatement(GET_PRODUCTS_BY_IMPLEMENTATION)) {
            ps.setLong(1, implementation.getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    products.add(productMapper(rs));
                }
            }
        } catch (final Exception e) {
            products = null;
            log.severe(e.getMessage());
        }
        return products;
    }

    private final String GET_PRODUCTS_BY_IMPLEMENTATION_AND_URL = 
        GET_PRODUCTS_BY_IMPLEMENTATION + " " +
        "AND p.url = ?";

    public Product getProduct(final Implementation implementation, final String url) {
        Product product = null;
        try (Connection connection = dataSource.getConnection(); PreparedStatement ps = connection.prepareStatement(GET_PRODUCTS_BY_IMPLEMENTATION_AND_URL)) {
            int i = 1;
            ps.setLong(i++, implementation.getId());
            ps.setString(i++, url);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    product = productMapper(rs);
                }
            }
        } catch (final Exception e) {
            log.severe(e.getMessage());
        }
        return product;
    }

    private Product productMapper(ResultSet rs) throws Exception {
        Product product = new Product();
        product.setId(rs.getLong("id"));
        product.setURL(rs.getString("url"));
        product.setDescription(rs.getString("description"));

        final Implementation implementation = new Implementation();
        implementation.setId(rs.getLong("implementation_id"));
        product.setImplementation(implementation);

        return product;
    }

    private final String INSERT_PRODUCT = 
        "INSERT " +
        "INTO product (url, description, implementation_id) " +
        "VALUES (?, ?, ?)";
    private final String UPDATE_PRODUCT = 
        "UPDATE product " +
        "SET url = ?, description = ?, implementation_id " +
        "WHERE id = ?";

    public Product postProduct(final Product product) {

        String sql = null;
        if (product.getId() == 0) {
            sql = INSERT_PRODUCT;
        } else {
            sql = UPDATE_PRODUCT;
        }

        try (Connection connection = dataSource.getConnection(); PreparedStatement ps = connection.prepareStatement(sql)) {
            int i = 1;
            ps.setString(i++, product.getURL());
            ps.setString(i++, product.getDescription());
            ps.setLong(i++, product.getImplementation().getId());
            if (sql.equals(UPDATE_PRODUCT)) {
                ps.setLong(i++, product.getId());
            }
            ps.executeUpdate();
        } catch (final Exception e) {
            log.severe(e.getMessage());
        }

        product.setId(this.getProduct(product.getImplementation(), product.getURL()).getId());
        return product;
    }

    private final String DELETE_PRODUCT = 
        "DELETE " +
        "FROM product p " +
        "WHERE p.id = ?";

    public boolean deleteProduct(final Product product) {

        try (Connection connection = dataSource.getConnection(); PreparedStatement ps = connection.prepareStatement(DELETE_PRODUCT)) {
            ps.setLong(1, product.getId());
            return ps.executeUpdate() > 0;
        } catch (final Exception e) {
            log.severe(e.getMessage());
        }

        return false;
    }

}