package org.peter.linq;

import org.apache.calcite.linq4j.Enumerable;
import org.apache.calcite.linq4j.Enumerator;
import org.apache.calcite.linq4j.Linq4j;
import org.apache.calcite.linq4j.Queryable;
import org.apache.calcite.linq4j.function.DoubleFunction1;

import java.util.Arrays;
import java.util.List;

public class LinqDemo {

    public static void main(String[] args) {

        // 创建一个包含数字的数据源
        Integer[] numbers = {1, 2, 3, 4, 5};

        // 使用LINQ4J对数据源进行筛选和转换
        Queryable<Integer> queryable = Linq4j.asEnumerable(numbers).asQueryable();
       var result =  queryable.where(n -> n %2 ==0)
                .select(n -> n * n);

        // 打印结果
        result.forEach(System.out::println);



        // 创建一个包含人员信息的数据源
        Enumerable<Person> persons = Linq4j.asEnumerable(getPersons());

        // 使用LINQ4J筛选出年龄大于等于18岁的人员
        Enumerable<Person> adults = persons.where(p -> p.getAge() >= 20);

        // 打印筛选结果
        adults.forEach(System.out::println);


        // 创建一个包含数字的数据源

        // 使用LINQ4J计算数字的平均值
        double average = Linq4j.asEnumerable(numbers).average((DoubleFunction1<Integer>) v0 -> v0);

        // 打印平均值
        System.out.println("Average: " + average);

    }


    // 获取人员信息列表
    private static List<Person> getPersons() {
        return Arrays.asList(
                new Person("Alice", 25),
                new Person("Bob", 17),
                new Person("Charlie", 30),
                new Person("David", 16)
        );
    }

    // 定义人员类
    static class Person {
        private String name;
        private int age;

        public Person(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public int getAge() {
            return age;
        }

        @Override
        public String toString() {
            return "Person{" +
                    "name='" + name + '\'' +
                    ", age=" + age +
                    '}';
        }
    }
}
