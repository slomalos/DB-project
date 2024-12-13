package com.example.buysell.dao;

import com.example.buysell.models.Image;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class ImageDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ImageDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public Optional<Image> findById(Long id) {
        String sql = "SELECT * FROM images WHERE id = ?";
        return Optional.ofNullable(jdbcTemplate.queryForObject(sql, new Object[]{id}, new ImageRowMapper()));
    }

    public List<Image> findByProductId(Long productId) {
        String sql = "SELECT * FROM images WHERE product_id = ?";
        return jdbcTemplate.query(sql, new Object[]{productId}, new ImageRowMapper());
    }

    public Long save(Image image) {
        String sql = "INSERT INTO images (name, original_file_name, size, content_type, is_preview_image, bytes, product_id) VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING id";
        return jdbcTemplate.queryForObject(sql,
                new Object[]{
                        image.getName(),
                        image.getOriginalFileName(),
                        image.getSize(),
                        image.getContentType(),
                        image.isPreviewImage(),
                        image.getBytes(),
                        image.getProduct().getId()
                },
                Long.class);
    }

    public void delete(Long id) {
        String sql = "DELETE FROM images WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    private static class ImageRowMapper implements RowMapper<Image> {
        @Override
        public Image mapRow(ResultSet rs, int rowNum) throws SQLException {
            Image image = new Image();
            image.setId(rs.getLong("id"));
            image.setName(rs.getString("name"));
            image.setOriginalFileName(rs.getString("original_file_name"));
            image.setSize(rs.getLong("size"));
            image.setContentType(rs.getString("content_type"));
            image.setPreviewImage(rs.getBoolean("is_preview_image"));
            image.setBytes(rs.getBytes("bytes"));
            return image;
        }
    }
}