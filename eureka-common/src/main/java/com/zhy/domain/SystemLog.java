package com.zhy.domain;

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
@TableName("system_log")
public class SystemLog {
    private Integer id;
    private String actionPeople;
    private String actionName;
    private String actionDetail;
    private LocalDateTime actionTime;
}
