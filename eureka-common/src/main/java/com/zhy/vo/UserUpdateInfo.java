package com.zhy.vo;

import com.zhy.domain.User;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
public class UserUpdateInfo {
    /**
     * 用户信息
     */
    private User user;
    /**
     * 头像
     */
    private MultipartFile avatar;
}
