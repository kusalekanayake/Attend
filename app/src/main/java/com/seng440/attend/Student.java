package com.seng440.attend;

public class Student {

    private String name;
    private String id;

    Student(String name, String id) {
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
