package com.xiyuan.apicore.model.dto.file;

import lombok.Data;

import java.io.Serializable;

/**
 * 文件上传请求
 */
@Data
public class FileUploadRequest implements Serializable {
    /**
     * 业务
     */
    private String business;

    private static final long serialVersionUID = 1L;

}
