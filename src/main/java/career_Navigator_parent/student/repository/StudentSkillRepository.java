package career_Navigator_parent.student.repository;

import career_Navigator_parent.student.entity.StudentSkill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudentSkillRepository extends JpaRepository<StudentSkill, Long> {

    List<StudentSkill> findByStudentId(Long studentId);

    Optional<StudentSkill> findByIdAndStudentId(Long skillId, Long studentId);

    boolean existsByStudentIdAndSkillNameIgnoreCase(Long studentId, String skillName);

}