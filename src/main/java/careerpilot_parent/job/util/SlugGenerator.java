package careerpilot_parent.job.util;

import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.util.Locale;
import java.util.UUID;

@Component
public class SlugGenerator {

    public String generate(String value) {

        if (value == null || value.isBlank()) {
            return UUID.randomUUID().toString();
        }

        String normalized =
                Normalizer.normalize(
                        value,
                        Normalizer.Form.NFD
                );

        String slug =
                normalized
                        .replaceAll("\\p{M}", "")
                        .toLowerCase(Locale.ENGLISH)
                        .replaceAll("[^a-z0-9]+", "-")
                        .replaceAll("^-|-$", "");

        return slug + "-" +
                UUID.randomUUID()
                        .toString()
                        .substring(0, 8);
    }
}