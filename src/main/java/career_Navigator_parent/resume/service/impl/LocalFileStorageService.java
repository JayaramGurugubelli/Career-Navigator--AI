package career_Navigator_parent.resume.service.impl;

import career_Navigator_parent.common.exception.BadRequestException;
import career_Navigator_parent.common.exception.ResourceNotFoundException;
import career_Navigator_parent.resume.service.FileStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.util.Set;
import java.util.UUID;

@Service
public class LocalFileStorageService implements FileStorageService {

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            "pdf",
            "doc",
            "docx"
    );

    private final Path storageLocation;

    public LocalFileStorageService(
            @Value("${app.file.upload-dir:uploads/resumes}") String uploadDir
    ) {

        this.storageLocation = Paths.get(uploadDir)
                .toAbsolutePath()
                .normalize();

        try {

            Files.createDirectories(storageLocation);

        } catch (IOException e) {

            throw new RuntimeException(
                    "Unable to create upload directory.",
                    e
            );
        }
    }


    @Override
    public String storeFile(
            MultipartFile file
    ) {

        if (file == null || file.isEmpty()) {

            throw new BadRequestException(
                    "Please upload a file."
            );
        }

        if (file.getSize() > MAX_FILE_SIZE) {

            throw new BadRequestException(
                    "Maximum file size is 10 MB."
            );
        }

        String originalFileName = file.getOriginalFilename();

        String extension = getExtension(originalFileName);

        if (!ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {

            throw new BadRequestException(
                    "Only PDF, DOC and DOCX files are allowed."
            );
        }

        String storedFileName =
                UUID.randomUUID() + "." + extension;

        try {

            Files.copy(
                    file.getInputStream(),
                    storageLocation.resolve(storedFileName),
                    StandardCopyOption.REPLACE_EXISTING
            );

        } catch (IOException e) {

            throw new RuntimeException(
                    "Failed to store file.",
                    e
            );
        }

        return storedFileName;
    }


    @Override
    public Resource loadFile(
            String storedFileName
    ) {

        try {

            Path filePath =
                    storageLocation.resolve(storedFileName)
                            .normalize();

            Resource resource =
                    new UrlResource(filePath.toUri());

            if (!resource.exists()) {

                throw new ResourceNotFoundException(
                        "Resume file not found."
                );
            }

            return resource;

        } catch (MalformedURLException e) {

            throw new RuntimeException(e);
        }
    }


    @Override
    public void deleteFile(
            String storedFileName
    ) {

        try {

            Files.deleteIfExists(
                    storageLocation.resolve(storedFileName)
            );

        } catch (IOException e) {

            throw new RuntimeException(
                    "Unable to delete file.",
                    e
            );
        }
    }


    private String getExtension(
            String fileName
    ) {

        if (fileName == null || !fileName.contains(".")) {

            throw new BadRequestException(
                    "Invalid file."
            );
        }

        return fileName.substring(
                fileName.lastIndexOf('.') + 1
        );
    }

}