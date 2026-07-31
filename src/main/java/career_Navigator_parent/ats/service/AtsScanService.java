package career_Navigator_parent.ats.service;


import career_Navigator_parent.ats.dto.request.CreateAtsScanRequest;
import career_Navigator_parent.ats.dto.response.AtsScanResponse;


import java.util.List;


public interface AtsScanService {


    AtsScanResponse createScan(CreateAtsScanRequest request);


    List<AtsScanResponse> getMyScans();


    AtsScanResponse getScanById(
            Long scanId
    );
    void deleteScan(Long scanId);

}