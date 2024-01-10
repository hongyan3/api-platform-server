package com.xiyuan.project.service;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
public interface FileUploadService {
    void UploadFileToLocal(String FilePath,File file) throws IOException;
}
