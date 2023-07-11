package adapter;

import com.google.gson.Gson;
import com.google.gson.annotations.JsonAdapter;

@JsonAdapter(UserJsonAdapter.class)
public class User {
    public final String firstName, lastName;

    public User(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public static void main(String[] args) {
        var text = new Gson().toJson(new User("aa", "bb"));
        System.out.println(text);
    }
}
