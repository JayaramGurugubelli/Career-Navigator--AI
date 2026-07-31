package career_Navigator_parent.student.repository;

import career_Navigator_parent.student.entity.StudentEducation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudentEducationRepository extends JpaRepository<StudentEducation, Long> {

    List<StudentEducation> findByStudentId(Long studentId);

    Optional<StudentEducation> findByIdAndStudentId(Long id, Long studentId);

}