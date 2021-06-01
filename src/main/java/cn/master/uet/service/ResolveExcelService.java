package cn.master.uet.service;

import cn.master.uet.entity.CaseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author by 11's papa on 2021年05月28日
 * @version 1.0.0
 */
public interface ResolveExcelService {
    /**
     * resolve excel data
     *
     * @param file excel file
     * @return java.util.List<cn.master.uet.entity.CaseEntity>
     */
    List<CaseEntity> resolveExcel(MultipartFile file);

    /**
     * 上传case
     *
     * @param caseEntityList
     * @return java.lang.String
     */

    String uploadCases(List<CaseEntity> caseEntityList);
}
