package career_Navigator_parent.student.dto.request;

import career_Navigator_parent.student.enums.LanguageProficiency;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateStudentLanguageRequest {

    @NotBlank(message = "Language name is required.")
    @Size(max = 50, message = "Language name must not exceed 50 characters.")
    private String languageName;

    @NotNull(message = "Language proficiency is required.")
    private LanguageProficiency proficiency;

}