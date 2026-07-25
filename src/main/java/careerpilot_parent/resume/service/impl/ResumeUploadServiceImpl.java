package careerpilot_parent.resume.service.impl;

import careerpilot_parent.ats.entity.AtsScanResult;
import careerpilot_parent.ats.repository.AtsScanResultRepository;
import careerpilot_parent.common.exception.ResourceNotFoundException;
import careerpilot_parent.resume.dto.request.UpdateResumeUploadRequest;
import careerpilot_parent.resume.dto.response.ResumeUploadResponse;
import careerpilot_parent.resume.entity.Resume;
import careerpilot_parent.resume.entity.ResumeUpload;
import careerpilot_parent.resume.mapper.ResumeUploadMapper;
import careerpilot_parent.resume.repository.ResumeRepository;
import careerpilot_parent.resume.repository.ResumeUploadRepository;
import careerpilot_parent.resume.service.FileStorageService;
import careerpilot_parent.resume.service.ResumeUploadService;
import careerpilot_parent.security.util.SecurityUtils;
import careerpilot_parent.student.entity.Student;
import careerpilot_parent.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ResumeUploadServiceImpl
        implements ResumeUploadService {

    private final ResumeUploadRepository resumeUploadRepository;
    private final ResumeRepository resumeRepository;
    private final StudentRepository studentRepository;
    private final ResumeUploadMapper resumeUploadMapper;
    private final FileStorageService fileStorageService;
    private final SecurityUtils securityUtils;
    private final AtsScanResultRepository atsScanRepository;

    @Override
    public ResumeUploadResponse uploadResume(
            Long resumeId,
            MultipartFile file
    ) {

        Student student = getCurrentStudent();

        /*
         * Fetch the Resume and verify that it belongs
         * to the authenticated student.
         */
        Resume resume = resumeRepository
                .findByIdAndStudentId(
                        resumeId,
                        student.getId()
                )
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Resume not found with id: "
                                        + resumeId
                        )
                );

        validateFile(file);

        String storedFileName =
                fileStorageService.storeFile(file);

        /*
         * Version is calculated separately for each Resume.
         */
        Integer version = resumeUploadRepository
                .findByStudentIdAndResumeIdOrderByVersionDesc(
                        student.getId(),
                        resume.getId()
                )
                .stream()
                .findFirst()
                .map(upload -> upload.getVersion() + 1)
                .orElse(1);

        ResumeUpload resumeUpload =
                resumeUploadMapper.toEntity(
                        student,
                        file.getOriginalFilename(),
                        storedFileName,
                        file.getContentType(),
                        file.getSize(),
                        storedFileName,
                        version
                );

        /*
         * This line fixes:
         * Column 'resume_id' cannot be null
         */
        resumeUpload.setResume(resume);

        ResumeUpload savedResume =
                resumeUploadRepository.save(resumeUpload);

        return resumeUploadMapper.toResponse(savedResume);
    }

    @Override
    public ResumeUploadResponse replaceResume(
            Long uploadId,
            MultipartFile file
    ) {

        Student student = getCurrentStudent();

        validateFile(file);

        ResumeUpload existingResume =
                resumeUploadRepository
                        .findByIdAndStudentId(
                                uploadId,
                                student.getId()
                        )
                        .orElseThrow(
                                () -> new ResourceNotFoundException(
                                        "Resume upload not found with id: "
                                                + uploadId
                                )
                        );

        fileStorageService.deleteFile(
                existingResume.getStoredFileName()
        );

        String storedFileName =
                fileStorageService.storeFile(file);

        existingResume.setOriginalFileName(
                file.getOriginalFilename()
        );

        existingResume.setStoredFileName(
                storedFileName
        );

        existingResume.setFileType(
                file.getContentType()
        );

        existingResume.setFileSize(
                file.getSize()
        );

        existingResume.setStoragePath(
                storedFileName
        );

        ResumeUpload updated =
                resumeUploadRepository.save(existingResume);

        return resumeUploadMapper.toResponse(updated);
    }

    @Override
    public ResumeUploadResponse updateResume(
            Long uploadId,
            UpdateResumeUploadRequest request
    ) {

        Student student = getCurrentStudent();

        ResumeUpload resumeUpload =
                resumeUploadRepository
                        .findByIdAndStudentId(
                                uploadId,
                                student.getId()
                        )
                        .orElseThrow(
                                () -> new ResourceNotFoundException(
                                        "Resume upload not found with id: "
                                                + uploadId
                                )
                        );

        resumeUploadMapper.updateEntity(
                resumeUpload,
                request.getActive()
        );

        ResumeUpload updated =
                resumeUploadRepository.save(resumeUpload);

        return resumeUploadMapper.toResponse(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public ResumeUploadResponse getResumeById(
            Long uploadId
    ) {

        Student student = getCurrentStudent();

        ResumeUpload resumeUpload =
                resumeUploadRepository
                        .findByIdAndStudentId(
                                uploadId,
                                student.getId()
                        )
                        .orElseThrow(
                                () -> new ResourceNotFoundException(
                                        "Resume upload not found with id: "
                                                + uploadId
                                )
                        );

        return resumeUploadMapper.toResponse(resumeUpload);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResumeUploadResponse> getAllResumes() {

        Student student = getCurrentStudent();

        return resumeUploadRepository
                .findByStudentIdOrderByVersionDesc(
                        student.getId()
                )
                .stream()
                .map(resumeUploadMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Resource downloadResume(Long uploadId) {

        Student student = getCurrentStudent();

        ResumeUpload resumeUpload =
                resumeUploadRepository
                        .findByIdAndStudentId(
                                uploadId,
                                student.getId()
                        )
                        .orElseThrow(
                                () -> new ResourceNotFoundException(
                                        "Resume upload not found with id: "
                                                + uploadId
                                )
                        );

        return fileStorageService.loadFile(
                resumeUpload.getStoredFileName()
        );
    }

    @Override
    public void deleteResume(Long uploadId) {

        Student student = getCurrentStudent();

        ResumeUpload resumeUpload =
                resumeUploadRepository
                        .findByIdAndStudentId(
                                uploadId,
                                student.getId()
                        )
                        .orElseThrow(
                                () -> new ResourceNotFoundException(
                                        "Resume upload not found with id: "
                                                + uploadId
                                )
                        );

        fileStorageService.deleteFile(
                resumeUpload.getStoredFileName()
        );

        resumeUploadRepository.delete(resumeUpload);
    }

    @Override
    @Transactional(readOnly = true)
    public ResumeUpload getActiveResume(
            Long studentId
    ) {

        return resumeUploadRepository
                .findFirstByStudentIdAndActiveTrueOrderByVersionDesc(
                        studentId
                )
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "No active resume found."
                        )
                );
    }

    private Student getCurrentStudent() {

        Long userId =
                securityUtils.getCurrentUserId();

        return studentRepository
                .findByUserId(userId)
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Student not found."
                        )
                );
    }

    private void validateFile(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException(
                    "Resume file must not be empty."
            );
        }

        String fileName = file.getOriginalFilename();

        if (fileName == null || fileName.isBlank()) {
            throw new IllegalArgumentException(
                    "Resume file name is missing."
            );
        }

        String lowerCaseFileName =
                fileName.toLowerCase();

        boolean supported =
                lowerCaseFileName.endsWith(".pdf")
                        || lowerCaseFileName.endsWith(".docx");

        if (!supported) {
            throw new IllegalArgumentException(
                    "Only PDF and DOCX resume files are supported."
            );
        }
    }
    @Override
    @Transactional(readOnly = true)
    public ResumeUpload getActiveResume(
            Long studentId,
            Long resumeId
    ) {

        return resumeUploadRepository
                .findFirstByStudentIdAndResumeIdAndActiveTrueOrderByVersionDesc(
                        studentId,
                        resumeId
                )
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "No active uploaded file found for this resume."
                        )
                );
    }
    @Override
    public void deleteScan(Long scanId) {

        Student student = getCurrentStudent();

        AtsScanResult result =
                atsScanRepository
                        .findByIdAndStudentId(
                                scanId,
                                student.getId()
                        )
                        .orElseThrow(
                                () -> new ResourceNotFoundException(
                                        "ATS scan not found with id: "
                                                + scanId
                                )
                        );

        atsScanRepository.delete(result);
    }
}