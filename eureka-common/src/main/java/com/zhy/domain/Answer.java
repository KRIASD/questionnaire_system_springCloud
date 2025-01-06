package com.zhy.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("answer")
public class Answer {
    private Integer id;
    @TableField("user_id")
    private Integer userID;
    @TableField("survey_id")
    private Integer surveyID;
    @TableField("question_id")
    private Integer questionID;
    private String content;
}
