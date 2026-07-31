package career_Navigator_parent.admin.service.impl;

import career_Navigator_parent.admin.dto.request.RecruiterVerificationRequest;
import career_Navigator_parent.admin.dto.request.RejectRecruiterRequest;
import career_Navigator_parent.admin.dto.response.AdminRecruiterResponse;
import career_Navigator_parent.admin.mapper.AdminMapper;
import career_Navigator_parent.admin.service.AdminRecruiterService;
import career_Navigator_parent.common.exception.ResourceNotFoundException;
import career_Navigator_parent.company.entity.RecruiterProfile;
import career_Navigator_parent.company.repository.RecruiterProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminRecruiterServiceImpl
        implements AdminRecruiterService {

    private final RecruiterProfileRepository
            recruiterProfileRepository;

    private final AdminMapper adminMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<AdminRecruiterResponse> getRecruiters(
            Boolean verified,
            Boolean active,
            Pageable pageable
    ) {

        List<RecruiterProfile> filtered =
                recruiterProfileRepository.findAll()
                        .stream()
                        .filter(profile ->
                                verified == null ||
                                profile.isVerified() == verified)
                        .filter(profile ->
                                active == null ||
                                profile.isActive() == active)
                        .toList();

        int start = Math.min(
                (int) pageable.getOffset(),
                filtered.size()
        );

        int end = Math.min(
                start + pageable.getPageSize(),
                filtered.size()
        );

        List<AdminRecruiterResponse> content =
                filtered.subList(start, end)
                        .stream()
                        .map(adminMapper::toRecruiterResponse)
                        .toList();

        return new PageImpl<>(
                content,
                pageable,
                filtered.size()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public AdminRecruiterResponse getRecruiterById(
            Long recruiterId
    ) {

        return adminMapper.toRecruiterResponse(
                getRecruiter(recruiterId)
        );
    }

    @Override
    public AdminRecruiterResponse verifyRecruiter(
            Long recruiterId,
            RecruiterVerificationRequest request
    ) {

        RecruiterProfile recruiter =
                getRecruiter(recruiterId);

        if (recruiter.isVerified()) {
            throw new IllegalStateException(
                    "Recruiter is already verified."
            );
        }

        recruiter.setVerified(true);
        recruiter.setActive(true);

        return adminMapper.toRecruiterResponse(
                recruiterProfileRepository.save(
                        recruiter
                )
        );
    }

    @Override
    public AdminRecruiterResponse rejectRecruiter(
            Long recruiterId,
            RejectRecruiterRequest request
    ) {

        RecruiterProfile recruiter =
                getRecruiter(recruiterId);

        recruiter.setVerified(false);
        recruiter.setActive(false);

        return adminMapper.toRecruiterResponse(
                recruiterProfileRepository.save(
                        recruiter
                )
        );
    }

    @Override
    public AdminRecruiterResponse
    updateRecruiterActiveStatus(
            Long recruiterId,
            boolean active
    ) {

        RecruiterProfile recruiter =
                getRecruiter(recruiterId);

        recruiter.setActive(active);

        return adminMapper.toRecruiterResponse(
                recruiterProfileRepository.save(
                        recruiter
                )
        );
    }

    private RecruiterProfile getRecruiter(
            Long recruiterId
    ) {

        return recruiterProfileRepository
                .findById(recruiterId)
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Recruiter profile not found."
                        )
                );
    }
}
