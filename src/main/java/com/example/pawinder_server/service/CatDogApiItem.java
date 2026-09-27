package com.example.pawinder_server.service;

import java.util.List;

public class CatDogApiItem {
    private String id;
    private String url;
    private List<BreedInfo> breeds;

    public String getId() { return id; }
    public String getUrl() { return url; }
    public List<BreedInfo> getBreeds() { return breeds; }
}