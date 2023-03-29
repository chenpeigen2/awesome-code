package org.peter;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class Story {
    public abstract int id();
    public abstract String title();
//    public static Story create(int id, String title){
//        new AutoValue_Story(id,title);
//    }
}