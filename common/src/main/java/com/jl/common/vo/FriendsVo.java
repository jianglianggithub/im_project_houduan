package com.jl.common.vo;


import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

@Data
public class FriendsVo {

    // 排序后的 好友列表 通过首文字字母排序
    private Map<String, List<MailListVo>> result;

    // 拥有的好友的 首字母集合 并且排序
    private Set<String> initials;
}
