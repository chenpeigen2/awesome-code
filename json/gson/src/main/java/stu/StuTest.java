package stu;

import com.google.gson.Gson;

public class StuTest {

    public static void toObject() {
        // {"student_age":28,"student_name":"野猿新一"}
        String json = "{\"student_age\":28,\"student_name\":\"野猿新一\"}";
        Student student = new Gson().fromJson(json, Student.class);
        System.out.println(student);
    }

    public static void main(String[] args) {
        toObject();
    }

}
