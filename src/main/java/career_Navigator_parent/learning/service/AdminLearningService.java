package career_Navigator_parent.learning.service;

import career_Navigator_parent.learning.dto.request.AdminLearningRequests;
import career_Navigator_parent.learning.dto.request.LearningStatusRequests;
import career_Navigator_parent.learning.dto.response.LearningPublicationResponses;
import career_Navigator_parent.learning.dto.response.LearningResponses;

public interface AdminLearningService {

    LearningResponses.Discipline createDiscipline(
            AdminLearningRequests.DisciplineCreate request
    );

    LearningResponses.Domain createDomain(
            AdminLearningRequests.DomainCreate request
    );

    LearningResponses.Role createRole(
            AdminLearningRequests.RoleCreate request
    );

    LearningResponses.PathSummary createPath(
            AdminLearningRequests.PathCreate request
    );

    LearningResponses.Milestone addMilestone(
            Long pathId,
            AdminLearningRequests.MilestoneCreate request
    );

    LearningResponses.CourseSummary createCourse(
            AdminLearningRequests.CourseCreate request
    );

    LearningResponses.Module addModule(
            Long courseId,
            AdminLearningRequests.ModuleCreate request
    );

    LearningResponses.Lesson addLesson(
            Long moduleId,
            AdminLearningRequests.LessonCreate request
    );

    LearningResponses.PathCourse attachCourse(
            Long pathId,
            AdminLearningRequests.PathCourseCreate request
    );

    LearningPublicationResponses.CourseStatus updateCourseStatus(
            Long courseId,
            LearningStatusRequests.CourseStatusUpdate request
    );

    LearningResponses.PathSummary updatePathStatus(
            Long pathId,
            AdminLearningRequests.PathStatusUpdate request
    );
}
