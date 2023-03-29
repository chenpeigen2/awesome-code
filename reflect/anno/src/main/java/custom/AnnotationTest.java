package custom;


////返回指定的注解
//getAnnotation
////判断当前元素是否被指定注解修饰
//        isAnnotationPresent
////返回所有的注解
//        getAnnotations


public class AnnotationTest {
    public static void main(String[] args) {
        Person p = new Person();
        Class clazz = Person.class;

        clazz = p.getClass();
        //判断person对象上是否有Info注解
        if (clazz.isAnnotationPresent(Info.class)) {
            System.out.println("Person类上配置了Info注解！");
            //获取该对象上Info类型的注解
            Info infoAnno = (Info) clazz.getAnnotation(Info.class);
            System.out.println("person.name :" + infoAnno.value() + ",person.isDelete:" + infoAnno.isDelete());
        } else {
            System.out.println("Person类上没有配置Info注解！");
        }
    }
}
