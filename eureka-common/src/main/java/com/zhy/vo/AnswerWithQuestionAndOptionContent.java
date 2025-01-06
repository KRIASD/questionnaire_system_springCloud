package com.zhy.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnswerWithQuestionAndOptionContent {
    private String questionContent;
    private String questionType;
    private List<OptionContentWithAnswer> optionContentWithAnswers;
}
