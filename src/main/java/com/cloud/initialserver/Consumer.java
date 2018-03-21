package com.cloud.initialserver;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
            import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class Consumer {

    //constantly listen over response queue, as soon as find the data item(message)
    private final RequestController requestController;

    @Autowired
    public Consumer(RequestController requestController) {
        this.requestController = requestController;
    }

    @JmsListener(destination = "${amazonProperties.requestQueue}")
    public void processMessage(String msg) throws IOException {
        //parse the message
        String imageaAnswer= msg.split("__")[1];
        String id = msg.split("__")[0];

        HashMap<String,String> finalMap = new HashMap<>();
        finalMap.put(id,imageaAnswer);

        /* send it to controller */
        Object finalAns = requestController.receiveResponseURL(finalMap);
    }
}
