package com.example.pawinder_server.entity_classes;

import jakarta.persistence.*;

@Entity
@Table(name = "city")
public class City {
    @Id
    @Column(name = "id_city")
    private Integer id;

    @Column(name = "city")
    private String name;

    @Column(name = "id_country")
    private Integer countryId;

    public Integer getId() { return id; }
    public String getName() { return name; }
    public Integer getCountryId() { return countryId; }
}