package com.zhy.vo;

import com.zhy.domain.Comment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoWithComment {
    private String username;
    private Comment comment;
    private String avatar;
    /**
     * 获取评论分数
     *
     * @return 评论分数
     */
    public Float getCommentScore() {
        return comment.getScore();
    }

    /**
     * 获取评论时间
     *
     * @return 评论时间
     */
    public LocalDateTime getCommentTime() {
        return comment.getSubmitTime();
    }
}
