package com.zhy.req;

import com.zhy.domain.Comment;
import com.zhy.vo.AnswerWithQuestionAndOptionContent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubmitDetailReq {
    private Integer surveyId;
    private List<AnswerWithQuestionAndOptionContent> answerWithQuestionAndOptionContents;
    private Comment comment;
    private String username;
    private String publisher;
}
