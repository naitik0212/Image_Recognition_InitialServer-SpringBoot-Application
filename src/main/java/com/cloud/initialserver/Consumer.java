package com.cloud.initialserver;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Hashtable;

@Component
public class Consumer {

    public static Hashtable<String, String> finalTable = new Hashtable<>();

    @JmsListener(destination = "${amazonProperties.responseQueue}")
    public void processMessage(String msg) throws IOException {
        //parse the message
        String imageAnswer= msg.split("__")[1];
        String id = msg.split("__")[0];

        finalTable.put(id,imageAnswer);
    }
}
