package com.cloud.initialserver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.persistence.Entity;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

@RestController
@RequestMapping(value = "/cloudimagerecognition", produces = MediaType.APPLICATION_JSON_VALUE)
@EntityScan
@CrossOrigin(origins = "*")


public class RequestController {

    private final Producer producer;
    private final Consumer consumer;

    private final RequestRepository repository;

    public RequestController(Producer producer, RequestRepository repository, Consumer consumer) {
        this.producer = producer;
        this.repository = repository;
        this.consumer = consumer;
    }


    @Autowired
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public void receiveUserURL(@RequestParam(value = "input") String imageUrl) throws IOException {


        Request requestAns = new Request();         //model
        requestAns.setUrl(imageUrl);
        requestAns = repository.save(requestAns);      //save url,id to database

        String id = requestAns.getId();
        String sqsUrl = requestAns.getUrl();        //retrive url and id

        String sqsMessage = id + "__" + sqsUrl;
        producer.sendMessages(sqsMessage);          //send to producer

//        return imageUrl;
    }

    public Object receiveResponseURL(HashMap fmap) throws IOException {
        Request requestAns = new Request();

        while (!fmap.containsKey(requestAns.getId())) {
            //continuous loop

        }
        //return to user

        return fmap.get(requestAns.getId());
    }
}

//call consumer