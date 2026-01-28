package com.udacity.jdnd.course3.critter.entity;

import com.udacity.jdnd.course3.critter.user.EmployeeSkill;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Entity
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;

    @ElementCollection(targetClass = EmployeeSkill.class)
    @Enumerated(EnumType.STRING)
    private Set<EmployeeSkill> activities;

    @ManyToMany
    private List<Employee> employees;

    @ManyToMany
    private List<Pet> pets;

    // ---------- Getters & Setters ----------

    public Long getId() {
        return id;
    }

    public LocalDate getDate() {
        return date;
    }

    public Set<EmployeeSkill> getActivities() {
        return activities;
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    public List<Pet> getPets() {
        return pets;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setActivities(Set<EmployeeSkill> activities) {
        this.activities = activities;
    }

    public void setEmployees(List<Employee> employees) {
        this.employees = employees;
    }

    public void setPets(List<Pet> pets) {
        this.pets = pets;
    }
}
