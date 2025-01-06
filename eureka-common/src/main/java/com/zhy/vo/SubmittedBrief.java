package com.zhy.vo;

import com.zhy.domain.SubmitSurveyRecord;
import com.zhy.domain.Survey;
import com.zhy.domain.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubmittedBrief {
    private Survey survey;
    private SubmitSurveyRecord submitSurveyRecord;
    private User user;
}
