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
@TableName("access_log")
public class AccessLog {
    @TableField("user_id")
    private Integer userID;
    private LocalDateTime accessTime;
    public AccessLog(Integer userID) {
        this.userID = userID;
        this.accessTime = LocalDateTime.now();
    }
}
