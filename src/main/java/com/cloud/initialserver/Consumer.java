package com.cloud.initialserver;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class Consumer {

    public static Map<String, String> finalMap = new HashMap<>();

    @JmsListener(destination = "${amazonProperties.responseQueue}")
    public void processMessage(String msg) throws IOException {
        //parse the message
        String imageaAnswer= msg.split("__")[1];
        String id = msg.split("__")[0];

        finalMap.put(id,imageaAnswer);
    }
}
