package career_Navigator_parent.coding.service;

import career_Navigator_parent.coding.dto.request.ProblemTestCaseRequests;
import career_Navigator_parent.coding.dto.request.ProblemTestCaseRequests.BatchCreate;
import career_Navigator_parent.coding.dto.request.ProblemTestCaseRequests.BulkDelete;
import career_Navigator_parent.coding.dto.request.ProblemTestCaseRequests.Create;
import career_Navigator_parent.coding.dto.request.ProblemTestCaseRequests.Import;
import career_Navigator_parent.coding.dto.request.ProblemTestCaseRequests.Update;
import career_Navigator_parent.coding.dto.response.ProblemTestCaseResponses;
import career_Navigator_parent.coding.dto.response.ProblemTestCaseResponses.AdminTestCase;
import career_Navigator_parent.coding.dto.response.ProblemTestCaseResponses.BatchResult;
import career_Navigator_parent.coding.dto.response.ProblemTestCaseResponses.DeleteResult;
import career_Navigator_parent.coding.dto.response.ProblemTestCaseResponses.ImportResult;
import career_Navigator_parent.coding.dto.response.ProblemTestCaseResponses.Summary;
import career_Navigator_parent.coding.enums.TestCaseVisibility;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProblemTestCaseService {

    AdminTestCase create(
            Long problemId,
            Create request
    );

    BatchResult createBatch(
            Long problemId,
            BatchCreate request
    );
    public ProblemTestCaseResponses.BulkResult createBulk(
            Long problemId,
            ProblemTestCaseRequests.BulkCreate request
    );
    ImportResult importTestCases(
            Long problemId,
            Import request
    );

    AdminTestCase update(
            Long problemId,
            Long testCaseId,
            Update request
    );

    AdminTestCase get(
            Long problemId,
            Long testCaseId,
            Boolean includeInactive
    );

    Page<AdminTestCase> list(
            Long problemId,
            TestCaseVisibility visibility,
            Boolean includeInactive,
            Pageable pageable
    );

    Summary summary(
            Long problemId
    );

    void delete(
            Long problemId,
            Long testCaseId
    );

    DeleteResult deleteBulk(
            Long problemId,
            BulkDelete request
    );

    AdminTestCase restore(
            Long problemId,
            Long testCaseId
    );
}