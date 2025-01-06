package com.zhy.dto;

import com.zhy.domain.Survey;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SurveyWithPublishDTO {
    private Survey survey;
    private String publisher;
}
