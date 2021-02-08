package com.jl;


import com.github.tobato.fastdfs.domain.fdfs.MetaData;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.domain.fdfs.ThumbImageConfig;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = HttpServerApplication.class)
public class Testaa {
    @Autowired
    protected FastFileStorageClient storageClient;

    @Autowired
    private ThumbImageConfig thumbImageConfig;

    @Test
    public void test() throws IOException {


        FileInputStream fileInputStream = new FileInputStream(new File("D:\\a.txt"));
        // Metadata
        Set<MetaData> metaDataSet = createMetaData();
        // 上传文件和Metadata
        StorePath path = storageClient.uploadFile(fileInputStream, fileInputStream.available(), "txt",
                metaDataSet);


        Set<MetaData> fetchMetaData = storageClient.getMetadata(path.getGroup(), path.getPath());
        System.out.println(path);
    }
    private Set<MetaData> createMetaData() {
        Set<MetaData> metaDataSet = new HashSet<>();
        metaDataSet.add(new MetaData("Author", "tobato"));
        metaDataSet.add(new MetaData("CreateDate", "2016-01-05"));
        return metaDataSet;
    }



}
