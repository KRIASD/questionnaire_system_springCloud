package com.zhy.resp;

import com.zhy.domain.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserWithTokenResp {
    private User user;
    private String token;
}
