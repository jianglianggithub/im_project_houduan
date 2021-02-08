package com.jl.service.impl;

import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.jl.service.FastDfsUtilService;
import com.sun.org.apache.xerces.internal.impl.dv.XSSimpleType;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


@Service
public class FastDfsUtilServiceImpl implements FastDfsUtilService, InitializingBean {

    @Autowired
    protected FastFileStorageClient storageClient;
    @Value("${fdfs.tracker-list[0]}")
    String preUrl;

    @Override
    public String upload(MultipartFile file) throws IOException {
        String s = null;
        String originalFilename = file.getOriginalFilename();
        String ext = originalFilename.split("\\.")[1];
        StorePath storePath = storageClient.uploadFile(file.getInputStream(), file.getSize(),ext, null);
        return preUrl + storePath.getFullPath();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        preUrl = "http://" + preUrl.split(":")[0] + "/";
    }
}
