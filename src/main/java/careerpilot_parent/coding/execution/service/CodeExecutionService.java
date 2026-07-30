package careerpilot_parent.coding.execution.service;

import careerpilot_parent.coding.dto.request.ExecutionRequests.ProblemRun;
import careerpilot_parent.coding.dto.request.ExecutionRequests.Run;
import careerpilot_parent.coding.dto.response.CodingResponses.Execution;

public interface CodeExecutionService {

    /**
     * Executes custom input using a problem ID supplied in the request body.
     */
    Execution run(Run request);

    /**
     * Executes custom input using a problem ID supplied in the URL path.
     */
    Execution run(
            Long problemId,
            ProblemRun request
    );
}