package career_Navigator_parent.resume.mapper;

import career_Navigator_parent.resume.dto.response.ResumeUploadResponse;
import career_Navigator_parent.resume.entity.ResumeUpload;
import career_Navigator_parent.student.entity.Student;
import org.springframework.stereotype.Component;

@Component
public class ResumeUploadMapper {

    /**
     * Convert Entity to Response
     */
    public ResumeUploadResponse toResponse(
            ResumeUpload resumeUpload
    ) {

        return ResumeUploadResponse.builder()
                .id(resumeUpload.getId())
                .originalFileName(resumeUpload.getOriginalFileName())
                .storedFileName(resumeUpload.getStoredFileName())
                .fileType(resumeUpload.getFileType())
                .fileSize(resumeUpload.getFileSize())
                .version(resumeUpload.getVersion())
                .active(resumeUpload.getActive())
                .uploadedAt(resumeUpload.getUploadedAt())
                .updatedAt(resumeUpload.getUpdatedAt())
                .build();
    }

    /**
     * Create Entity
     */
    public ResumeUpload toEntity(
            Student student,
            String originalFileName,
            String storedFileName,
            String fileType,
            Long fileSize,
            String storagePath,
            Integer version
    ) {

        return ResumeUpload.builder()
                .student(student)
                .originalFileName(originalFileName)
                .storedFileName(storedFileName)
                .fileType(fileType)
                .fileSize(fileSize)
                .storagePath(storagePath)
                .version(version)
                .active(true)
                .build();
    }


    /**
     * Activate / Deactivate Resume
     */
    public void updateEntity(
            ResumeUpload upload,
            Boolean active
    ) {

        upload.setActive(active);

    }

}