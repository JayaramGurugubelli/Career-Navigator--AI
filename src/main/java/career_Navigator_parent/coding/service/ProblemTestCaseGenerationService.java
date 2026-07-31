package career_Navigator_parent.coding.service;

import career_Navigator_parent.coding.dto.request.ProblemTestCaseRequests.Generate;
import career_Navigator_parent.coding.dto.response.ProblemTestCaseResponses.GenerationAccepted;
import career_Navigator_parent.coding.dto.response.ProblemTestCaseResponses.GenerationJob;

public interface ProblemTestCaseGenerationService {

    /**
     * Validates the generation request, creates a persistent generation
     * job, and queues asynchronous test-case generation.
     */
    GenerationAccepted startGeneration(
            Long problemId,
            Generate request
    );

    /**
     * Returns the generation-job status only when the job belongs to
     * the supplied coding problem.
     */
    GenerationJob getJob(
            Long problemId,
            Long jobId
    );
}