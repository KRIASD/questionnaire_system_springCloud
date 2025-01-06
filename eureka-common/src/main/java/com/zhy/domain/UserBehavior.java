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
@TableName("user_behavior")
public class UserBehavior {
    private Integer id;
    @TableField("user_id")
    private Integer userID;
    @TableField("survey_id")
    private Integer surveyID;
    private String action;
    private LocalDateTime actionTime;
    @TableField(exist = false)
    private Double rating;
}
