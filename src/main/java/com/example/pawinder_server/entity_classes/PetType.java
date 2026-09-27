package com.example.pawinder_server.entity_classes;

import jakarta.persistence.*;

@Entity
@Table(name = "type")
public class PetType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_type")
    private Integer id;

    @Column(name = "type_name")
    private String typeName;

    public void setId(Integer id) {
        this.id = id;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public Integer getId() {
        return id;
    }

    public String getTypeName() {
        return typeName;
    }
}