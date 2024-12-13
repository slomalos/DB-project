package com.example.buysell.dao;

import com.example.buysell.models.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class ProductDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ProductDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<Product> findAll() {
        String sql = "SELECT * FROM products";
        return jdbcTemplate.query(sql, new ProductRowMapper());
    }

    public List<Product> findByTitle(String title) {
        String sql = "SELECT * FROM products WHERE title LIKE ?";
        return jdbcTemplate.query(sql, new Object[]{"%" + title + "%"}, new ProductRowMapper());
    }

    public Product findById(Long id) {
        String sql = "SELECT * FROM products WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{id}, new ProductRowMapper());
    }

    public void save(Product product) {
        String sql = "INSERT INTO products (title, description, price, city, user_id, preview_image_id, date_of_created) VALUES (?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, product.getTitle(), product.getDescription(), product.getPrice(), product.getCity(), product.getUser().getId(), product.getPreviewImageId(), product.getDateOfCreated());
    }

    public void update(Product product) {
        String sql = "UPDATE products SET title = ?, description = ?, price = ?, city = ?, user_id = ?, preview_image_id = ?, date_of_created = ? WHERE id = ?";
        jdbcTemplate.update(sql, product.getTitle(), product.getDescription(), product.getPrice(), product.getCity(), product.getUser().getId(), product.getPreviewImageId(), product.getDateOfCreated(), product.getId());
    }

    public void delete(Long id) {
        String sql = "DELETE FROM products WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    private static class ProductRowMapper implements RowMapper<Product> {
        @Override
        public Product mapRow(ResultSet rs, int rowNum) throws SQLException {
            Product product = new Product();
            product.setId(rs.getLong("id"));
            product.setTitle(rs.getString("title"));
            product.setDescription(rs.getString("description"));
            product.setPrice(rs.getInt("price"));
            product.setCity(rs.getString("city"));
            product.setPreviewImageId(rs.getLong("preview_image_id"));
            product.setDateOfCreated(rs.getTimestamp("date_of_created").toLocalDateTime());
            return product;
        }
    }
}
