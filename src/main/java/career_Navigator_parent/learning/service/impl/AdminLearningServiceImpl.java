package career_Navigator_parent.learning.service.impl;

import career_Navigator_parent.learning.dto.request.AdminLearningRequests;
import career_Navigator_parent.learning.dto.request.LearningStatusRequests;
import career_Navigator_parent.learning.dto.response.LearningPublicationResponses;
import career_Navigator_parent.learning.dto.response.LearningResponses;
import career_Navigator_parent.learning.entity.AcademicDiscipline;
import career_Navigator_parent.learning.entity.CareerDomain;
import career_Navigator_parent.learning.entity.CareerRole;
import career_Navigator_parent.learning.entity.Course;
import career_Navigator_parent.learning.entity.CourseModule;
import career_Navigator_parent.learning.entity.LearningPath;
import career_Navigator_parent.learning.entity.LearningPathMilestone;
import career_Navigator_parent.learning.entity.Lesson;
import career_Navigator_parent.learning.entity.PathCourse;
import career_Navigator_parent.learning.enums.ContentStatus;
import career_Navigator_parent.learning.enums.CourseType;
import career_Navigator_parent.learning.mapper.LearningMapper;
import career_Navigator_parent.learning.repository.AcademicDisciplineRepository;
import career_Navigator_parent.learning.repository.CareerDomainRepository;
import career_Navigator_parent.learning.repository.CareerRoleRepository;
import career_Navigator_parent.learning.repository.CourseModuleRepository;
import career_Navigator_parent.learning.repository.CourseRepository;
import career_Navigator_parent.learning.repository.LearningPathMilestoneRepository;
import career_Navigator_parent.learning.repository.LearningPathRepository;
import career_Navigator_parent.learning.repository.LessonRepository;
import career_Navigator_parent.learning.repository.PathCourseRepository;
import career_Navigator_parent.learning.service.AdminLearningService;
import career_Navigator_parent.learning.validation.LearningPublicationValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminLearningServiceImpl
        implements AdminLearningService {

    private final AcademicDisciplineRepository disciplineRepository;
    private final CareerDomainRepository domainRepository;
    private final CareerRoleRepository roleRepository;
    private final LearningPathRepository pathRepository;
    private final LearningPathMilestoneRepository milestoneRepository;
    private final CourseRepository courseRepository;
    private final CourseModuleRepository moduleRepository;
    private final LessonRepository lessonRepository;
    private final PathCourseRepository pathCourseRepository;
    private final LearningMapper learningMapper;
    private final LearningPublicationValidator publicationValidator;

    @Override
    public LearningResponses.Discipline createDiscipline(
            AdminLearningRequests.DisciplineCreate request
    ) {
        if (disciplineRepository.existsByCodeIgnoreCase(request.code())
                || disciplineRepository.existsByNameIgnoreCase(request.name())) {
            throw conflict(
                    "Discipline code or name already exists."
            );
        }

        AcademicDiscipline discipline =
                AcademicDiscipline.builder()
                        .name(request.name())
                        .code(request.code())
                        .description(request.description())
                        .iconUrl(request.iconUrl())
                        .displayOrder(request.displayOrder())
                        .active(request.active())
                        .build();

        return learningMapper.toDiscipline(
                disciplineRepository.save(discipline)
        );
    }

    @Override
    public LearningResponses.Domain createDomain(
            AdminLearningRequests.DomainCreate request
    ) {
        if (domainRepository.existsBySlugIgnoreCase(request.slug())
                || domainRepository.existsByNameIgnoreCase(request.name())) {
            throw conflict(
                    "Career domain name or slug already exists."
            );
        }

        CareerDomain domain =
                CareerDomain.builder()
                        .name(request.name())
                        .slug(request.slug())
                        .description(request.description())
                        .iconUrl(request.iconUrl())
                        .displayOrder(request.displayOrder())
                        .active(request.active())
                        .build();

        return learningMapper.toDomain(
                domainRepository.save(domain)
        );
    }

    @Override
    public LearningResponses.Role createRole(
            AdminLearningRequests.RoleCreate request
    ) {
        if (roleRepository.existsBySlugIgnoreCase(request.slug())) {
            throw conflict(
                    "Career role slug already exists."
            );
        }

        CareerDomain domain =
                domainRepository.findById(request.domainId())
                        .orElseThrow(() ->
                                notFound(
                                        "Career domain not found."
                                )
                        );

        Set<AcademicDiscipline> disciplines =
                new LinkedHashSet<>(
                        disciplineRepository.findAllById(
                                request.disciplineIds()
                        )
                );

        if (disciplines.size()
                != request.disciplineIds().size()) {
            throw badRequest(
                    "One or more discipline IDs are invalid."
            );
        }

        CareerRole role =
                CareerRole.builder()
                        .domain(domain)
                        .title(request.title())
                        .slug(request.slug())
                        .summary(request.summary())
                        .responsibilities(request.responsibilities())
                        .workEnvironment(request.workEnvironment())
                        .entryLevelTitles(request.entryLevelTitles())
                        .careerOutlook(request.careerOutlook())
                        .minimumQualification(request.minimumQualification())
                        .averageLearningMonths(request.averageLearningMonths())
                        .difficulty(request.difficulty())
                        .thumbnailUrl(request.thumbnailUrl())
                        .featured(request.featured())
                        .active(request.active())
                        .eligibleDisciplines(disciplines)
                        .build();

        return learningMapper.toRole(
                roleRepository.save(role)
        );
    }

    @Override
    public LearningResponses.PathSummary createPath(
            AdminLearningRequests.PathCreate request
    ) {
        int version =
                request.pathVersion() == null
                        ? 1
                        : request.pathVersion();

        if (pathRepository.existsBySlugIgnoreCaseAndPathVersion(
                request.slug(),
                version
        )) {
            throw conflict(
                    "Path slug and version already exist."
            );
        }

        CareerRole role =
                roleRepository.findById(request.careerRoleId())
                        .orElseThrow(() ->
                                notFound(
                                        "Career role not found."
                                )
                        );

        Set<AcademicDiscipline> disciplines =
                new LinkedHashSet<>(
                        disciplineRepository.findAllById(
                                request.disciplineIds()
                        )
                );

        if (disciplines.size()
                != request.disciplineIds().size()) {
            throw badRequest(
                    "One or more discipline IDs are invalid."
            );
        }

        LearningPath path =
                LearningPath.builder()
                        .careerRole(role)
                        .title(request.title())
                        .slug(request.slug())
                        .description(request.description())
                        .level(request.level())
                        .estimatedDurationHours(
                                request.estimatedDurationHours()
                        )
                        .thumbnailUrl(request.thumbnailUrl())
                        .status(ContentStatus.DRAFT)
                        .premium(request.premium())
                        .featured(request.featured())
                        .active(request.active())
                        .pathVersion(version)
                        .disciplines(disciplines)
                        .build();

        return learningMapper.toPath(
                pathRepository.save(path)
        );
    }

    @Override
    public LearningResponses.Milestone addMilestone(
            Long pathId,
            AdminLearningRequests.MilestoneCreate request
    ) {
        LearningPath path =
                pathRepository.findById(pathId)
                        .orElseThrow(() ->
                                notFound(
                                        "Learning path not found."
                                )
                        );

        if (milestoneRepository
                .existsByLearningPathIdAndSequenceNumber(
                        pathId,
                        request.sequenceNumber()
                )) {
            throw conflict(
                    "Milestone sequence number already exists."
            );
        }

        LearningPathMilestone milestone =
                LearningPathMilestone.builder()
                        .learningPath(path)
                        .title(request.title())
                        .description(request.description())
                        .milestoneType(request.milestoneType())
                        .sequenceNumber(request.sequenceNumber())
                        .mandatory(request.mandatory())
                        .estimatedHours(request.estimatedHours())
                        .active(true)
                        .build();

        LearningPathMilestone saved =
                milestoneRepository.save(milestone);

        return new LearningResponses.Milestone(
                saved.getId(),
                saved.getTitle(),
                saved.getDescription(),
                saved.getMilestoneType(),
                saved.getSequenceNumber(),
                saved.getMandatory(),
                saved.getEstimatedHours()
        );
    }

    @Override
    public LearningResponses.CourseSummary createCourse(
            AdminLearningRequests.CourseCreate request
    ) {
        int version =
                request.courseVersion() == null
                        ? 1
                        : request.courseVersion();

        if (courseRepository
                .existsBySlugIgnoreCaseAndCourseVersion(
                        request.slug(),
                        version
                )) {
            throw conflict(
                    "Course slug and version already exist."
            );
        }

        validateExternalCourseConfiguration(
                request.courseType(),
                request.externalCourseUrl()
        );

        Set<AcademicDiscipline> disciplines =
                request.disciplineIds() == null
                        ? new LinkedHashSet<>()
                        : new LinkedHashSet<>(
                        disciplineRepository.findAllById(
                                request.disciplineIds()
                        )
                );

        if (request.disciplineIds() != null
                && disciplines.size()
                != request.disciplineIds().size()) {
            throw badRequest(
                    "One or more discipline IDs are invalid."
            );
        }

        Set<Course> prerequisites =
                request.prerequisiteCourseIds() == null
                        ? new LinkedHashSet<>()
                        : new LinkedHashSet<>(
                        courseRepository.findAllById(
                                request.prerequisiteCourseIds()
                        )
                );

        if (request.prerequisiteCourseIds() != null
                && prerequisites.size()
                != request.prerequisiteCourseIds().size()) {
            throw badRequest(
                    "One or more prerequisite course IDs are invalid."
            );
        }

        Course course =
                Course.builder()
                        .title(request.title())
                        .slug(request.slug())
                        .description(request.description())
                        .learningOutcomes(request.learningOutcomes())
                        .prerequisiteDescription(
                                request.prerequisiteDescription()
                        )
                        .courseType(request.courseType())
                        .level(request.level())
                        .providerType(request.providerType())
                        .providerName(request.providerName())
                        .instructorName(request.instructorName())
                        .externalCourseUrl(
                                request.externalCourseUrl()
                        )
                        .thumbnailUrl(request.thumbnailUrl())
                        .language(request.language())
                        .estimatedDurationHours(
                                request.estimatedDurationHours()
                        )
                        .certificateEnabled(
                                request.certificateEnabled()
                        )
                        .free(request.free())
                        .featured(request.featured())
                        .active(request.active())
                        .status(ContentStatus.DRAFT)
                        .courseVersion(version)
                        .disciplines(disciplines)
                        .prerequisites(prerequisites)
                        .build();

        return learningMapper.toCourse(
                courseRepository.save(course)
        );
    }

    @Override
    public LearningResponses.Module addModule(
            Long courseId,
            AdminLearningRequests.ModuleCreate request
    ) {
        Course course =
                courseRepository.findById(courseId)
                        .orElseThrow(() ->
                                notFound(
                                        "Course not found."
                                )
                        );

        if (moduleRepository
                .existsByCourseIdAndSequenceNumber(
                        courseId,
                        request.sequenceNumber()
                )) {
            throw conflict(
                    "Module sequence number already exists."
            );
        }

        CourseModule module =
                CourseModule.builder()
                        .course(course)
                        .title(request.title())
                        .description(request.description())
                        .sequenceNumber(request.sequenceNumber())
                        .estimatedMinutes(
                                request.estimatedMinutes()
                        )
                        .mandatory(request.mandatory())
                        .previewEnabled(
                                request.previewEnabled()
                        )
                        .completionPercentageRequired(
                                request.completionPercentageRequired()
                        )
                        .active(true)
                        .build();

        CourseModule saved =
                moduleRepository.save(module);

        return new LearningResponses.Module(
                saved.getId(),
                saved.getTitle(),
                saved.getDescription(),
                saved.getSequenceNumber(),
                saved.getEstimatedMinutes(),
                saved.getMandatory(),
                List.of()
        );
    }

    @Override
    public LearningResponses.Lesson addLesson(
            Long moduleId,
            AdminLearningRequests.LessonCreate request
    ) {
        CourseModule module =
                moduleRepository.findById(moduleId)
                        .orElseThrow(() ->
                                notFound(
                                        "Course module not found."
                                )
                        );

        if (lessonRepository
                .existsByModuleIdAndSequenceNumber(
                        moduleId,
                        request.sequenceNumber()
                )) {
            throw conflict(
                    "Lesson sequence number already exists."
            );
        }

        Lesson lesson =
                Lesson.builder()
                        .module(module)
                        .title(request.title())
                        .summary(request.summary())
                        .lessonType(request.lessonType())
                        .content(request.content())
                        .videoUrl(request.videoUrl())
                        .externalUrl(request.externalUrl())
                        .fileUrl(request.fileUrl())
                        .durationMinutes(
                                request.durationMinutes()
                        )
                        .sequenceNumber(
                                request.sequenceNumber()
                        )
                        .preview(request.preview())
                        .completionRequired(
                                request.completionRequired()
                        )
                        .active(true)
                        .build();

        return learningMapper.toLesson(
                lessonRepository.save(lesson),
                null
        );
    }

    @Override
    public LearningResponses.PathCourse attachCourse(
            Long pathId,
            AdminLearningRequests.PathCourseCreate request
    ) {
        LearningPath path =
                pathRepository.findById(pathId)
                        .orElseThrow(() ->
                                notFound(
                                        "Learning path not found."
                                )
                        );

        LearningPathMilestone milestone =
                milestoneRepository.findById(
                                request.milestoneId()
                        )
                        .orElseThrow(() ->
                                notFound(
                                        "Learning path milestone not found."
                                )
                        );

        Course course =
                courseRepository.findById(
                                request.courseId()
                        )
                        .orElseThrow(() ->
                                notFound(
                                        "Course not found."
                                )
                        );

        if (!milestone.getLearningPath()
                .getId()
                .equals(pathId)) {
            throw badRequest(
                    "Milestone does not belong to the selected path."
            );
        }

        if (pathCourseRepository
                .existsByLearningPathIdAndCourseId(
                        pathId,
                        request.courseId()
                )) {
            throw conflict(
                    "Course is already attached to this path."
            );
        }

        if (pathCourseRepository
                .existsByLearningPathIdAndSequenceNumber(
                        pathId,
                        request.sequenceNumber()
                )) {
            throw conflict(
                    "Path-course sequence number already exists."
            );
        }

        PathCourse pathCourse =
                PathCourse.builder()
                        .learningPath(path)
                        .milestone(milestone)
                        .course(course)
                        .sequenceNumber(
                                request.sequenceNumber()
                        )
                        .mandatory(request.mandatory())
                        .unlockRule(request.unlockRule())
                        .minimumScore(request.minimumScore())
                        .scheduledReleaseAt(
                                request.scheduledReleaseAt()
                        )
                        .estimatedHoursOverride(
                                request.estimatedHoursOverride()
                        )
                        .active(true)
                        .build();

        PathCourse saved =
                pathCourseRepository.save(pathCourse);

        return new LearningResponses.PathCourse(
                saved.getId(),
                milestone.getId(),
                milestone.getTitle(),
                course.getId(),
                course.getTitle(),
                saved.getSequenceNumber(),
                saved.getMandatory(),
                saved.getUnlockRule(),
                saved.getMinimumScore()
        );
    }

    @Override
    public LearningPublicationResponses.CourseStatus updateCourseStatus(
            Long courseId,
            LearningStatusRequests.CourseStatusUpdate request
    ) {
        Course course =
                courseRepository.findDetailedById(courseId)
                        .orElseThrow(() ->
                                notFound(
                                        "Course not found."
                                )
                        );

        if (request.status() == ContentStatus.PUBLISHED) {
            validateCourseForPublication(course);
        }

        course.setStatus(request.status());

        Course saved =
                courseRepository.save(course);

        return new LearningPublicationResponses.CourseStatus(
                saved.getId(),
                saved.getTitle(),
                saved.getSlug(),
                saved.getStatus(),
                saved.getActive()
        );
    }

    @Override
    public LearningResponses.PathSummary updatePathStatus(
            Long pathId,
            AdminLearningRequests.PathStatusUpdate request
    ) {
        LearningPath path =
                pathRepository.findDetailedById(pathId)
                        .orElseThrow(() ->
                                notFound(
                                        "Learning path not found."
                                )
                        );

        if (request.status() == ContentStatus.PUBLISHED) {
            publicationValidator.validateForPublication(path);
        }

        path.setStatus(request.status());

        return learningMapper.toPath(
                pathRepository.save(path)
        );
    }

    private void validateCourseForPublication(
            Course course
    ) {
        if (!Boolean.TRUE.equals(course.getActive())) {
            throw conflict(
                    "Only an active course can be published."
            );
        }

        if (course.getTitle() == null
                || course.getTitle().isBlank()) {
            throw conflict(
                    "Course title is required before publication."
            );
        }

        if (course.getDescription() == null
                || course.getDescription().isBlank()) {
            throw conflict(
                    "Course description is required before publication."
            );
        }

        validateExternalCourseConfiguration(
                course.getCourseType(),
                course.getExternalCourseUrl()
        );

        if (course.getCourseType() == CourseType.INTERNAL
                || course.getCourseType() == CourseType.HYBRID
                || course.getCourseType()
                == CourseType.PROJECT_BASED) {

            long moduleCount =
                    moduleRepository.countByCourseIdAndActiveTrue(
                            course.getId()
                    );

            long lessonCount =
                    lessonRepository
                            .countByModuleCourseIdAndActiveTrue(
                                    course.getId()
                            );

            if (moduleCount == 0) {
                throw conflict(
                        "An internal, hybrid, or project-based course requires at least one active module."
                );
            }

            if (lessonCount == 0) {
                throw conflict(
                        "An internal, hybrid, or project-based course requires at least one active lesson."
                );
            }
        }
    }

    private void validateExternalCourseConfiguration(
            CourseType courseType,
            String externalUrl
    ) {
        if (courseType == CourseType.EXTERNAL
                && (externalUrl == null
                || externalUrl.isBlank())) {
            throw badRequest(
                    "External course URL is required for an external course."
            );
        }
    }

    private ResponseStatusException notFound(
            String message
    ) {
        return new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                message
        );
    }

    private ResponseStatusException badRequest(
            String message
    ) {
        return new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                message
        );
    }

    private ResponseStatusException conflict(
            String message
    ) {
        return new ResponseStatusException(
                HttpStatus.CONFLICT,
                message
        );
    }
}
