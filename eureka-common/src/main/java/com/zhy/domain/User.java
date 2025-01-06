package com.zhy.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("user")
public class User {
    private Integer id;
    private String name;
    private String username;
    private String password;
    private String email;
    private String phone;
    @TableField("created_time")
    private LocalDateTime createTime;
    private String address;
    private Date birthday;
    private String photo;
    @TableField("role_id")
    private Integer roleID;
    private String school;
    private String college;
    private String major;
    private String grade;
    @TableField("class")
    private String classes;
}
