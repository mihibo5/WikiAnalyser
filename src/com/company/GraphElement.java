package com.company;

import java.util.List;

public class GraphElement {
    String title;
    String url;
    List<String> connections;

    public GraphElement(String title, String url, List<String> connections) {
        this.title = title;
        this.url = url;
        this.connections = connections;
    }

}
