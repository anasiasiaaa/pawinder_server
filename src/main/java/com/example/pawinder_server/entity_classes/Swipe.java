package com.example.pawinder_server.entity_classes;
import jakarta.persistence.*;

@Entity
@Table(name = "swipe")
public class Swipe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_swipe")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_user")
    private User user;

    @ManyToOne
    @JoinColumn(name = "id_pet")
    private Pet pet;

    @Column(name = "is_like")
    private Boolean like;

    public void setId(Integer id) {
        this.id = id;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setPet(Pet pet) {
        this.pet = pet;
    }

    public void setLike(Boolean like) {
        this.like = like;
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

    public Boolean getLike() {
        return like;
    }
}
