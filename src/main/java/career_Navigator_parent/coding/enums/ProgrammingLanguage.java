package career_Navigator_parent.coding.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProgrammingLanguage {

    JAVA(
            "Java",
            "java",
            62,
            "java",
            "15.0.2",
            "Main.java"
    ),

    PYTHON(
            "Python",
            "python",
            71,
            "python",
            "3.12.0",
            "main.py"
    ),

    C(
            "C",
            "c",
            50,
            "c",
            "10.2.0",
            "main.c"
    ),

    CPP(
            "C++",
            "cpp",
            54,
            "c++",
            "10.2.0",
            "main.cpp"
    ),

    JAVASCRIPT(
            "JavaScript",
            "javascript",
            63,
            "javascript",
            "20.11.1",
            "main.js"
    ),

    TYPESCRIPT(
            "TypeScript",
            "typescript",
            74,
            "typescript",
            "5.0.3",
            "main.ts"
    ),

    CSHARP(
            "C#",
            "csharp",
            51,
            "csharp",
            "6.12.0",
            "Main.cs"
    ),

    GO(
            "Go",
            "go",
            60,
            "go",
            "1.16.2",
            "main.go"
    ),

    KOTLIN(
            "Kotlin",
            "kotlin",
            78,
            "kotlin",
            "1.8.20",
            "Main.kt"
    ),

    RUST(
            "Rust",
            "rust",
            73,
            "rust",
            "1.68.2",
            "main.rs"
    );

    private final String displayName;

    private final String editorLanguage;

    /**
     * Kept temporarily for backward compatibility.
     * Remove after every Judge0 reference has been migrated.
     */
    private final Integer judge0LanguageId;

    private final String pistonLanguage;

    private final String pistonVersion;

    private final String pistonSourceFileName;

    public boolean isPistonConfigured() {
        return pistonLanguage != null
                && !pistonLanguage.isBlank()
                && pistonVersion != null
                && !pistonVersion.isBlank()
                && pistonSourceFileName != null
                && !pistonSourceFileName.isBlank();
    }
}