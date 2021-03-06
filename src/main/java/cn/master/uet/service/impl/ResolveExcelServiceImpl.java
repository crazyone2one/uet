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
import cn.master.uet.util.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

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
            log.error("文件内容为空");
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
        if (Objects.isNull(workbook)) {
            log.error("获取excel文件失败");
            return caseEntityList;
        } else {
            Sheet sheet = workbook.getSheetAt(0);
            int lastRowNum = sheet.getLastRowNum();
            // 第一行为表头信息，跳过
            for (int i = 1; i <= lastRowNum; i++) {
                Row row = sheet.getRow(i);
                CaseEntity caseEntity = caseEntity(row);
                // 过滤空白行
                if (CommonUtils.checkObjectField(caseEntity)) {
                    continue;
                }
                caseEntityList.add(caseEntity);
            }
        }
        return caseEntityList;
    }

    private CaseEntity caseEntity(Row row) {
        CaseEntity caseEntity = new CaseEntity();
        caseEntity.setProjectName(row.getCell(0).getStringCellValue());
        caseEntity.setSuiteName(row.getCell(1).getStringCellValue());
        caseEntity.setCaseTitle(row.getCell(2).getStringCellValue());
        if (Objects.nonNull(row.getCell(3))) {
            caseEntity.setSummery(row.getCell(3).getStringCellValue());
        } else {
            caseEntity.setSummery("");
        }
        if (Objects.nonNull(row.getCell(4))) {
            caseEntity.setPreconditions(row.getCell(4).getStringCellValue());
        } else {
            caseEntity.setPreconditions("");
        }
        caseEntity.setCaseDetail(row.getCell(5).getStringCellValue());
        caseEntity.setExpectResult(row.getCell(6).getStringCellValue());
        caseEntity.setImported(row.getCell(7).getBooleanCellValue());
        log.info("解析到一条数据:========================\n" + caseEntity);
        return caseEntity;
    }

    @Override
    public String uploadCases(List<CaseEntity> caseEntityList) {
        int imported = 0;
        int imported1 = 0;
        int failed = 0;
        if (CollectionUtils.isEmpty(caseEntityList)) {
            return "未解析到数据";
        }
        for (CaseEntity entity : caseEntityList) {
            if (Objects.equals(true, entity.getImported())) {
                imported += 1;
                continue;
            }
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
                log.warn("测试步骤和期望结果数量不匹配");
                failed += 1;
                continue;
            }
            Integer projectId = checkProject(entity.getProjectName());
            // create case
            try {
                log.info("开始存储到数据库");
                apiConfig.api().createTestCase(entity.getCaseTitle(),
                        checkSuite(projectId, entity.getSuiteName()),
                        projectId,
                        "admin",
                        entity.getSummery(),
                        steps,
                        entity.getPreconditions(),
                        TestCaseStatus.DRAFT,
                        TestImportance.MEDIUM, ExecutionType.MANUAL,
                        10, null, null, null
                );
                imported1 += 1;
            } catch (TestLinkAPIException exception) {
                failed += 1;
                log.error("测试用例创建失败:" + exception.getMessage());
                continue;
            }
        }
        return "共解析到" + caseEntityList.size() + "条测试用例: " + imported + "条已导入.本次成功导入" + imported1 + "条,导入失败" + failed + "条";
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
        return apiConfig.api().createTestProject(projectName, CommonUtils.randomCode(4), null, false,
                true, true, false, true, true).getId();
    }

    /**
     * 检查suite
     *
     * @param projectId 项目id
     * @param suiteName suite name
     * @return java.lang.String
     */
    private Integer checkSuite(Integer projectId, String suiteName) {
        Integer suiteId = null;
        try {
            TestSuite[] first = apiConfig.api().getFirstLevelTestSuitesForTestProject(projectId);
            for (TestSuite testSuite : first) {
                if (testSuite.getName().equals(suiteName)) {
                    suiteId = testSuite.getId();
                    break;
                }
            }
            if (Objects.isNull(suiteId)) {
                TestSuite testSuite1 = apiConfig.api().createTestSuite(projectId, suiteName, "", null, null, true, ActionOnDuplicate.BLOCK);
                suiteId = testSuite1.getId();
            }
        } catch (TestLinkAPIException e) {
            e.printStackTrace(System.err);
            TestSuite testSuite1 = apiConfig.api().createTestSuite(projectId, suiteName, "", null, null, true, ActionOnDuplicate.BLOCK);
            suiteId = testSuite1.getId();
        }
        return suiteId;
    }
}
