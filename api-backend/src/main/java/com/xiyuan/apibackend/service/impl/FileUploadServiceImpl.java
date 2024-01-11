package com.xiyuan.apibackend.service.impl;

import com.xiyuan.apibackend.common.ErrorCode;
import com.xiyuan.apibackend.constant.FileConstant;
import com.xiyuan.apibackend.exception.BusinessException;
import com.xiyuan.apibackend.service.FileUploadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
@Slf4j
public class FileUploadServiceImpl implements FileUploadService {
    @Override
    public void UploadFileToLocal(String filePath,File file) {
        String savePath = FileConstant.LOCAL_FILE_PAth +filePath;
        File tempFile = null;
        try {
            tempFile = new File(savePath);
            File parentDir = tempFile.getParentFile();
//            如果父文件夹不存在，则创建
            if (!parentDir.exists()) {
                if (!parentDir.mkdirs()) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR,"文件夹创建失败");
                }
            }
            Files.copy(Paths.get(file.toURI()),Paths.get(tempFile.toURI()));
        } catch (Exception e) {
            log.error("file upload error, filepath = " + savePath, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"文件上传失败");
        }
    }
}
