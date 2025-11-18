/*
 Licensed to the Apache Software Foundation (ASF) under one
 or more contributor license agreements.  See the NOTICE file
 distributed with this work for additional information
 regarding copyright ownership.  The ASF licenses this file
 to you under the Apache License, Version 2.0 (the
 "License"); you may not use this file except in compliance
 with the License.  You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing,
 software distributed under the License is distributed on an
 "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 either express or implied.  See the License for the specific
 language governing permissions and limitations
 under the License.
 */

import java.net.*;
import java.io.*;
import java.nio.channels.*;
import java.util.Properties;

public class MavenWrapperDownloader {

    private static final String WRAPPER_PROPERTIES_PATH = ".mvn/wrapper/maven-wrapper.properties";
    private static final String DEFAULT_DOWNLOAD_URL = "https://repo.maven.apache.org/maven2/";
    private static final String MAVEN_WRAPPER_JAR_PATH = ".mvn/wrapper/maven-wrapper.jar";

    public static void main(String args[]) {
        System.out.println("- Downloader started");
        File baseDirectory = new File(args.length > 0 ? args[0] : ".");
        System.out.println("- Using base directory: " + baseDirectory.getAbsolutePath());

        // If the maven-wrapper.jar already exists, no need to download
        File mavenWrapperJar = new File(baseDirectory, MAVEN_WRAPPER_JAR_PATH);
        if (mavenWrapperJar.exists()) {
            System.out.println("- Found " + MAVEN_WRAPPER_JAR_PATH + ", no need to download.");
            return;
        }

        File mavenWrapperPropertyFile = new File(baseDirectory, WRAPPER_PROPERTIES_PATH);
        Properties properties = new Properties();
        InputStream in = null;
        try {
            in = new FileInputStream(mavenWrapperPropertyFile);
            properties.load(in);
        } catch (IOException e) {
            System.out.println("- ERROR loading '" + WRAPPER_PROPERTIES_PATH + "'");
        } finally {
            if (in != null) {
                try { in.close(); } catch (IOException e) { /* Ignore */ }
            }
        }

        String url = properties.getProperty("wrapperUrl");
        if (url == null || url.trim().isEmpty()) {
            System.out.println("- ERROR: 'wrapperUrl' not set in '" + WRAPPER_PROPERTIES_PATH + "'.");
            return;
        }
        System.out.println("- Downloading from: " + url);

        try {
            downloadFileFromURL(url, mavenWrapperJar);
            System.out.println("Done");
        } catch (Throwable e) {
            System.out.println("- Error downloading");
            e.printStackTrace();
        }
    }

    private static void downloadFileFromURL(String urlString, File destination) throws Exception {
        if (System.getenv("MVNW_USERNAME") != null && System.getenv("MVNW_PASSWORD") != null) {
            String userInfo = System.getenv("MVNW_USERNAME") + ":" + System.getenv("MVNW_PASSWORD");
            URL url = new URL(null, urlString, new URLStreamHandler() {
                @Override
                protected URLConnection openConnection(URL url) throws IOException {
                    URLConnection connection = new URL(url.toString()).openConnection();
                    String basicAuth = "Basic " + java.util.Base64.getEncoder().encodeToString(userInfo.getBytes());
                    connection.setRequestProperty("Authorization", basicAuth);
                    return connection;
                }
            });
            downloadToFile(url, destination);
        } else {
            URL url = new URL(urlString);
            downloadToFile(url, destination);
        }
    }

    private static void downloadToFile(URL url, File destination) throws Exception {
        ReadableByteChannel rbc;
        FileOutputStream fos = null;
        try {
            URLConnection connection = url.openConnection();
            connection.setUseCaches(false);
            InputStream in = connection.getInputStream();
            rbc = Channels.newChannel(in);
            fos = new FileOutputStream(destination);
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        } finally {
            if (fos != null) {
                try { fos.close(); } catch (IOException e) { /* Ignore */ }
            }
        }
    }
}
