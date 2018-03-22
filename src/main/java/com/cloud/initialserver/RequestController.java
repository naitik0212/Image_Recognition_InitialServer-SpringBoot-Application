package com.cloud.initialserver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping(value = "/cloudimagerecognition", produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin(origins = "*")
public class RequestController {

    private final Producer producer;

    private final RequestRepository repository;

    @Autowired
    public RequestController(Producer producer, RequestRepository repository) {
        this.producer = producer;
        this.repository = repository;
    }


    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public String receiveUserURL(@RequestParam(value = "input") String imageUrl) throws IOException {
        Request requestAns = new Request();         //model

        requestAns.setUrl(imageUrl);
        requestAns = repository.save(requestAns);      //save url,id to database
        String id = String.valueOf(requestAns.getId());
        String sqsUrl = requestAns.getUrl();        // get url and id

        String sqsMessage = id + "__" + sqsUrl;
        producer.sendMessages(sqsMessage);          //send to producer

        while (true) {
            if(Consumer.finalMap.containsKey(id)) {
                requestAns.setIdentifiedImage(Consumer.finalMap.get(id));
                repository.save(requestAns);
                return Consumer.finalMap.get(id);
            }
        }
    }
}