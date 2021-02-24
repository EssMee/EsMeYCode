package com.LeetCode;

import org.apache.kafka.common.metrics.Stat;
import scala.Char;
import scala.runtime.Tuple3Zipped$;

import java.util.*;

 /*Time O(n) 字符串的长度 Space O(N + 括号种类)*/
public class T20Triple {
    public static void main(String[] args) {
        String test = "([)]";
        String test2 = "()[]";
        String test3 = "({})";
        String test4 = "(}{)";
        String test5 = "[[[]";
        System.out.println(new T20Triple().isValid(test5));
    }
    public boolean isValid(String s) {
        Map<Character,Character> record = new HashMap<>();
        record.put(')','(');
        record.put('}','{');
        record.put(']','[');
        if (s.length() % 2 == 1) {
            return false;
        }
        if (s.startsWith(")") || s.startsWith("}") || s.startsWith("]")) {
            return false;
        }
//`        Stack<Character> stack = new LinkedList<>();`
        Stack<Character> stack = new Stack<>();
        for (int i = 0; i < s.length(); i++) {
            char target = s.charAt(i);
            if (record.containsKey(target)) {
                if (stack.isEmpty() || record.get(target) != stack.peek()) {
                    return false;
                }
                stack.pop();
             } else {
                stack.push(target);
            }
        }
        return stack.isEmpty();
    }
}
