package com.greco.twitter.twitterbot.bus;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.vault.annotation.VaultPropertySource;
import twitter4j.*;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;

import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.net.URL;

@Slf4j
@Component
public class TwitterReader {

    @Value("${twitter.twitteraccesssecret}")
    private String twitteraccesssecret;
    @Value("${twitter.twitteraccesstoken}")
    private String twitteraccesstoken;
    @Value("${twitter.twitterconsumerkey}")
    private String twitterconsumerkey;
    @Value("${twitter.twitterconsumersecret}")
    private String twitterconsumersecret;
    @Value("${twitterbot.mediastorelocation}")
    private String mediastorelocation;
    @Value("${twitterbot.userlong}")
    private String[] userlong;

    @Autowired
    S3Uploader s3Uploader;

    public void doTwitter() throws Exception{



        TwitterStream twitterStream = new TwitterStreamFactory(new ConfigurationBuilder().setJSONStoreEnabled(true).build()).getInstance();

        twitterStream.setOAuthConsumer(twitterconsumerkey, twitterconsumersecret);
        AccessToken token = new AccessToken(twitteraccesstoken, twitteraccesssecret);
        twitterStream.setOAuthAccessToken(token);

        StatusListener listener = new StatusListener() {

            private String getExtension(String type) {
                if (type.equals("photo")) {
                    return "jpg";
                } else if (type.equals("video")) {
                    return "mp4";
                } else if (type.equals("animated_gif")) {
                    return "gif";
                } else {
                    return "err";
                }
            }

            @Override
            public void onException(Exception e) {
                log.error("EXCEPTION: " + ExceptionUtils.getStackTrace(e));
            }

            public void onStatus(Status status) {
                if(!status.isRetweet()){
                    log.info("STATUS: " + status.getText());
                    MediaEntity[] media = status.getMediaEntities(); //get the media entities from the status
                    for(MediaEntity m : media){ //search trough your entities
                        log.info("MEDIA URL: " + m.getMediaURL()); //get your url!
                        try {
                            URL url = new URL(m.getMediaURL());
                            InputStream in = new BufferedInputStream(url.openStream());
                            ByteArrayOutputStream out = new ByteArrayOutputStream();
                            byte[] buf = new byte[1024];
                            int n = 0;
                            while (-1 != (n = in.read(buf))) {
                                out.write(buf, 0, n);
                            }
                            out.close();
                            in.close();
                            byte[] response = out.toByteArray();
                            File file = new File(mediastorelocation);
                            String filename = m.getId() + "." + getExtension(m.getType());
                            String fullfilename = file.getAbsolutePath() + "/" + filename;
                            log.info("Writing file: " + fullfilename );
                            FileOutputStream fos = new FileOutputStream(fullfilename);
                            fos.write(response);
                            fos.close();
                            s3Uploader.uploadFile(fullfilename,filename);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }

            }

            @Override
            public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
                log.info("DELETION DETECTED: " + statusDeletionNotice.toString());
            }

            @Override
            public void onTrackLimitationNotice(int i) {
                log.info("TRACK LIMITATION DETECTED: " + i);
            }

            @Override
            public void onScrubGeo(long l, long l1) {
                log.info("SCRUB GEO DETECTED: " + l1);
            }

            @Override
            public void onStallWarning(StallWarning stallWarning) {
                log.info("STALL WARNInG DETECTED: " + stallWarning.toString());
            }


        };

        twitterStream.addListener(listener);
        FilterQuery query = new FilterQuery();
        query.follow(getLongValues(userlong));
        String keywords[] = {"grecoderbyday"};
        query.track(keywords);
        twitterStream.filter(query);
    }

    private long[] getLongValues(String[] userlong) {
        long[] data = new long[userlong.length];
        for (int i = 0; i < userlong.length; i++) {
            data[i] = Long.parseLong(userlong[i]);
        }
        return data;
    }
}
