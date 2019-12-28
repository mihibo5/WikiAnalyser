package com.company;

public interface CrawlerInterface {
    public void onDequeue(int index, String url, int queueSize, int threadCount);
}
