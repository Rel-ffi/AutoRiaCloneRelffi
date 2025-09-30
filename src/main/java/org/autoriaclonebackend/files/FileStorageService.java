package org.autoriaclonebackend.files;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@Data
public class FileStorageService {

    @Value("${app.upload.dir}") // например, uploads
    private String uploadDir;

    /**
     * Сохраняет файл и возвращает его имя (для формирования URL)
     */
    public String save(MultipartFile file) {
        try {
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path filepath = uploadPath.resolve(filename);

            Files.copy(file.getInputStream(), filepath);

            return filename; // возвращаем имя файла
        } catch (IOException e) {
            throw new RuntimeException("Не удалось сохранить файл", e);
        }
    }

    /**
     * Получение полного пути к файлу по имени
     */
    public Path getFilePath(String filename) {
        return Paths.get(uploadDir).resolve(filename);
    }
}
