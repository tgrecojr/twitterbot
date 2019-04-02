package com.greco.twitter.twitterbot;

import com.greco.twitter.twitterbot.bus.TwitterReader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages={"com.greco.twitter.*"})
@Slf4j
public class TwitterbotApplication implements CommandLineRunner {


    @Autowired
    private TwitterReader twitterReader;

    public static void main(String[] args) {

        SpringApplication.run(TwitterbotApplication.class, args);

    }

    @Override
    public void run(String[] args) throws Exception {


        long startTime = System.currentTimeMillis();
        log.info("STARTING APPLICATION");
        twitterReader.doTwitter();
        long totalTime = System.currentTimeMillis() - startTime;
        log.info("Finished Processing Twitter Streams " + totalTime/1000 + " seconds");

    }

}
