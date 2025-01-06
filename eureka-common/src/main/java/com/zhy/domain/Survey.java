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
@TableName("survey")
public class Survey {
    private Integer id;
    private String name;
    private String description;
    private LocalDateTime createTime;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String type;
    private Integer status;
    private LocalDateTime deleteAt;
    @TableField(exist = false)
    private List<Question> questions;
    @TableField(exist = false)
    private Integer submitCount;
}
