package com.zhy.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("submit_survey_record")
public class SubmitSurveyRecord {
    private Integer id;
    @TableField("user_id")
    private Integer userID;
    @TableField("survey_id")
    private Integer surveyID;
    private LocalDateTime submitTime;
    private String publisher;
    private String positiveProb;
    private String negativeProb;
    private String sentiment;
}
