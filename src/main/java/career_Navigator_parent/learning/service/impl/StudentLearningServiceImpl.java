package career_Navigator_parent.learning.service.impl;

import career_Navigator_parent.learning.dto.request.StudentLearningRequests;
import career_Navigator_parent.learning.dto.response.LearningResponses;
import career_Navigator_parent.learning.entity.*;
import career_Navigator_parent.learning.enums.*;
import career_Navigator_parent.learning.event.LearningProgressEvent;
import career_Navigator_parent.learning.mapper.LearningMapper;
import career_Navigator_parent.learning.repository.*;
import career_Navigator_parent.learning.service.LearningContentAccessService;
import career_Navigator_parent.learning.service.StudentLearningService;
import career_Navigator_parent.security.util.SecurityUtils;
import career_Navigator_parent.student.entity.Student;
import career_Navigator_parent.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class StudentLearningServiceImpl implements StudentLearningService {
    private final LearningPathRepository paths;
    private final PathCourseRepository pathCourses;
    private final LessonRepository lessons;
    private final StudentLearningPathEnrollmentRepository enrollments;
    private final StudentCourseProgressRepository courseProgress;
    private final StudentLessonProgressRepository lessonProgress;
    private final StudentWeeklyLearningGoalRepository goals;
    private final StudentRepository students;
    private final SecurityUtils security;
    private final LearningMapper mapper;
    private final LearningContentAccessService contentAccessService;
    private final ApplicationEventPublisher events;

    @Override public LearningResponses.Enrollment enroll(Long pathId){
        Student s=student();
        LearningPath p=paths.findDetailedById(pathId).filter(x->x.getStatus()==ContentStatus.PUBLISHED
                &&Boolean.TRUE.equals(x.getActive())).orElseThrow(()->nf("Published path not found."));
        if(enrollments.existsByStudentIdAndLearningPathId(s.getId(),pathId))
            throw new ResponseStatusException(HttpStatus.CONFLICT,"Already enrolled.");
        List<PathCourse> pcs=pathCourses.findByLearningPathIdAndActiveTrueOrderBySequenceNumberAsc(pathId);
        LearningPathMilestone current=p.getMilestones().stream().filter(m->Boolean.TRUE.equals(m.getActive()))
                .min(Comparator.comparing(LearningPathMilestone::getSequenceNumber)).orElse(null);
        StudentLearningPathEnrollment e=enrollments.save(StudentLearningPathEnrollment.builder()
                .student(s).learningPath(p).currentMilestone(current).status(EnrollmentStatus.ENROLLED)
                .progressPercentage(0.0).completedCourses(0).totalCourses(pcs.size())
                .enrolledAt(LocalDateTime.now()).lastAccessedAt(LocalDateTime.now()).build());
        publish(s.getId(),"PATH_ENROLLED",pathId,"LEARNING_PATH",Map.of("enrollmentId",e.getId()));
        return mapper.toEnrollment(e);
    }

    @Override @Transactional(readOnly=true)
    public Page<LearningResponses.Enrollment> enrollments(Pageable p){
        Student s=student();
        return enrollments.findByStudentIdOrderByLastAccessedAtDesc(s.getId(),p).map(mapper::toEnrollment);
    }

    @Override public LearningResponses.Lesson startLesson(Long id){
        return updateLessonProgress(id,new StudentLearningRequests.LessonProgress(0,0,0));
    }

    @Override public LearningResponses.Lesson updateLessonProgress(Long id,StudentLearningRequests.LessonProgress r){
        Student s=student();
        Lesson l=lessons.findById(id).filter(x->Boolean.TRUE.equals(x.getActive()))
                .orElseThrow(()->nf("Lesson not found."));
        contentAccessService.validateLessonAccess(s,l);
        StudentLessonProgress p=lessonProgress.findByStudentIdAndLessonId(s.getId(),id)
                .orElseGet(()->StudentLessonProgress.builder().student(s).lesson(l)
                        .course(l.getModule().getCourse()).status(ProgressStatus.NOT_STARTED)
                        .progressPercentage(0.0).timeSpentSeconds(0L).lastPositionSeconds(0L)
                        .completionCount(0).build());
        p.recordProgress(r.progressPercentage(),r.lastPositionSeconds(),r.additionalTimeSeconds());
        p=lessonProgress.save(p);
        refresh(s,l.getModule().getCourse());
        updateGoal(s,(int)(r.additionalTimeSeconds()/60),0);
        publish(s.getId(),"LESSON_PROGRESS_UPDATED",id,"LESSON",Map.of("progress",p.getProgressPercentage()));
        return mapper.toLesson(l,p);
    }

    @Override public LearningResponses.Lesson completeLesson(Long id){
        Student s=student();
        Lesson l=lessons.findById(id).orElseThrow(()->nf("Lesson not found."));
        contentAccessService.validateLessonAccess(s,l);
        StudentLessonProgress p=lessonProgress.findByStudentIdAndLessonId(s.getId(),id)
                .orElseGet(()->StudentLessonProgress.builder().student(s).lesson(l)
                        .course(l.getModule().getCourse()).status(ProgressStatus.NOT_STARTED)
                        .progressPercentage(0.0).timeSpentSeconds(0L).lastPositionSeconds(0L)
                        .completionCount(0).build());
        if(p.getStatus()!=ProgressStatus.COMPLETED){p.complete();updateGoal(s,0,1);}
        p=lessonProgress.save(p);
        refresh(s,l.getModule().getCourse());
        publish(s.getId(),"LESSON_COMPLETED",id,"LESSON",Map.of());
        return mapper.toLesson(l,p);
    }

    @Override public LearningResponses.WeeklyGoal setWeeklyGoal(StudentLearningRequests.WeeklyGoal r){
        Student s=student(); LocalDate ws=weekStart();
        StudentWeeklyLearningGoal g=goals.findByStudentIdAndWeekStartDate(s.getId(),ws)
                .orElseGet(()->StudentWeeklyLearningGoal.builder().student(s).weekStartDate(ws)
                        .weekEndDate(ws.plusDays(6)).completedMinutes(0).completedLessons(0)
                        .status(LearningGoalStatus.ACTIVE).build());
        g.setTargetMinutes(r.targetMinutes());g.setTargetLessons(r.targetLessons());g.evaluateStatus();
        g=goals.save(g);
        return new LearningResponses.WeeklyGoal(g.getId(),g.getWeekStartDate(),g.getWeekEndDate(),
                g.getTargetMinutes(),g.getCompletedMinutes(),g.getTargetLessons(),g.getCompletedLessons(),g.getStatus());
    }

    private void refresh(Student s,Course c){
        int total=Math.toIntExact(lessons.countByModuleCourseIdAndActiveTrue(c.getId()));
        int done=Math.toIntExact(lessonProgress.countByStudentIdAndCourseIdAndStatus(
                s.getId(),c.getId(),ProgressStatus.COMPLETED));
        StudentCourseProgress cp=courseProgress.findByStudentIdAndCourseId(s.getId(),c.getId())
                .orElseGet(()->StudentCourseProgress.builder().student(s).course(c).status(ProgressStatus.NOT_STARTED)
                        .completedLessons(0).totalLessons(total).progressPercentage(0.0)
                        .totalTimeSpentSeconds(0L).build());
        ProgressStatus old=cp.getStatus(); cp.updateLessonProgress(done,total); courseProgress.save(cp);
        if(old!=ProgressStatus.COMPLETED&&cp.getStatus()==ProgressStatus.COMPLETED)
            publish(s.getId(),"COURSE_COMPLETED",c.getId(),"COURSE",Map.of());
        List<StudentLearningPathEnrollment> es=new ArrayList<>(enrollments
                .findByStudentIdAndStatus(s.getId(),EnrollmentStatus.IN_PROGRESS));
        es.addAll(enrollments.findByStudentIdAndStatus(s.getId(),EnrollmentStatus.ENROLLED));
        for(StudentLearningPathEnrollment e:es){
            List<PathCourse> pcs=pathCourses.findByLearningPathIdAndActiveTrueOrderBySequenceNumberAsc(
                    e.getLearningPath().getId());
            if(pcs.stream().noneMatch(x->x.getCourse().getId().equals(c.getId()))) continue;
            int complete=(int)pcs.stream().filter(x->courseProgress.findByStudentIdAndCourseId(
                    s.getId(),x.getCourse().getId()).map(y->y.getStatus()==ProgressStatus.COMPLETED).orElse(false)).count();
            e.updateProgress(complete,pcs.size()); enrollments.save(e);
            if(e.getStatus()==EnrollmentStatus.COMPLETED)
                publish(s.getId(),"PATH_COMPLETED",e.getLearningPath().getId(),"LEARNING_PATH",Map.of());
        }
    }

    private void updateGoal(Student s,int mins,int ls){
        goals.findByStudentIdAndWeekStartDate(s.getId(),weekStart()).ifPresent(g->{g.recordLearning(mins,ls);goals.save(g);});
    }
    private LocalDate weekStart(){return LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));}
    private Student student(){Long u=security.getCurrentUserId();return students.findByUserId(u)
            .orElseThrow(()->nf("Student profile not found."));}
    private void publish(Long sid,String t,Long id,String target,Map<String,Object> p){
        events.publishEvent(new LearningProgressEvent(sid,t,id,target,p,LocalDateTime.now()));
    }
    private ResponseStatusException nf(String m){return new ResponseStatusException(HttpStatus.NOT_FOUND,m);}
}