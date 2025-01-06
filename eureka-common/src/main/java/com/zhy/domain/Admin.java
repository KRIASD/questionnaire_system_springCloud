package com.zhy.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("admin")
public class Admin {
    private Integer id;
    private String userName;
    private String passWord;
    private String department;
    private String photo;
    @TableField("role_id")
    private Integer roleID;

}
