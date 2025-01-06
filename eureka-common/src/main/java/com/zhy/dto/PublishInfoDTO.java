package com.zhy.dto;

import com.zhy.req.PublishConditionReq;
import lombok.Data;

import java.util.List;

@Data
public class PublishInfoDTO {
    private String publisher;
    private List<PublishConditionReq> receiver;
}
