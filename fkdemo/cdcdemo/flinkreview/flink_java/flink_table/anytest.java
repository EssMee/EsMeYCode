package flink_java.flink_table;

import java.time.LocalDate;
public class anytest {
    public static void main(String[] args) {
        LocalDate lDate = LocalDate.parse("1996-01-05");
        System.out.println(lDate);
        System.out.println(lDate.getClass());
    }
}
