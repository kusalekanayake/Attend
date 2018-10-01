package com.seng440.attend.model;

public class Student {

    private String name;
    private String id;

    public Student(String name, String id) {
        this.name = name;
        this.id = id;
    }

    public String getId() {
        return this.id;
    }


    public String getName() {
        return this.name;
    }
}
