package com.example.pawinder_server.entity_classes;
import jakarta.persistence.*;

@Entity
@Table(name = "pet")
public class Pet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pet")
    private Integer id;

    @Column(name = "api_pet_id")
    private String apiPetId;

    @ManyToOne
    @JoinColumn(name = "id_type")
    private PetType type;

    private String name;
    private String description;

    @Column(name = "photo_url")
    private String photoUrl;

    public void setId(Integer id) {
        this.id = id;
    }

    public void setApiPetId(String apiPetId) {
        this.apiPetId = apiPetId;
    }

    public void setType(PetType type) {
        this.type = type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public Integer getId() {
        return id;
    }

    public String getApiPetId() {
        return apiPetId;
    }

    public PetType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }
}