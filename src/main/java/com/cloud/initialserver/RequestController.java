package com.cloud.initialserver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Hashtable;
import java.util.Map;

@RestController
@RequestMapping(value = "/cloudimagerecognition", produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin(origins = "*")
public class RequestController {

    private final AmazonClient amazonClient;

    private final Producer producer;

    private static Hashtable<String, String> cache = new Hashtable<>();

    private static Hashtable<String, Integer> imageFrequencyMap = new Hashtable<>();

    private static final Integer CACHE_CAPACITY = 20;

    @Autowired
    public RequestController(AmazonClient amazonClient, Producer producer) {
        this.amazonClient = amazonClient;
        this.producer = producer;
    }

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public String receiveUserURL(@RequestParam(value = "input") String imageUrl) {
        if (imageUrl == null) {
            return "Image URL is required";
        }
        String[] imageUrlArr = imageUrl.split("/");
        String imageName = imageUrlArr[imageUrlArr.length-1];
        if (cache.containsKey(imageName)) {
            String identifiedImage = cache.get(imageName);
            amazonClient.uploadFileTos3bucket(imageName, identifiedImage);
            return identifiedImage;
        }
        String id = String.valueOf(generateNumber());
        String sqsMessage = id + "__" + imageUrl;
        producer.sendMessages(sqsMessage);

        while (true) {
            if(Consumer.finalTable.containsKey(id)) {
                String result = Consumer.finalTable.get(id);
                Consumer.finalTable.remove(id);
                putToCache(imageName, result);
                return result;
            }
        }
    }

    private long generateNumber() {
        return (long)(Math.random()*100000 + 3333300000L);
    }

    private void putToCache(String imageName, String identifiedImage) {
        if (cache.size() > CACHE_CAPACITY) {
            String leastUsedKey = getLeastUsedKey();
            if (leastUsedKey != null) {
                cache.remove(leastUsedKey);
                imageFrequencyMap.remove(leastUsedKey);
            } else {
                Map.Entry<String, String> entry = cache.entrySet().iterator().next();
                cache.remove(entry.getKey());
                imageFrequencyMap.remove(entry.getKey());
            }
        }
        putToMap(imageName, identifiedImage);
    }

    private void putToMap(String imageName, String identifiedImage) {
        cache.put(imageName, identifiedImage);
        if (imageFrequencyMap.contains(imageName)) {
            imageFrequencyMap.put(imageName, imageFrequencyMap.get(imageName) + 1);
        } else {
            imageFrequencyMap.put(imageName, 1);
        }
    }

    private String getLeastUsedKey() {
        for (String key: imageFrequencyMap.keySet()) {
            if (imageFrequencyMap.get(key).equals(Collections.min(imageFrequencyMap.values()))) {
                return key;
            }
        }
        return null;
    }
}