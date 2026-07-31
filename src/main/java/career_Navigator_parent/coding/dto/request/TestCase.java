package career_Navigator_parent.coding.dto.request;

import career_Navigator_parent.coding.enums.TestCaseVisibility;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestCase {

    @NotBlank(message = "Test case input is required")
    private String input;

    @NotBlank(message = "Expected output is required")
    private String expectedOutput;

    @NotNull(message = "Test case visibility is required")
    private TestCaseVisibility visibility;

    @NotNull(message = "Display order is required")
    @Min(value = 1, message = "Display order must be at least 1")
    private Integer displayOrder;

    @NotNull(message = "Score weight is required")
    @Positive(message = "Score weight must be greater than zero")
    private Integer scoreWeight;

    @Positive(message = "Custom time limit must be greater than zero")
    private Double customTimeLimitSeconds;

    @Positive(message = "Custom memory limit must be greater than zero")
    private Integer customMemoryLimitMegabytes;
}