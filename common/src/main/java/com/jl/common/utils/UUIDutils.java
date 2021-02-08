package com.jl.common.utils;

import java.util.UUID;

public class UUIDutils {

    public static String getUUid() {
        return UUID.randomUUID().toString().replaceAll("-","");
    }
}
