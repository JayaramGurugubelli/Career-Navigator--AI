package career_Navigator_parent.resume.service.impl;


import career_Navigator_parent.common.exception.ResourceNotFoundException;
import career_Navigator_parent.resume.dto.request.*;
import career_Navigator_parent.resume.dto.response.ResumeContentResponse;
import career_Navigator_parent.resume.entity.Resume;
import career_Navigator_parent.resume.entity.ResumeContent;
import career_Navigator_parent.resume.mapper.ResumeContentMapper;
import career_Navigator_parent.resume.repository.ResumeContentRepository;
import career_Navigator_parent.resume.repository.ResumeRepository;
import career_Navigator_parent.resume.service.ResumeContentService;


import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional
public class ResumeContentServiceImpl implements ResumeContentService {



    private final ResumeRepository resumeRepository;

    private final ResumeContentRepository contentRepository;

    private final ResumeContentMapper contentMapper;



    @Override
    public ResumeContentResponse createContent(Long resumeId, CreateResumeContentRequest request){


        Resume resume = resumeRepository.findById(resumeId).orElseThrow(() -> new ResourceNotFoundException("Resume not found : "+resumeId));


        ResumeContent content =
                contentMapper.toEntity(request, resume);


        return contentMapper.toResponse(contentRepository.save(content));

    }



    @Override
    public ResumeContentResponse updateContent(Long resumeId, UpdateResumeContentRequest request){


        ResumeContent content = contentRepository.findByResumeId(resumeId)
                        .orElseThrow(
                                () -> new ResourceNotFoundException(
                                        "Resume content not found"
                                )
                        );


        contentMapper.updateEntity(content, request);


        return contentMapper.toResponse(contentRepository.save(content));

    }



    @Override
    @Transactional(readOnly = true)
    public ResumeContentResponse getContent(
            Long resumeId
    ){


        ResumeContent content =
                contentRepository.findByResumeId(resumeId)
                        .orElseThrow(
                                () -> new ResourceNotFoundException(
                                        "Resume content not found"
                                )
                        );


        return contentMapper.toResponse(content);

    }



    @Override
    public void deleteContent(
            Long resumeId
    ){


        ResumeContent content =
                contentRepository.findByResumeId(resumeId)
                        .orElseThrow(
                                () -> new ResourceNotFoundException(
                                        "Resume content not found"
                                )
                        );


        contentRepository.delete(content);

    }

}