package com.greco.twitter.twitterbot.bus;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
@Slf4j
public class S3Uploader {

    @Value("${aws.s3accesskey}")
    private String s3accesskey;
    @Value("${aws.s3secretkey}")
    private String s3secretkey;
    @Value("${aws.s3bucketname}")
    private String bucketname;
    @Value("${aws.s3bucketkey}")
    private String bucketkey;


    public void uploadFile(String fullfilename,String filename){

        AWSCredentials credentials = new BasicAWSCredentials(s3accesskey,s3secretkey);

        AmazonS3 s3client = AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(Regions.US_EAST_1)
                .build();
        log.info("Uploading " + filename + " to S3");
        s3client.putObject(
                bucketname,
                bucketkey + "/" + filename,
                new File(fullfilename)
        );
        log.info("Upload of " + filename + " complete");

    }
}
