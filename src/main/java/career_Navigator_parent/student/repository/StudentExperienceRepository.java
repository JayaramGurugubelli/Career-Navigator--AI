package career_Navigator_parent.student.repository;

import career_Navigator_parent.student.entity.StudentExperience;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudentExperienceRepository extends JpaRepository<StudentExperience, Long> {

    List<StudentExperience> findByStudentId(Long studentId);

    Optional<StudentExperience> findByIdAndStudentId(Long experienceId, Long studentId);

}