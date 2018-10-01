package com.seng440.attend.model;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Course {

    private String courseName;
    private Map<String, Student> students;

    public Course(String courseName) {
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

    public ArrayList<Student> getStudentArray() {
        ArrayList<Student> studentArray = new ArrayList<>();
        for (Student student: students.values()) {
            studentArray.add(student);
        }
        return studentArray;
    }

    public String getCourseName() {
        return this.courseName;
    }

    public String toString() {
        String studentsString = "";
        for (Student student: students.values()) {
            studentsString += student.getName() + "," + student.getId() + "\n";
        }
        return studentsString;
    }

    public void parseStudents(String input) {
        if (input != null) {
            String[] students = input.split("\n");
            Log.d("STUDENTS", students.toString());
            if (students.length > 1 || (students.length == 1 && students[0] != "")) {
                for (String studentString : students) {
                    String[] student = studentString.split(",");
                    addStudent(new Student(student[0], student[1]));
                }
            }
        }
    }


}
