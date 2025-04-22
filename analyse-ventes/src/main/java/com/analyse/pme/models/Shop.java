package com.analyse.pme.models;

public class Shop {
    private int id;
    private String city;
    private int employees;

    public Shop() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getEmployees() {
        return employees;
    }

    public void setEmployees(int employees) {
        this.employees = employees;
    }


    @Override
    public String toString() {
        return "Shop{" +
                "id=" + id +
                ", city='" + city + '\'' +
                ", employees=" + employees +
                '}';
    }
}