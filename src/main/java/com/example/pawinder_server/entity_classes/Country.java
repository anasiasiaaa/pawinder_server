package com.example.pawinder_server.entity_classes;

import jakarta.persistence.*;

@Entity
@Table(name = "country")
public class Country {
    @Id
    @Column(name = "id_country")
    private Integer id;

    @Column(name = "country")
    private String name;

    public Integer getId() { return id; }
    public String getName() { return name; }
}