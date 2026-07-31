package career_Navigator_parent.student.repository;

import career_Navigator_parent.student.entity.StudentLanguage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudentLanguageRepository
        extends JpaRepository<StudentLanguage, Long> {


    List<StudentLanguage> findByStudentId(Long studentId);


    List<StudentLanguage> findByStudentIdOrderByLanguageNameAsc(Long studentId);


    Optional<StudentLanguage> findByIdAndStudentId(
            Long id,
            Long studentId
    );


    boolean existsByStudentIdAndLanguageNameIgnoreCase(
            Long studentId,
            String languageName
    );

}