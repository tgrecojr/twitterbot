# TWITTERBOT

This is a simple twitterbot written in Java.  It simply listens for posts on certain <configurable> users and downloads any of the media that they post.  My intention is to then have a batch job that bulk uploads this media on a periodic basis to an S3 bucket where it can be used for other purposes.

My original intent for this appliction it to enable web sharing of pictures that are captured and posed at our yearly Kentucky Derby Party.  Sicne my iPad Photo Booth (SnapBooth) automatically forwards to @GrecoDerbyDay, I will use this to create a web gallery at https://grecoderbyday.com

### Tech

Twitterbot makes use of:

* Spring Boote
* Lombok
* Twitter4J
* Amazon S3

