package com.zhy.req;

import lombok.Data;

import java.util.List;

@Data
public class PublishConditionListReq {
    List<PublishConditionReq> publishConditionReq;
    private Integer surveyId;
    private String publisher;
}
