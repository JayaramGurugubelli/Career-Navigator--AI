package career_Navigator_parent.learning.validation;

import career_Navigator_parent.learning.entity.*;
import career_Navigator_parent.learning.enums.*;
import career_Navigator_parent.learning.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import java.util.*;

@Component
@RequiredArgsConstructor
public class LearningPublicationValidatorImpl implements LearningPublicationValidator {
    private final LearningPathMilestoneRepository milestoneRepository;
    private final PathCourseRepository pathCourseRepository;
    private final CourseModuleRepository moduleRepository;
    private final LessonRepository lessonRepository;
    private final LearningProjectRepository projectRepository;
    private final AssessmentLearningRepository assessmentLearningRepository;

    @Override
    public void validateForPublication(LearningPath path) {
        if(path.getCareerRole()==null) conflict("Career role is required.");
        if(path.getDisciplines()==null||path.getDisciplines().isEmpty())
            conflict("At least one discipline is required.");

        List<LearningPathMilestone> milestones=
                milestoneRepository.findByLearningPathIdAndActiveTrueOrderBySequenceNumberAsc(path.getId());
        if(milestones.isEmpty()) conflict("At least one active milestone is required.");
        contiguous(milestones.stream().map(LearningPathMilestone::getSequenceNumber).toList(),"milestone");

        List<PathCourse> pcs=pathCourseRepository
                .findByLearningPathIdAndActiveTrueOrderBySequenceNumberAsc(path.getId());
        if(pcs.isEmpty()) conflict("At least one course is required.");
        contiguous(pcs.stream().map(PathCourse::getSequenceNumber).toList(),"path course");

        for(PathCourse pc:pcs){
            Course c=pc.getCourse();
            if(c.getStatus()!=ContentStatus.PUBLISHED||!Boolean.TRUE.equals(c.getActive()))
                conflict("Every attached course must be published and active.");
            if((c.getCourseType()==CourseType.INTERNAL||c.getCourseType()==CourseType.HYBRID)
                    &&(moduleRepository.countByCourseIdAndActiveTrue(c.getId())==0
                    ||lessonRepository.countByModuleCourseIdAndActiveTrue(c.getId())==0))
                conflict("Internal/hybrid courses require modules and lessons.");
            if(c.getCourseType()==CourseType.EXTERNAL
                    &&(c.getExternalCourseUrl()==null||c.getExternalCourseUrl().isBlank()))
                conflict("External course URL is required.");
        }

        long projects=projectRepository.countByMilestoneLearningPathIdAndActiveTrue(path.getId());
        long assessments=milestones.stream().mapToLong(m->
                assessmentLearningRepository.findByMilestoneIdAndStatusAndActiveTrue(
                        m.getId(),AssessmentStatus.PUBLISHED).size()).sum();
        if(projects==0&&assessments==0)
            conflict("A published path requires a project or published assessment.");
    }

    private void contiguous(List<Integer> values,String label){
        Set<Integer> set=new HashSet<>(values);
        if(set.size()!=values.size()) conflict("Duplicate "+label+" sequence.");
        for(int i=1;i<=values.size();i++) if(!set.contains(i))
            conflict(label+" sequence must be contiguous from 1.");
    }
    private void conflict(String message){

        throw new ResponseStatusException(HttpStatus.CONFLICT,message);
    }
}
