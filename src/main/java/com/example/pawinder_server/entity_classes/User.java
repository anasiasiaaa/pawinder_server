package com.example.pawinder_server.entity_classes;

import jakarta.persistence.*;

@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_user")
    private Integer id;


    private String nickname;
    private String email;

    @Column(name = "pass_hash")
    private String passHash;

    private String salt;


    public void setId(Integer id) {
        this.id = id;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassHash(String passHash) {
        this.passHash = passHash;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public Integer getId() {
        return id;
    }

    public String getNickname() {
        return nickname;
    }

    public String getEmail() {
        return email;
    }

    public String getPassHash() {
        return passHash;
    }

    public String getSalt() {
        return salt;
    }
}