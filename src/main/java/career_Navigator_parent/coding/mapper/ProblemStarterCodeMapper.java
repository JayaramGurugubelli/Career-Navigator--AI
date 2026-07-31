package career_Navigator_parent.coding.mapper;

import career_Navigator_parent.coding.dto.response.ProblemStarterCodeResponses.AdminStarterCode;
import career_Navigator_parent.coding.dto.response.ProblemStarterCodeResponses.StudentStarterCode;
import career_Navigator_parent.coding.entity.ProblemStarterCode;
import org.springframework.stereotype.Component;

@Component
public class ProblemStarterCodeMapper {

    public AdminStarterCode toAdmin(
            ProblemStarterCode entity
    ) {

        if (entity == null) {
            return null;
        }

        return new AdminStarterCode(
                entity.getId(),
                entity.getProblem().getId(),
                entity.getProgrammingLanguage(),
                entity.getStarterCode(),
                entity.getDriverCode(),
                entity.getMethodSignature(),
                entity.getActive(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public StudentStarterCode toStudent(
            ProblemStarterCode entity
    ) {

        if (entity == null) {
            return null;
        }

        return new StudentStarterCode(
                entity.getId(),
                entity.getProgrammingLanguage(),
                entity.getStarterCode(),
                entity.getMethodSignature()
        );
    }
}