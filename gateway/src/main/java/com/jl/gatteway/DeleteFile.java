package com.jl.gatteway;

import reactor.core.publisher.Mono;

import java.io.File;

public class DeleteFile {

    public void deleteDir(File dir) {
        if (dir.isDirectory()) {
            File[] children = dir.listFiles();
            //递归删除目录中的子目录下
            for (int i=0; i<children.length; i++) {
                File child = children[i];
                if (child.isDirectory()) {

                    deleteDir(child);
                } else {
                    if (child.getName().lastIndexOf(".iml") != -1) {
                        child.delete();
                    }
                }

            }
        }

        if (dir.getName().lastIndexOf(".iml") != -1) {
            dir.delete();
        }

    }
    public static void main(String[] args) {
        String aa = Mono.just("aa").block();

        DeleteFile m = new DeleteFile();
        m.deleteDir(new File("/Users/keyi/Downloads/seata-samples"));
    }

}
