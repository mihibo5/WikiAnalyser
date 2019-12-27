package com.company;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Main {

    //base variables
    private final static String BASE_URL = "https://en.wikipedia.org";

    //time variables
    private static SimpleDateFormat dateFormatter;
    private static Date TIME_START;
    private static Date TIME_END;

    private static Graph graph = new Graph();

    public static void main(String[] args) throws Exception {
        dateFormatter = new SimpleDateFormat("dd.MM.yyyy 'at' HH:mm:ss z");
        TIME_START = new Date(System.currentTimeMillis());
        System.out.println("Started: " + dateFormatter.format(TIME_START));

        if (args.length > 0) {
            //all we do in main function is start the process
            Main.start(BASE_URL + args[0]);
        }
        else {
            throw new Exception("URL required!");
        }

        TIME_END = new Date(System.currentTimeMillis());
        System.out.println("Ended: " + dateFormatter.format(TIME_END));

        long diffInMillis = Math.abs(TIME_END.getTime() - TIME_START.getTime());
        long diff = TimeUnit.DAYS.convert(diffInMillis, TimeUnit.MILLISECONDS);
        System.out.println("Duration: " + diff);


    }

    private static void start(String startUrl) throws IOException {
        /*
        now we should loop through all elements for which we have two options:
            -recursively
            -queue: selected because then we can move away from source page one step at the time
        */

        //we declare start
        Queue<String> queue = new LinkedList<>();
        if (graph.set.add(startUrl)) {
            queue.add(startUrl);
        }

        //we use debug index
        int index = 0;

        //we loop as long as we have elements in queue
        while (queue.size() > 0) {
            String currentUrl = queue.remove();
            System.out.println(index + ": " + currentUrl + ", queue size: " + queue.size());

            String result = httpRequest(currentUrl);
            if (result != null) {
                String title = extractTitle(result);
                List<String> urls = extractUrls(extractContent(result));
                for (String url: urls) {
                    //we check if we have not yet been on that page
                    if (graph.set.add(url)) {
                        queue.add(url);
                    }

                    //we treat it differently if we have
                    else {

                    }
                }

                graph.add(new GraphElement(title, startUrl, urls));
                index++;
            }
        }
    }

    private static String httpRequest(String url) {
        try {
            URL requestURL = new URL(url);
            URLConnection requestConnection = requestURL.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(requestConnection.getInputStream()));


            StringBuilder result = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                result.append(inputLine);
            }
            in.close();

            return result.toString();
        } catch (IOException e) {
            return null;
        }
    }

    private static String extractTitle(String content) {
        String title = content.substring(content.indexOf("<title>") + 7, content.indexOf("</title>"));
        int wikiIndex = title.indexOf(" - Wikipedia");
        if (wikiIndex == -1) wikiIndex = title.length();
        return title.substring(0, wikiIndex);
    }

    private static String extractContent(String content) {
        int start = Math.max(0, content.indexOf("<div id=\"content\" class=\"mw-body\" role=\"main\">"));
        int end = content.indexOf("<span class=\"mw-headline\" id=\"References\">References</span>");
        if (end == -1) end = content.length() - 1;

        return content.substring(start, end);
    }

    private static List<String> extractUrls(String content) {
        /*
         Here we extract all urls from a specific site.
         Notes:
            -do we ignore search results (https://en.wikipedia.org/w/index.php?title=...)?
                *it would decrease the amount of found pages, but would keep the graph integrity
        */

        List<String> urls = new ArrayList<>();

        while (content.contains("<a href=")) {
            //move to the first found url
            content = content.substring(content.indexOf("<a href="));
            String sub = content.substring(content.indexOf("<a href="), content.indexOf("</a>") + 4);

            //check if the link is correct
            if (!sub.contains("Wikipedia:") &&
                    !sub.contains("File:") &&
                    !sub.contains("https://") &&
                    !sub.contains("wikimedia.org") &&
                    !sub.contains("m.wikipedia.org") &&
                    !sub.contains("wiktionary.org") &&
                    !sub.contains("www.wikidata.org") &&
                    !sub.contains("www.wikimediafoundation.org") &&
                    !sub.contains("#")) {
                //url is correct
                String startUrl = sub.substring(sub.indexOf("href=") + 6);
                if (startUrl.contains(" ") && startUrl.charAt(0) == '/') {
                    startUrl = startUrl.substring(0, startUrl.indexOf(" "));
                    urls.add((BASE_URL + startUrl.substring(0, startUrl.length() - 1)));
                }

                //otherwise url is actually incorrect
            }

            //move past first found url
            content = content.substring(content.indexOf("</a>") + 4);
        }

        return urls;
    }
}
