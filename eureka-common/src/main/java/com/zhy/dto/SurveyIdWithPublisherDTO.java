package com.zhy.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SurveyIdWithPublisherDTO {
    private Integer surveyId;
    private String publisher;
}
