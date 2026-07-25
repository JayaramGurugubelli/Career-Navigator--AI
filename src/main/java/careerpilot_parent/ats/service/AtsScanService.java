package careerpilot_parent.ats.service;


import careerpilot_parent.ats.dto.request.CreateAtsScanRequest;
import careerpilot_parent.ats.dto.response.AtsScanResponse;


import java.util.List;


public interface AtsScanService {


    AtsScanResponse createScan(CreateAtsScanRequest request);


    List<AtsScanResponse> getMyScans();


    AtsScanResponse getScanById(
            Long scanId
    );
    void deleteScan(Long scanId);

}