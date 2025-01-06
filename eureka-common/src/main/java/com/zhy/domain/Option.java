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
@TableName("option_brief")
public class Option {
    private Integer id;
    @TableField("question_id")
    private Integer questionID;
    @TableField("option_content_id")
    private Integer optionContentID;
    private LocalDateTime deleteAt;
    @TableField(exist = false)
    private OptionContent optionContent;
}
