package com.example.buysell.services;

import com.example.buysell.dao.ProductDao;
import com.example.buysell.dao.UserDao;
import com.example.buysell.models.Image;
import com.example.buysell.models.Product;
import com.example.buysell.models.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductService {
    private final ProductDao productDao;
    private final UserDao userDao;
    private final ImageService imageService;

    public List<Product> listProducts(String title) {
        if (title != null) return productDao.findByTitle(title);
        return productDao.findAll();
    }

    public void saveProduct(Principal principal, Product product, MultipartFile file1, MultipartFile file2, MultipartFile file3) throws IOException {
        product.setUser(getUserByPrincipal(principal));
        product.setDateOfCreated(LocalDateTime.now());

        Image image1 = null;
        Image image2 = null;
        Image image3 = null;

        if (file1.getSize() != 0) {
            image1 = imageService.toImageEntity(file1);
            image1.setPreviewImage(true);
            product.addImageToProduct(image1);
        }
        if (file2.getSize() != 0) {
            image2 = imageService.toImageEntity(file2);
            product.addImageToProduct(image2);
        }
        if (file3.getSize() != 0) {
            image3 = imageService.toImageEntity(file3);
            product.addImageToProduct(image3);
        }

        log.info("Saving new Product. Title: {}; Author email: {}", product.getTitle(), product.getUser().getEmail());
        productDao.save(product);

        if (image1 != null) {
            imageService.save(image1);
            product.setPreviewImageId(image1.getId());
        }
        if (image2 != null) imageService.save(image2);
        if (image3 != null) imageService.save(image3);

        productDao.update(product);
    }

    public User getUserByPrincipal(Principal principal) {
        if (principal == null) return new User();
        return userDao.findByEmail(principal.getName());
    }

    public void deleteProduct(User user, Long id) {
        Product product = productDao.findById(id);
        if (product != null) {
            if (product.getUser().getId().equals(user.getId())) {
                productDao.delete(id);
                log.info("Product with id = {} was deleted", id);
            } else {
                log.error("User: {} haven't this product with id = {}", user.getEmail(), id);
            }
        } else {
            log.error("Product with id = {} is not found", id);
        }
    }

    public Product getProductById(Long id) {
        return productDao.findById(id);
    }
}