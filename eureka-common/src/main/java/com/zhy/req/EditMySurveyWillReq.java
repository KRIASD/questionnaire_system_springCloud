package com.zhy.req;

import com.zhy.domain.Survey;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EditMySurveyWillReq {
    private String username;
    private Survey survey;
}
