package stu;

import com.google.gson.annotations.SerializedName;

public class Student {


    //    https://blog.csdn.net/mqdxiaoxiao/article/details/90300900
    @SerializedName("student_name")
    public String name;
    @SerializedName("student_age")
    public int age;

    public Student(String name, int age) {
        this.name = name;
        this.age = age;
    }

    @Override
    public String toString() {
        return "Student{" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}
