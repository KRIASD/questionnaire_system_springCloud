package com.zhy.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class Message {
    private Integer id;
    private Integer senderId;
    private Integer receiverId;
    private Integer groupId;
    private String content;
    private LocalDateTime sendTime;
}
