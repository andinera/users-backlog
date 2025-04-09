package idea_service.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import idea_service.models.Implementation;
import idea_service.models.Product;

@Repository
public class ProductDAO {
    
    private final String GET_PRODUCTS = 
        "SELECT " +
            "p.url, " +
            "p.description " +
        "FROM product p " +
        "WHERE p.implementation_name = ?";

    @Autowired DataSource dataSource;

    public List<Product> getProducts(final Implementation implementation) {
        List<Product> products = new ArrayList<>();
        try (PreparedStatement ps = dataSource.getConnection().prepareStatement(GET_PRODUCTS)) {
            int i = 1;
            ps.setString(i++, implementation.getName());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    final Product product = new Product();
                    product.setURL(rs.getString("url"));
                    product.setDescription(rs.getString("description"));
                    products.add(product);
                }
            }
        } catch (final SQLException e) {
            products = null;
            System.out.println(e.getMessage());
        }
        return products;
    }

}