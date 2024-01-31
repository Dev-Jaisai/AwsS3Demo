package com.s3demo.AWSS3.dto;

import jdk.jfr.Timestamp;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResponseMetaData {
    private String fielName;
    private String fileSize;
    private String fileCreated;
    private String msg;
}
