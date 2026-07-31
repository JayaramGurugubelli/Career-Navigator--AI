package career_Navigator_parent.coding.execution.service;

import career_Navigator_parent.coding.dto.request.ExecutionRequests.ProblemRun;
import career_Navigator_parent.coding.dto.request.ExecutionRequests.Run;
import career_Navigator_parent.coding.dto.response.CodingResponses.Execution;

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