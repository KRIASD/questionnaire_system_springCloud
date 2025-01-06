package com.zhy.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SurveyIdWIthPublisherReq {
    private Integer surveyId;
    private String publisher;
}
