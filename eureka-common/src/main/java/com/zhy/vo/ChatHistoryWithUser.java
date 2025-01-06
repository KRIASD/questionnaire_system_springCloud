package com.zhy.vo;

import com.zhy.domain.Group;
import com.zhy.domain.Message;
import com.zhy.domain.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatHistoryWithUser {
    private User user;
    private List<Message> messages;
    private Group group;
}
