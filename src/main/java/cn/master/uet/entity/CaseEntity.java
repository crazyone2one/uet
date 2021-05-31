package cn.master.uet.entity;

import lombok.Data;

/**
 * @author by 11's papa on 2021年05月28日
 * @version 1.0.0
 */
@Data
public class CaseEntity {
    private String projectName;
    private String testPlan;
    private String versionName;
    private String suiteName;
    private String functionDesc;
    private String caseNo;
    private String caseTitle;
    private String summery;
    private String preconditions;
    private String caseDetail;
    private String expectResult;
    private String priority;
    private String executionType;
    private String designer;
    private String executor;
}
