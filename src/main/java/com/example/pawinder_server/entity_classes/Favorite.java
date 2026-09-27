package com.example.pawinder_server.entity_classes;
import jakarta.persistence.*;

@Entity
@Table(name = "favorites")
public class Favorite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_favorite")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_user")
    private User user;

    @ManyToOne
    @JoinColumn(name = "id_pet")
    private Pet pet;

    public void setId(Integer id) {
        this.id = id;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setPet(Pet pet) {
        this.pet = pet;
    }

    public Integer getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public Pet getPet() {
        return pet;
    }
}
