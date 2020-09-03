package com.flink.zy.javaItself;

import java.util.ArrayList;
import java.util.Arrays;

public class ArrayArraysArrayList {

    public static void main(String[] args) {
        System.out.println("===Array===");
        System.out.println("数组长度固定后不可变");
        Person[] person = new Person[3];
        Person one = new Person("eason", 18);
        Person two = new Person("zy", 19);
        Person three = new Person("zch", 20);
        person[0] = one;
        person[1] = two;
        person[2] = three;
        for (Person x:person
             ) {
            System.out.println("name: " + x.getName() + " at age " + x.getAge());
        }
        System.out.println("===ArrayList===");
        System.out.println("数组列表长度可变");
        ArrayList<Person> personArrayList = new ArrayList<Person>();
        personArrayList.add(new Person("eason1", 18));
        personArrayList.add(new Person("eason2", 19));
        personArrayList.add(new Person("eason3", 20));
        personArrayList.add(new Person("eason4", 21));
        personArrayList.forEach(person1 -> System.out.println("name: " + person1.getName() + " at age " + person1.getAge()));

        System.out.println("===Arrays===");
        System.out.println("Arrays是一个与数组有关的类，提供了大量的静态方法来操作数组");
        System.out.println("person类数组转换为String" + Arrays.toString(person));
        Arrays.sort(person);
        System.out.println("对person类数组从大到小排序：" + Arrays.toString(person));

    }
    static class Person implements Comparable<Person> {
        private String name;
        private int age;
        public Person(String n, int a) {
            name = n;
            age = a;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        @Override
        public String toString() {
            return "name " + name + " at age " + age;
        }

        @Override
        public int compareTo(Person person) {
            if (this.age > person.age) {
                return -1;
            }
            if (this.age < person.age) {
                return 1;
            }
            return 0;
        }
    }
}
