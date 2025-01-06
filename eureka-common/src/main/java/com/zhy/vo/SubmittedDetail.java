package com.zhy.vo;

import com.zhy.domain.Survey;
import com.zhy.domain.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubmittedDetail {
    private Survey survey;
    private String submitTime;
    private User user;
    private List<AnswerWithQuestionAndOptionContent> answerWithQuestionAndOptionContents;
    private String positiveProb;
    private String negativeProb;
    private String sentiment;
}
