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
@TableName("comment")
public class Comment {
    private Integer id;
    @TableField("user_id")
    private Integer userID;
    @TableField("survey_id")
    private Integer surveyID;
    private Float score;
    private LocalDateTime submitTime;
    private String content;
}
