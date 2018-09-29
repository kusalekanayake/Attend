package com.seng440.attend;

import java.util.HashMap;
import java.util.Map;

public class Course {

    private String courseName;
    private Map<String, Student> students;

    Course(String courseName) {
        this.courseName = courseName;
        students = new HashMap<>();
    }

    public boolean addStudent(Student student){
        boolean existing = false;
        if (students.containsKey(student.getId())) {
            existing = true;
        }
        students.put(student.getId(), student);
        return existing;
    }

    public String getStudents() {
        String studentsString = "";
        for (Student student: students.values()) {
            studentsString += student.getName() + "\n";
        }
        return studentsString;
    }


}
