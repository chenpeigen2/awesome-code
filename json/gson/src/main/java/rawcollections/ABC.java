package rawcollections;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class ABC extends ArrayList<String> {

    public static void main(String[] args) {
        Type mySuperClass = ABC.class.getGenericSuperclass();
        Type type = ((ParameterizedType) mySuperClass).getActualTypeArguments()[0];
        System.out.println(mySuperClass);
        System.out.println(type);


        ArrayList<String> abc = new ArrayList<>();
        mySuperClass = abc.getClass().getGenericSuperclass();
        type = ((ParameterizedType) mySuperClass).getActualTypeArguments()[0];
        System.out.println(mySuperClass);
        System.out.println(type);
    }
}
