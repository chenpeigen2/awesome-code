package adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class UserJsonAdapter extends TypeAdapter<User> {
    @Override public void write(JsonWriter out, User user) throws IOException {
        // implement write: combine firstName and lastName into name
        out.beginObject();
        out.name("name");
        out.value(user.firstName + "###" + user.lastName);
        out.endObject();
        // implement the write method
    }
    @Override public User read(JsonReader in) throws IOException {
        // implement read: split name into firstName and lastName
        in.beginObject();
        in.nextName();
        String[] nameParts = in.nextString().split(" ");
        in.endObject();
        return new User(nameParts[0], nameParts[1]);
    }
}
