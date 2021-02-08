package com.jl.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FastDfsUtilService {

    String upload(MultipartFile file) throws IOException;
}
