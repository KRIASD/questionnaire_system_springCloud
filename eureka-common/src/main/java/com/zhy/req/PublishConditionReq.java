package com.zhy.req;

import lombok.Data;

import java.util.Objects;

@Data
public class PublishConditionReq {
    private String school;
    private String college;
    private String major;
    private String grade;
    private String classes;
    /**
     * 1:已发布 0:未发布
     */
    private Integer publishStatus;
    private String publishDate;
}
