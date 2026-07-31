package careerpilot_parent.coding.dto.response;

public record ReviewHelpfulResponse(

        Long reviewId,
        Long problemId,
        boolean helpful,
        long helpfulCount

) {
}