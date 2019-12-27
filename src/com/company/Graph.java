package com.company;

import java.util.HashSet;
import java.util.Set;

public class Graph {
    public GraphElement root = null;
    Set<String> set = new HashSet<>();

    public void add(GraphElement element) {
        if (root == null) root = element;
    }
}
