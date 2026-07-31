package career_Navigator_parent.student.service.impl;
import career_Navigator_parent.common.exception.ResourceNotFoundException;
import career_Navigator_parent.security.util.SecurityUtils;
import career_Navigator_parent.student.dto.request.StudentEducationRequest;
import career_Navigator_parent.student.dto.response.StudentEducationResponse;
import career_Navigator_parent.student.entity.Student;
import career_Navigator_parent.student.entity.StudentEducation;
import career_Navigator_parent.student.mapper.StudentEducationMapper;
import career_Navigator_parent.student.repository.StudentEducationRepository;
import career_Navigator_parent.student.repository.StudentRepository;
import career_Navigator_parent.student.service.StudentEducationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class StudentEducationServiceImpl implements StudentEducationService {

    private final StudentRepository studentRepository;

    private final StudentEducationRepository educationRepository;

    private final StudentEducationMapper educationMapper;

    private final SecurityUtils securityUtils;

    /**
     * Returns currently logged-in student's record.
     */
    private Student getCurrentStudent() {

        Long userId = securityUtils.getCurrentUserId();

        return studentRepository.findByUserId(userId).orElseThrow(() ->
                        new ResourceNotFoundException("Student profile not found."));
    }

    @Override
    public StudentEducationResponse addEducation(StudentEducationRequest request) {

        Student student = getCurrentStudent();

        StudentEducation education = educationMapper.toEntity(request, student);

        education = educationRepository.save(education);

        return educationMapper.toResponse(education);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentEducationResponse> getEducations() {

        Student student = getCurrentStudent();

        return educationRepository.findByStudentId(student.getId())
                .stream()
                .map(educationMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public StudentEducationResponse getEducationById(Long educationId) {

        Student student = getCurrentStudent();

        StudentEducation education = educationRepository.findByIdAndStudentId(educationId, student.getId()).orElseThrow(() ->
                                new ResourceNotFoundException("Education not found."));

        return educationMapper.toResponse(education);
    }

    @Override
    public StudentEducationResponse updateEducation(
            Long educationId,
            StudentEducationRequest request) {

        Student student = getCurrentStudent();

        StudentEducation education = educationRepository
                        .findByIdAndStudentId(educationId, student.getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Education not found."));

        educationMapper.updateEntity(education, request);

        education = educationRepository.save(education);

        return educationMapper.toResponse(education);
    }

    @Override
    public void deleteEducation(Long educationId) {

        Student student = getCurrentStudent();

        StudentEducation education = educationRepository
                        .findByIdAndStudentId(educationId, student.getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Education not found."));

        educationRepository.delete(education);
    }
}