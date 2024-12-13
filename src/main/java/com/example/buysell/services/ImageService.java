package com.example.buysell.services;

import com.example.buysell.dao.ImageDao;
import com.example.buysell.models.Image;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ImageService {
    private final ImageDao imageDao;

    public Image toImageEntity(MultipartFile file) throws IOException {
        Image image = new Image();
        image.setName(file.getName());
        image.setOriginalFileName(file.getOriginalFilename());
        image.setContentType(file.getContentType());
        image.setSize(file.getSize());
        image.setBytes(file.getBytes());
        return image;
    }

    public void save(Image image) {
        imageDao.save(image);
    }

    public Optional<Image> findById(Long id) {
        return imageDao.findById(id);
    }
}