package careerpilot_parent.resume.service;

import careerpilot_parent.resume.dto.request.UpdateResumeUploadRequest;
import careerpilot_parent.resume.dto.response.ResumeUploadResponse;
import careerpilot_parent.resume.entity.ResumeUpload;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ResumeUploadService {

    ResumeUploadResponse uploadResume(
            Long resumeId,
            MultipartFile file
    );

    ResumeUploadResponse replaceResume(
            Long uploadId,
            MultipartFile file
    );

    ResumeUploadResponse updateResume(
            Long uploadId,
            UpdateResumeUploadRequest request
    );

    ResumeUploadResponse getResumeById(Long uploadId);

    List<ResumeUploadResponse> getAllResumes();

    Resource downloadResume(Long uploadId);

    void deleteResume(Long uploadId);

    ResumeUpload getActiveResume(Long studentId);

    ResumeUpload getActiveResume(
            Long studentId,
            Long resumeId
    );
    public void deleteScan(Long scanId);
}