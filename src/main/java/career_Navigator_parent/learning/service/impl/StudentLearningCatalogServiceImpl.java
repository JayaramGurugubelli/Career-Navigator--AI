package career_Navigator_parent.learning.service.impl;

import career_Navigator_parent.learning.dto.response.LearningResponses;
import career_Navigator_parent.learning.entity.*;
import career_Navigator_parent.learning.enums.ContentStatus;
import career_Navigator_parent.learning.mapper.LearningMapper;
import career_Navigator_parent.learning.repository.*;
import career_Navigator_parent.learning.service.StudentLearningCatalogService;
import career_Navigator_parent.security.util.SecurityUtils;
import career_Navigator_parent.student.entity.Student;
import career_Navigator_parent.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly=true)
public class StudentLearningCatalogServiceImpl implements StudentLearningCatalogService {
    private final AcademicDisciplineRepository disciplines;
    private final CareerRoleRepository roles;
    private final LearningPathRepository paths;
    private final LearningPathMilestoneRepository milestones;
    private final PathCourseRepository pathCourses;
    private final CourseRepository courses;
    private final CourseModuleRepository modules;
    private final LessonRepository lessons;
    private final StudentLearningPathEnrollmentRepository enrollments;
    private final StudentLessonProgressRepository lessonProgress;
    private final StudentRepository students;
    private final SecurityUtils security;
    private final LearningMapper mapper;

    @Override public List<LearningResponses.Discipline> disciplines(){
        return disciplines.findByActiveTrueOrderByDisplayOrderAscNameAsc().stream().map(mapper::toDiscipline).toList();
    }
    @Override public Page<LearningResponses.Role> careerRoles(Long disciplineId,Pageable p){
        return (disciplineId==null?roles.findByActiveTrue(p):roles.findPublishedForDiscipline(disciplineId,p))
                .map(mapper::toRole);
    }
    @Override public LearningResponses.Role careerRole(Long id){
        return mapper.toRole(roles.findDetailedById(id).filter(r->Boolean.TRUE.equals(r.getActive()))
                .orElseThrow(()->nf("Career role not found.")));
    }
    @Override public Page<LearningResponses.PathSummary> paths(Long disciplineId,Long roleId,Pageable p){
        Page<LearningPath> page=disciplineId!=null?paths.findForDiscipline(disciplineId,ContentStatus.PUBLISHED,p):
                roleId!=null?paths.findByCareerRoleIdAndStatusAndActiveTrue(roleId,ContentStatus.PUBLISHED,p):
                        paths.findByStatusAndActiveTrue(ContentStatus.PUBLISHED,p);
        return page.map(mapper::toPath);
    }
    @Override public LearningResponses.PathDetail path(Long id){
        Student s=student();
        LearningPath path=paths.findDetailedById(id).filter(x->x.getStatus()==ContentStatus.PUBLISHED
                &&Boolean.TRUE.equals(x.getActive())).orElseThrow(()->nf("Published path not found."));
        List<LearningResponses.Milestone> ms=milestones
                .findByLearningPathIdAndActiveTrueOrderBySequenceNumberAsc(id).stream()
                .map(m->new LearningResponses.Milestone(m.getId(),m.getTitle(),m.getDescription(),
                        m.getMilestoneType(),m.getSequenceNumber(),m.getMandatory(),m.getEstimatedHours())).toList();
        List<LearningResponses.PathCourse> cs=pathCourses
                .findByLearningPathIdAndActiveTrueOrderBySequenceNumberAsc(id).stream()
                .map(pc->new LearningResponses.PathCourse(pc.getId(),pc.getMilestone().getId(),
                        pc.getMilestone().getTitle(),pc.getCourse().getId(),pc.getCourse().getTitle(),
                        pc.getSequenceNumber(),pc.getMandatory(),pc.getUnlockRule(),pc.getMinimumScore())).toList();
        StudentLearningPathEnrollment e=enrollments.findByStudentIdAndLearningPathId(s.getId(),id).orElse(null);
        return new LearningResponses.PathDetail(mapper.toPath(path),ms,cs,e!=null,e==null?0.0:e.getProgressPercentage());
    }
    @Override public LearningResponses.CourseDetail course(Long id){
        Student s=student();
        Course c=courses.findDetailedById(id).filter(x->x.getStatus()==ContentStatus.PUBLISHED
                &&Boolean.TRUE.equals(x.getActive())).orElseThrow(()->nf("Published course not found."));
        List<LearningResponses.Module> ms=modules.findByCourseIdAndActiveTrueOrderBySequenceNumberAsc(id).stream()
                .map(m->new LearningResponses.Module(m.getId(),m.getTitle(),m.getDescription(),
                        m.getSequenceNumber(),m.getEstimatedMinutes(),m.getMandatory(),
                        lessons.findByModuleIdAndActiveTrueOrderBySequenceNumberAsc(m.getId()).stream()
                                .map(l->mapper.toLesson(l,lessonProgress.findByStudentIdAndLessonId(
                                        s.getId(),l.getId()).orElse(null))).toList())).toList();
        return new LearningResponses.CourseDetail(mapper.toCourse(c),ms);
    }
    private Student student(){Long u=security.getCurrentUserId();return students.findByUserId(u)
            .orElseThrow(()->nf("Student profile not found."));}
    private ResponseStatusException nf(String m){return new ResponseStatusException(HttpStatus.NOT_FOUND,m);}
}
