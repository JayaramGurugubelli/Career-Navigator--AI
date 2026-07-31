package career_Navigator_parent.ats.service.impl;

import career_Navigator_parent.ats.calculator.AtsScoreCalculator;
import career_Navigator_parent.ats.dto.request.CreateAtsScanRequest;
import career_Navigator_parent.ats.dto.response.AtsScanResponse;
import career_Navigator_parent.ats.engine.ResumeTextExtractor;
import career_Navigator_parent.ats.entity.AtsScanResult;
import career_Navigator_parent.ats.extractor.JobKeyword;
import career_Navigator_parent.ats.extractor.JobKeywordExtractor;
import career_Navigator_parent.ats.extractor.SkillExtractor;
import career_Navigator_parent.ats.mapper.AtsScanMapper;
import career_Navigator_parent.ats.model.AtsAnalysisResult;
import career_Navigator_parent.ats.model.ExtractedSkill;
import career_Navigator_parent.ats.repository.AtsScanResultRepository;
import career_Navigator_parent.ats.service.AtsScanService;

import career_Navigator_parent.common.exception.ResourceNotFoundException;

import career_Navigator_parent.resume.entity.Resume;
import career_Navigator_parent.resume.entity.ResumeUpload;
import career_Navigator_parent.resume.parser.TextCleaner;
import career_Navigator_parent.resume.repository.ResumeRepository;
import career_Navigator_parent.resume.service.FileStorageService;
import career_Navigator_parent.resume.service.ResumeUploadService;

import career_Navigator_parent.security.util.SecurityUtils;

import career_Navigator_parent.student.entity.Student;
import career_Navigator_parent.student.repository.StudentRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AtsScanServiceImpl implements AtsScanService {

    private final AtsScanResultRepository atsScanRepository;

    private final ResumeRepository resumeRepository;

    private final StudentRepository studentRepository;

    private final AtsScanMapper atsScanMapper;

    private final SecurityUtils securityUtils;

    private final ResumeUploadService resumeUploadService;

    private final FileStorageService fileStorageService;

    private final ResumeTextExtractor resumeTextExtractor;

    private final TextCleaner textCleaner;

    private final SkillExtractor skillExtractor;

    private final JobKeywordExtractor jobKeywordExtractor;

    private final AtsScoreCalculator atsScoreCalculator;

    @Override
    public AtsScanResponse createScan(
            CreateAtsScanRequest request
    ) {

        Student student = getCurrentStudent();

        /*
         * Verify that the selected builder resume
         * belongs to the logged-in student.
         */
        Resume resume =
                resumeRepository
                        .findByIdAndStudentId(
                                request.getResumeId(),
                                student.getId()
                        )
                        .orElseThrow(
                                () -> new ResourceNotFoundException(
                                        "Resume not found."
                                )
                        );

        /*
         * Get the latest active uploaded PDF/DOCX
         * belonging to the current student.
         */
        ResumeUpload resumeUpload =
                resumeUploadService.getActiveResume(
                        student.getId(),
                        resume.getId()
                );

        /*
         * Load the uploaded file from local storage.
         */
        Resource resumeResource =
                fileStorageService.loadFile(
                        resumeUpload.getStoredFileName()
                );

        /*
         * Extract text from the PDF or DOCX file.
         */
        String extractedResumeText =
                resumeTextExtractor.extractText(
                        resumeResource,
                        resumeUpload.getFileType()
                );

        if (extractedResumeText == null ||
                extractedResumeText.isBlank()) {

            throw new IllegalArgumentException(
                    "No readable text was found in the uploaded resume."
            );
        }

        /*
         * Clean resume text and job description.
         *
         * Your TextCleaner method is clean(),
         * not cleanText().
         */
        String cleanedResumeText =
                textCleaner.clean(
                        extractedResumeText
                );

        String cleanedJobDescription =
                textCleaner.clean(
                        request.getJobDescription()
                );

        /*
         * SkillExtractor returns List<ExtractedSkill>.
         */
        List<ExtractedSkill> resumeSkills =
                skillExtractor.extractSkills(
                        cleanedResumeText
                );

        /*
         * JobKeywordExtractor returns List<JobKeyword>.
         */
        List<JobKeyword> jobKeywords =
                jobKeywordExtractor.extractKeywords(
                        cleanedJobDescription
                );

        /*
         * AtsScoreCalculator returns AtsAnalysisResult.
         */
        AtsAnalysisResult analysisResult =
                atsScoreCalculator.calculateScore(
                        resumeSkills,
                        jobKeywords
                );

        String matchedSkills =
                convertListToString(
                        analysisResult.getMatchedSkills()
                );

        String missingSkills =
                convertListToString(
                        analysisResult.getMissingSkills()
                );

        String suggestions =
                generateSuggestions(
                        analysisResult,
                        cleanedResumeText
                );

        AtsScanResult result =
                AtsScanResult.builder()
                        .student(student)
                        .resume(resume)
                        .jobTitle(request.getJobTitle())
                        .companyName(request.getCompanyName())
                        .jobDescription(request.getJobDescription())
                        .atsScore(
                                analysisResult.getAtsScore()
                        )
                        .matchedSkills(matchedSkills)
                        .missingSkills(missingSkills)
                        .suggestions(suggestions)
                        .build();

        AtsScanResult savedResult =
                atsScanRepository.save(result);

        return atsScanMapper.toResponse(savedResult);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AtsScanResponse> getMyScans() {

        Student student = getCurrentStudent();

        return atsScanRepository
                .findByStudentIdOrderByCreatedAtDesc(
                        student.getId()
                )
                .stream()
                .map(atsScanMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AtsScanResponse getScanById(
            Long scanId
    ) {

        Student student = getCurrentStudent();

        /*
         * This validates that the ATS scan belongs
         * to the logged-in student.
         */
        AtsScanResult result =
                atsScanRepository
                        .findByIdAndStudentId(
                                scanId,
                                student.getId()
                        )
                        .orElseThrow(
                                () -> new ResourceNotFoundException(
                                        "ATS scan not found."
                                )
                        );

        return atsScanMapper.toResponse(result);
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

    private String convertListToString(
            List<String> values
    ) {

        if (values == null || values.isEmpty()) {
            return "";
        }

        return String.join(", ", values);
    }

    private String generateSuggestions(
            AtsAnalysisResult analysisResult,
            String resumeText
    ) {

        StringBuilder suggestions =
                new StringBuilder();

        double atsScore =
                analysisResult.getAtsScore();

        if (atsScore >= 80) {

            suggestions.append(
                    "Your resume has a strong match with the job description. "
            );

        } else if (atsScore >= 60) {

            suggestions.append(
                    "Your resume has a moderate match. Add more relevant skills and job-specific keywords. "
            );

        } else {

            suggestions.append(
                    "Your resume has a low match. Customize the resume according to the job description. "
            );
        }

        List<String> missingSkills =
                analysisResult.getMissingSkills();

        if (missingSkills != null &&
                !missingSkills.isEmpty()) {

            suggestions
                    .append(
                            "Consider adding these skills if you genuinely have experience with them: "
                    )
                    .append(
                            String.join(
                                    ", ",
                                    missingSkills
                            )
                    )
                    .append(". ");
        }

        List<String> matchedSkills =
                analysisResult.getMatchedSkills();

        if (matchedSkills == null ||
                matchedSkills.isEmpty()) {

            suggestions.append(
                    "No important job skills were matched with your resume. "
            );
        }

        if (!containsSection(
                resumeText,
                "summary"
        ) && !containsSection(
                resumeText,
                "objective"
        )) {

            suggestions.append(
                    "Add a professional summary tailored to the target position. "
            );
        }

        if (!containsSection(
                resumeText,
                "experience"
        )) {

            suggestions.append(
                    "Add an experience section containing responsibilities and measurable achievements. "
            );
        }

        if (!containsSection(
                resumeText,
                "project"
        )) {

            suggestions.append(
                    "Add relevant projects demonstrating practical use of your technical skills. "
            );
        }

        if (!containsMeasurableResult(resumeText)) {

            suggestions.append(
                    "Add measurable results such as percentages, users served, performance improvements, or time saved. "
            );
        }

        return suggestions.toString().trim();
    }

    private boolean containsSection(
            String text,
            String sectionName
    ) {

        if (text == null || text.isBlank()) {
            return false;
        }

        return text
                .toLowerCase()
                .contains(
                        sectionName.toLowerCase()
                );
    }

    private boolean containsMeasurableResult(
            String text
    ) {

        if (text == null || text.isBlank()) {
            return false;
        }

        return text.matches(
                "(?s).*\\b\\d+(\\.\\d+)?\\s*" +
                        "(%|percent|users?|hours?|days?|months?|" +
                        "projects?|requests?|records?)\\b.*"
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
                                        "ATS scan not found."
                                )
                        );

        atsScanRepository.delete(result);
    }
}