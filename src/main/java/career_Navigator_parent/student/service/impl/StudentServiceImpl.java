package career_Navigator_parent.student.service.impl;

import career_Navigator_parent.auth.exception.StudentAlreadyExistsException;
import career_Navigator_parent.common.exception.ResourceNotFoundException;
import career_Navigator_parent.common.exception.UserNotFoundException;
import career_Navigator_parent.security.util.SecurityUtils;
import career_Navigator_parent.student.dto.request.CreateStudentRequest;
import career_Navigator_parent.student.dto.request.UpdateStudentRequest;
import career_Navigator_parent.student.dto.response.StudentResponse;
import career_Navigator_parent.student.entity.Student;
import career_Navigator_parent.student.mapper.StudentMapper;
import career_Navigator_parent.student.repository.StudentRepository;
import career_Navigator_parent.student.service.StudentService;
import career_Navigator_parent.user.entity.User;
import career_Navigator_parent.user.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class StudentServiceImpl implements StudentService {
    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final StudentMapper studentMapper;
    private final SecurityUtils securityUtils;
    @Override
    public StudentResponse create(CreateStudentRequest request) {

        Long userId = securityUtils.getCurrentUserId();

        if (studentRepository.existsByUserId(userId)) {
            throw new StudentAlreadyExistsException("Student profile already exists.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found."));

        Student student = studentMapper.toEntity(request);
        student.setUser(user);

        Student savedStudent = studentRepository.save(student);

        return studentMapper.toResponse(savedStudent);
    }
    @Override
    @Transactional(readOnly = true)
    public StudentResponse getCurrentStudent() {

        Long userId = securityUtils.getCurrentUserId();

        Student student = studentRepository.findByUserId(userId).orElseThrow(() -> new ResourceNotFoundException("Student profile not found."));

        return studentMapper.toResponse(student);
    }
    @Override
    public StudentResponse update(UpdateStudentRequest request) {

        Long userId = securityUtils.getCurrentUserId();

        Student student = studentRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Student profile not found."));

        studentMapper.update(student, request);

        student.setProfileCompleted(true);

        Student updatedStudent = studentRepository.save(student);

        return studentMapper.toResponse(updatedStudent);
    }
    @Override
    public void delete() {

        Long userId = securityUtils.getCurrentUserId();

        Student student = studentRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Student profile not found."));

        studentRepository.delete(student);
    }
}
