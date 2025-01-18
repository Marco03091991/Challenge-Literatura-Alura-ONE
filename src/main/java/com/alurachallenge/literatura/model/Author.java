package com.alurachallenge.literatura.model;


import jakarta.persistence.*;

@Entity
@Table(name = "authors")
public class Author {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    private Integer birthDay;
    private Integer dayDeath;

    public Author(){}

    public Author(DataAuthor dataAuthor) {
        this.name = dataAuthor.name();
        this.birthDay = dataAuthor.birthday();
        this.dayDeath = dataAuthor.datedeath();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(Integer birthDay) {
        this.birthDay = birthDay;
    }

    public Integer getDayDeath() {
        return dayDeath;
    }

    public void setDayDeath(Integer dayDeath) {
        this.dayDeath = dayDeath;
    }
}
