package com.cloud.initialserver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/cloudimagerecognition", produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin(origins = "*")
public class RequestController {

    private final Producer producer;

    @Autowired
    public RequestController(Producer producer) {
        this.producer = producer;
    }


    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public String receiveUserURL(@RequestParam(value = "input") String imageUrl) {
        if (imageUrl == null) {
            return "Image URL is required";
        }
        String id = String.valueOf(generateNumber());
        String sqsMessage = id + "__" + imageUrl;
        producer.sendMessages(sqsMessage);

        while (true) {
            if(Consumer.finalTable.containsKey(id)) {
                String result = Consumer.finalTable.get(id);
                Consumer.finalTable.remove(id);
                return result;
            }
        }
    }

    private long generateNumber() {
        return (long)(Math.random()*100000 + 3333300000L);
    }
}