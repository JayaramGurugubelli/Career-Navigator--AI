package career_Navigator_parent.student.repository;


import career_Navigator_parent.student.entity.StudentCertification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface StudentCertificationRepository extends JpaRepository<StudentCertification, Long> {


    List<StudentCertification> findByStudentId(Long studentId);
    Optional<StudentCertification> findByIdAndStudentId(Long certificationId, Long studentId);
}