package career_Navigator_parent.student.repository;

import career_Navigator_parent.student.entity.StudentProject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudentProjectRepository extends JpaRepository<StudentProject, Long> {

    List<StudentProject> findByStudentIdOrderByStartDateDesc(Long studentId);

    Optional<StudentProject> findByIdAndStudentId(Long projectId, Long studentId);

    boolean existsByStudentIdAndProjectTitleIgnoreCase(Long studentId, String projectTitle);

}