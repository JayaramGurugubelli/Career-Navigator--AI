package career_Navigator_parent.resume.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {

    /**
     * Store uploaded file.
     *
     * @param file Multipart file
     * @return Stored unique filename
     */
    String storeFile(
            MultipartFile file
    );


    /**
     * Load file for download.
     */
    Resource loadFile(
            String storedFileName
    );


    /**
     * Delete file from storage.
     */
    void deleteFile(
            String storedFileName
    );

}