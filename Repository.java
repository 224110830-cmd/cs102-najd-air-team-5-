package com.mycompany.cs102.project.step1;

import java.util.*;

public class Repository<T extends Storeable> {
    private final Map<String, T> items = new HashMap<>();
    public boolean add(T item) {
        if (item == null || items.containsKey(item.getId())) {
            return false;
        }
        items.put(item.getId(), item);
        return true;
    }


    public T get(String id) {
        return items.get(id);
    }

    public boolean contains(String id) {
        return items.containsKey(id);
    }

 
    public T remove(String id) {
        return items.remove(id);
    }

    public List<T> getAll() {
        return new ArrayList<>(items.values());
    }

    public int size() {
        return items.size();
    }
}