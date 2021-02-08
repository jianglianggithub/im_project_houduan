package com.jl.boot;

import com.jl.common.utils.UUIDutils;
import com.jl.netty.manager.StateManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;

import org.springframework.cloud.stream.config.BinderProperties;
import org.springframework.cloud.stream.config.BindingProperties;

import org.springframework.cloud.stream.config.BindingServiceProperties;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 *  为了使得 BindingServiceProperties 配置的groupId 不重复 出此下策 这个方法并不优雅。但是暂时先这么做
 */
@Component
@Slf4j
public class BottProcessor implements BeanPostProcessor{

    @Autowired
    StateManager stateManager;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof BindingServiceProperties) {
            BindingServiceProperties c = (BindingServiceProperties) bean;
            Map<String, BindingProperties> bindings = c.getBindings();
            // 对于output 来说 是哪个组发送的消息不重要  重要的是消费者组
            BindingProperties myInput = bindings.get("myInput");

            try {
                String localUUID = getLocalUUID();
                stateManager.localUUID = localUUID;
                // 手动改变 yml 中 设置的 组id
                myInput.setGroup(localUUID);
            } catch (IOException e) {
                e.printStackTrace();
                log.error("启动设置uuid失败");
                System.exit(1);
            }
            /**
             *   为什么要用一个文件存储 GroupId 的原因就是。
             *   1 某台nettyNode 宕机 重启的时候  uuid 变了  ip 变了 那么那么那些消息 永远都不会被再次消费了。
             *   2 如果ip 没有变化 那么groupID 变化了。 那么 消息极大可能 重复消费。

             *   当前解决方案
             *      1. 主机 纪录一个uuid文件
             *      2. 在发送消息的时候 不发送消息本身 发送消息本身的ID 然后 consumer 拿到消息id
             *         后查询数据库 查询是否发送到给了用户
             *
             */

        }
        return bean;
    }


    public static String getLocalUUID() throws IOException {
        String userHome = System.getProperty("user.home");
        String dir = userHome + File.separator + "nett_node";
        String filePath = dir + File.separator + "uuid";
        File dir_ = new File(dir);
        File file = new File(filePath);

        if (file.exists()) {
            List<String> lines = IOUtils.readLines(new FileReader(file));
            log.info("本地文件有缓存 恢复之前的group id");
            return lines.get(0);
        }else {
            dir_.mkdirs();
            String uUid = UUIDutils.getUUid();
            BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
            writer.write(uUid);
            writer.close();
            log.info("启动创建新uuid文件成功 path = {} uuid = {}",filePath,uUid);
            return uUid;
        }


    }


}
