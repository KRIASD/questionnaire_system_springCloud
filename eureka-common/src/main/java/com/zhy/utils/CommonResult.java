package com.zhy.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class CommonResult<T> {
    private Integer code;
    private String message;
    private T data;

    public CommonResult(Integer code, String message) {
        this(code, message, null);
    }

    public static CommonResult<?> success() {
        return new CommonResult<>(200, "操作成功");
    }

    public static <T> CommonResult<T> success(T data) {
        return new CommonResult<>(200, "操作成功", data);
    }

    public static CommonResult<?> failed() {
        return new CommonResult<>(500, "操作失败");
    }

    public static CommonResult<?> failed(String message) {
        return new CommonResult<>(500, message);
    }
}
