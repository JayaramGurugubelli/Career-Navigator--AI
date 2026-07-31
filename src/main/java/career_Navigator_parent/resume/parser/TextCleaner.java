package career_Navigator_parent.resume.parser;

import org.springframework.stereotype.Component;

@Component
public class TextCleaner {

    public String clean(String text) {

        if (text == null) {
            return "";
        }

        return text
                .replaceAll("\\r", "")
                .replaceAll("[ \\t]+", " ")
                .replaceAll("\\n{2,}", "\n")
                .trim();
    }

}