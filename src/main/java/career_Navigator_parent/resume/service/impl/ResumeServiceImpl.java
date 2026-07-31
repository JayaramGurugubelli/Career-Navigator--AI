package career_Navigator_parent.resume.service.impl;

import career_Navigator_parent.common.exception.ResourceNotFoundException;
import career_Navigator_parent.resume.dto.request.CreateResumeRequest;
import career_Navigator_parent.resume.dto.request.UpdateResumeRequest;
import career_Navigator_parent.resume.dto.response.ResumeResponse;
import career_Navigator_parent.resume.entity.Resume;
import career_Navigator_parent.resume.mapper.ResumeMapper;
import career_Navigator_parent.resume.repository.ResumeRepository;
import career_Navigator_parent.resume.service.ResumeService;
import career_Navigator_parent.security.util.SecurityUtils;
import career_Navigator_parent.student.entity.Student;
import career_Navigator_parent.student.repository.StudentRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional
public class ResumeServiceImpl implements ResumeService {


    private final ResumeRepository resumeRepository;

    private final StudentRepository studentRepository;

    private final ResumeMapper resumeMapper;
    private final SecurityUtils securityUtils;


    @Override
    public ResumeResponse createResume(CreateResumeRequest request) {


        Long userId = securityUtils.getCurrentUserId();


        Student student =
                studentRepository.findByUserId(userId)
                        .orElseThrow(
                                () -> new ResourceNotFoundException(
                                        "Student not found for user id : "
                                                + userId
                                )
                        );


        Resume resume =
                resumeMapper.toEntity(
                        request,
                        student
                );


        Resume savedResume =
                resumeRepository.save(resume);


        return resumeMapper.toResponse(savedResume);
    }



    @Override
    public ResumeResponse updateResume(Long resumeId, UpdateResumeRequest request) {


        Resume resume =
                resumeRepository.findById(resumeId)
                        .orElseThrow(
                                () -> new ResourceNotFoundException(
                                        "Resume not found with id : "
                                                + resumeId
                                )
                        );


        resumeMapper.updateEntity(
                resume,
                request
        );


        return resumeMapper.toResponse(
                resumeRepository.save(resume)
        );
    }



    @Override
    @Transactional(readOnly = true)
    public ResumeResponse getResumeById(Long resumeId) {


        Resume resume =
                resumeRepository.findById(resumeId)
                        .orElseThrow(
                                () -> new ResourceNotFoundException(
                                        "Resume not found with id : "
                                                + resumeId
                                )
                        );


        return resumeMapper.toResponse(resume);
    }




    @Override
    @Transactional(readOnly = true)
    public List<ResumeResponse> getAllResumes() {


        Long userId = securityUtils.getCurrentUserId();


        Student student =
                studentRepository.findByUserId(userId)
                        .orElseThrow(
                                () -> new ResourceNotFoundException(
                                        "Student not found"
                                )
                        );


        return resumeRepository
                .findByStudentId(student.getId())
                .stream()
                .map(resumeMapper::toResponse)
                .toList();
    }



    @Override
    public void deleteResume(Long resumeId) {


        Resume resume =
                resumeRepository.findById(resumeId)
                        .orElseThrow(
                                () -> new ResourceNotFoundException(
                                        "Resume not found with id : "
                                                + resumeId
                                )
                        );


        resumeRepository.delete(resume);

    }

}
