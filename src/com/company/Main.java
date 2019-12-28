package com.company;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Main {

    //debug variables
    private static boolean debug = false;

    //time variables
    private static SimpleDateFormat dateFormatter;
    private static Date TIME_START;
    private static Date TIME_END;

    public static void main(String[] args) throws Exception {
        dateFormatter = new SimpleDateFormat("dd.MM.yyyy 'at' HH:mm:ss z");
        TIME_START = new Date(System.currentTimeMillis());
        System.out.println("Started: " + dateFormatter.format(TIME_START));

        if (args.length > 0) {
            //all we do in main function is start the process
            Crawler crawler = new Crawler(args[0], new CrawlerInterface() {

                @Override
                public void onDequeue(int index, String url, int queueSize) {
                    System.out.println(index + ": " + url + ", queue size: " + queueSize);
                    if (debug) System.out.println(printRuntimeUsage());
                }
            });
            crawler.start();
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


    private static String printRuntimeUsage() {
        Runtime runtime = Runtime.getRuntime();

        NumberFormat format = NumberFormat.getInstance();

        StringBuilder sb = new StringBuilder();
        long maxMemory = runtime.maxMemory();
        long allocatedMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();

        sb.append("free memory: ").append(format.format(freeMemory)).append(" - ");
        sb.append("allocated memory: ").append(format.format(allocatedMemory)).append(" - ");
        sb.append("max memory: ").append(format.format(maxMemory)).append(" - ");
        sb.append("total free memory: ").append(format.format((freeMemory + (maxMemory - allocatedMemory))));

        return sb.toString();
    }
}
