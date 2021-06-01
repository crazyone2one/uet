package cn.master.uet.service.impl;

import br.eti.kinoshita.testlinkjavaapi.constants.ActionOnDuplicate;
import br.eti.kinoshita.testlinkjavaapi.constants.ExecutionType;
import br.eti.kinoshita.testlinkjavaapi.constants.TestCaseStatus;
import br.eti.kinoshita.testlinkjavaapi.constants.TestImportance;
import br.eti.kinoshita.testlinkjavaapi.model.TestCaseStep;
import br.eti.kinoshita.testlinkjavaapi.model.TestProject;
import br.eti.kinoshita.testlinkjavaapi.model.TestSuite;
import br.eti.kinoshita.testlinkjavaapi.util.TestLinkAPIException;
import cn.master.uet.commom.TestLinkApiConfig;
import cn.master.uet.entity.CaseEntity;
import cn.master.uet.service.ResolveExcelService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * @author by 11's papa on 2021年05月28日
 * @version 1.0.0
 */
@Slf4j
@Service
public class ResolveExcelServiceImpl implements ResolveExcelService {

    private static final String SUFFIX_2003 = ".xls";
    private static final String SUFFIX_2007 = ".xlsx";

    @Autowired
    private TestLinkApiConfig apiConfig;

    @Override
    public List<CaseEntity> resolveExcel(MultipartFile file) {
        List<CaseEntity> caseEntityList = new LinkedList<>();
        if (file.isEmpty()) {
            log.error("");
            return caseEntityList;
        }
        // 获取文件的名字
        String filename = file.getOriginalFilename();
        Workbook workbook = null;
        try {
            assert filename != null;
            if (filename.endsWith(SUFFIX_2003)) {
                workbook = new HSSFWorkbook(file.getInputStream());
            } else if (filename.endsWith(SUFFIX_2007)) {
                workbook = new XSSFWorkbook(file.getInputStream());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (workbook == null) {
            log.error("");
            return caseEntityList;
        } else {
            Sheet sheet = workbook.getSheetAt(0);
            int lastRowNum = sheet.getLastRowNum();
            for (int i = 1; i <= lastRowNum; i++) {
                Row row = sheet.getRow(i);
                CaseEntity caseEntity = caseEntity(row);
                caseEntityList.add(caseEntity);
            }
        }
        return caseEntityList;
    }

    private CaseEntity caseEntity(Row row) {
        CaseEntity caseEntity = new CaseEntity();
        if (row.getCell(0) != null) {
            caseEntity.setProjectName(row.getCell(0).getStringCellValue());
        }
        if (row.getCell(1) != null) {
            caseEntity.setTestPlan(row.getCell(1).getStringCellValue());
        } else {
            caseEntity.setTestPlan("");
        }
        if (row.getCell(2) != null) {
            caseEntity.setVersionName(row.getCell(2).getStringCellValue());
        } else {
            caseEntity.setVersionName("");
        }
        if (row.getCell(3) != null) {
            caseEntity.setSuiteName(row.getCell(3).getStringCellValue());
        }
        if (row.getCell(4) != null) {
            caseEntity.setFunctionDesc(row.getCell(4).getStringCellValue());
        }
        if (row.getCell(5) != null) {
            caseEntity.setCaseNo(row.getCell(5).getStringCellValue());
        }
        if (row.getCell(6) != null) {
            caseEntity.setCaseTitle(row.getCell(6).getStringCellValue());
        }
        if (row.getCell(7) != null) {
            caseEntity.setSummery(row.getCell(7).getStringCellValue());
        }
        if (row.getCell(8) != null) {
            caseEntity.setPreconditions(row.getCell(8).getStringCellValue());
        }
        if (row.getCell(9) != null) {
            caseEntity.setCaseDetail(row.getCell(9).getStringCellValue());
        }
        if (row.getCell(10) != null) {
            caseEntity.setExpectResult(row.getCell(10).getStringCellValue());
        }
        if (row.getCell(11) != null) {
            caseEntity.setPriority(String.valueOf((int) row.getCell(11).getNumericCellValue()));
        }
        if (row.getCell(12) != null) {
            caseEntity.setExecutionType(String.valueOf((int) row.getCell(12).getNumericCellValue()));
        }
        if (row.getCell(13) != null) {
            caseEntity.setDesigner(row.getCell(13).getStringCellValue());
        }
        if (row.getCell(14) != null) {
            caseEntity.setExecutor(row.getCell(14).getStringCellValue());
        }
        log.info("解析到一条数据:========================\n" + caseEntity);
        return caseEntity;
    }

    @Override
    public String uploadCases(List<CaseEntity> caseEntityList) {
        if (CollectionUtils.isEmpty(caseEntityList)) {
            return "未解析到数据";
        }
        for (CaseEntity entity : caseEntityList) {
            List<TestCaseStep> steps = new ArrayList<>();
            String[] caseSteps = entity.getCaseDetail().split("\\r?\\n");
            String[] caseExcept = entity.getExpectResult().split("\\r?\\n");
            if (caseSteps.length == caseExcept.length) {
                for (int i = 0; i < caseSteps.length; i++) {
                    TestCaseStep step = new TestCaseStep();
                    step.setNumber(i + 1);
                    step.setActions(caseSteps[i]);
                    step.setExpectedResults(caseExcept[i]);
                    step.setExecutionType(ExecutionType.MANUAL);
                    steps.add(step);
                }
            } else {
                log.warn("");
                continue;
            }

            Integer projectId = checkProject(entity.getProjectName());
            String summery = entity.getSummery();
            if (StringUtils.isBlank(summery)) {
                summery = entity.getFunctionDesc();
            }
            String suiteId = checkSuite(projectId, entity.getSuiteName());
            // create case
            log.info("开始存储到数据库");
            apiConfig.api().createTestCase(entity.getCaseTitle(),
                    Integer.valueOf(suiteId),
                    projectId,
                    entity.getDesigner(),
                    summery,
                    steps,
                    null,
                    TestCaseStatus.DRAFT,
                    TestImportance.MEDIUM, ExecutionType.MANUAL,
                    10, null, null, null
            );
        }
        return "上传成功";
    }

    /**
     * 验证project
     *
     * @param projectName 项目名称
     * @return java.lang.Integer
     */

    private Integer checkProject(String projectName) {
        TestProject project;
        try {
            project = apiConfig.api().getTestProjectByName(projectName);
            if (project.isActive()) {
                return project.getId();
            }
        } catch (TestLinkAPIException exception) {
            exception.printStackTrace();
        }
        return apiConfig.api().createTestProject(projectName, "tc1", null, false,
                true, true, false, true, true).getId();
    }

    /**
     * 检查suite
     *
     * @param projectId 项目id
     * @param suiteName suite name
     * @return java.lang.String
     */
    private String checkSuite(Integer projectId, String suiteName) {
        String suiteId = null;
        try {
            TestSuite[] first = apiConfig.api().getFirstLevelTestSuitesForTestProject(projectId);
            if (CollectionUtils.isNotEmpty(Arrays.asList(first))) {
                for (TestSuite testSuite : first) {
                    if (testSuite.getName().equals(suiteName)) {
                        suiteId = testSuite.getId().toString();
                        break;
                    } else {
                        TestSuite ts = apiConfig.api().createTestSuite(projectId, suiteName, "", null, null, true, ActionOnDuplicate.BLOCK);
                        suiteId = ts.getId().toString();
                    }
                }
            }
        } catch (TestLinkAPIException e) {
            e.printStackTrace(System.err);
            TestSuite testSuite1 = apiConfig.api().createTestSuite(projectId, suiteName, "", null, null, true, ActionOnDuplicate.BLOCK);
            suiteId = testSuite1.getId().toString();
        }
        return suiteId;
    }
}
