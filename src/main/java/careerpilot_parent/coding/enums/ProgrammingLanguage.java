package careerpilot_parent.coding.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProgrammingLanguage {

    JAVA("Java", "java", 62),
    PYTHON("Python", "python", 71),
    C("C", "c", 50),
    CPP("C++", "cpp", 54),
    JAVASCRIPT("JavaScript", "javascript", 63),
    TYPESCRIPT("TypeScript", "typescript", 74),
    CSHARP("C#", "csharp", 51),
    GO("Go", "go", 60),
    KOTLIN("Kotlin", "kotlin", 78),
    RUST("Rust", "rust", 73);

    private final String displayName;
    private final String editorLanguage;
    private final Integer judge0LanguageId;
}