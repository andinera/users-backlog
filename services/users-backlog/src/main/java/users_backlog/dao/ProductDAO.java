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
            "p.description " +
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
                    final Product product = new Product();
                    product.setId(rs.getLong("id"));
                    product.setURL(rs.getString("url"));
                    product.setDescription(rs.getString("description"));
                    products.add(product);
                }
            }
        } catch (final Exception e) {
            products = null;
            log.severe(e.getMessage());
        }
        return products;
    }

}