package repeats;

import java.lang.annotation.Repeatable;

//https://blog.csdn.net/howeres/article/details/120106581?spm=1001.2101.3001.6661.1&utm_medium=distribute.pc_relevant_t0.none-task-blog-2%7Edefault%7ECTRLIST%7Edefault-1-120106581-blog-103651646.pc_relevant_aa2&depth_1-utm_source=distribute.pc_relevant_t0.none-task-blog-2%7Edefault%7ECTRLIST%7Edefault-1-120106581-blog-103651646.pc_relevant_aa2&utm_relevant_index=1
@Repeatable(APIS.class)
public @interface API {
    String content() default "please add tag";
}
