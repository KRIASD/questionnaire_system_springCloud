package com.zhy.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("question")
public class Question {
    private Integer id;
    @TableField("survey_id")
    private Integer surveyID;
    private String type;
    private String content;
    private LocalDateTime deleteAt;
    @TableField(exist = false)
    private List<Option> options;
}
