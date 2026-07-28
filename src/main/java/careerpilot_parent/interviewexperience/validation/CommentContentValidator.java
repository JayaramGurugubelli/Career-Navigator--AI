package careerpilot_parent.interviewexperience.validation;

import careerpilot_parent.common.exception.BadRequestException;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class CommentContentValidator {

    private static final int MAX_COMMENT_LENGTH = 1000;

    /*
     * Detects normal email formats:
     *
     * user@gmail.com
     * first.last@company.co.in
     */
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile(
                    "(?i)(?<![A-Za-z0-9._%+-])" +
                            "[A-Za-z0-9._%+-]+" +
                            "@" +
                            "[A-Za-z0-9.-]+" +
                            "\\.[A-Za-z]{2,}" +
                            "(?![A-Za-z0-9._%+-])"
            );

    /*
     * Detects commonly disguised emails:
     *
     * user at gmail dot com
     * user [at] gmail [dot] com
     * user(at)gmail(dot)com
     */
    private static final Pattern OBFUSCATED_EMAIL_PATTERN =
            Pattern.compile(
                    "(?i)\\b" +
                            "[A-Za-z0-9._%+-]{1,64}" +
                            "\\s*(?:@|\\[at]|\\(at\\)|\\sat\\s)" +
                            "\\s*[A-Za-z0-9.-]{1,100}" +
                            "\\s*(?:\\.|\\[dot]|\\(dot\\)|\\sdot\\s)" +
                            "\\s*[A-Za-z]{2,15}" +
                            "\\b"
            );

    /*
     * Finds possible telephone-number sequences.
     *
     * The final digit-count check prevents ordinary small numbers such as:
     *
     * Java 21
     * 90 minutes
     * 25 questions
     */
    private static final Pattern PHONE_CANDIDATE_PATTERN =
            Pattern.compile(
                    "(?<!\\d)" +
                            "(?:\\+\\s*)?" +
                            "(?:\\d[\\s().-]*){10,15}" +
                            "(?!\\d)"
            );

    /*
     * Helps identify shorter contact numbers when the user explicitly writes
     * words such as phone, mobile, WhatsApp or contact.
     */
    private static final Pattern CONTACT_KEYWORD_PATTERN =
            Pattern.compile(
                    "(?i)\\b(" +
                            "phone|" +
                            "mobile|" +
                            "contact|" +
                            "call|" +
                            "whatsapp|" +
                            "telegram|" +
                            "number|" +
                            "mob" +
                            ")\\b"
            );

    /*
     * Detects a number near an explicit contact keyword.
     */
    private static final Pattern CONTACT_NUMBER_PATTERN =
            Pattern.compile(
                    "(?i)\\b(" +
                            "phone|" +
                            "mobile|" +
                            "contact|" +
                            "call|" +
                            "whatsapp|" +
                            "telegram|" +
                            "number|" +
                            "mob" +
                            ")\\b" +
                            "[^\\d+]{0,20}" +
                            "(\\+?[\\d\\s().-]{7,25})"
            );

    public String validateAndNormalize(
            String content
    ) {

        if (content == null || content.isBlank()) {
            throw new BadRequestException(
                    "Comment cannot be empty."
            );
        }

        String normalizedContent =
                normalizeWhitespace(content);

        if (normalizedContent.length() > MAX_COMMENT_LENGTH) {
            throw new BadRequestException(
                    "Comment cannot exceed "
                            + MAX_COMMENT_LENGTH
                            + " characters."
            );
        }

        if (containsEmailAddress(normalizedContent)) {
            throw new BadRequestException(
                    "Email addresses are not allowed in comments."
            );
        }

        if (containsPhoneNumber(normalizedContent)) {
            throw new BadRequestException(
                    "Phone numbers are not allowed in comments."
            );
        }

        return normalizedContent;
    }

    public void validate(
            String content
    ) {

        validateAndNormalize(content);
    }

    public boolean containsEmailAddress(
            String content
    ) {

        if (content == null || content.isBlank()) {
            return false;
        }

        return EMAIL_PATTERN.matcher(content).find()
                || OBFUSCATED_EMAIL_PATTERN
                .matcher(content)
                .find();
    }

    public boolean containsPhoneNumber(
            String content
    ) {

        if (content == null || content.isBlank()) {
            return false;
        }

        Matcher candidateMatcher =
                PHONE_CANDIDATE_PATTERN.matcher(content);

        while (candidateMatcher.find()) {

            String candidate =
                    candidateMatcher.group();

            int digitCount =
                    countDigits(candidate);

            /*
             * Indian mobile numbers normally contain ten digits.
             * International telephone numbers can contain up to 15 digits.
             */
            if (digitCount >= 10 && digitCount <= 15) {
                return true;
            }
        }

        /*
         * Detects explicitly labelled contact numbers that might be shorter,
         * such as local telephone numbers.
         */
        if (CONTACT_KEYWORD_PATTERN.matcher(content).find()) {

            Matcher contactMatcher =
                    CONTACT_NUMBER_PATTERN.matcher(content);

            while (contactMatcher.find()) {

                String candidate =
                        contactMatcher.group(2);

                int digitCount =
                        countDigits(candidate);

                if (digitCount >= 7 && digitCount <= 15) {
                    return true;
                }
            }
        }

        return false;
    }

    private String normalizeWhitespace(
            String content
    ) {

        return content
                .strip()
                .replaceAll("[\\p{Z}\\s]+", " ");
    }

    private int countDigits(
            String value
    ) {

        int count = 0;

        for (int index = 0;
             index < value.length();
             index++) {

            if (Character.isDigit(
                    value.charAt(index)
            )) {
                count++;
            }
        }

        return count;
    }
}