﻿import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Stack;

public class BalancedBrackets {

    public static void main(String []args) {
        String inString = "[ (){} ]";
        if(isBalanced(inString)) { System.out.println("\nThis brackString is balanced");}
        else {System.out.println("\nThis brackString is not balanced");}
    }

    private static boolean isBalanced(String inputString) {
        Stack<Character> brackStack = new Stack<>();

        ArrayList<Character> brackOpen = new ArrayList<>();
        brackOpen.add('[');
        brackOpen.add('(');
        brackOpen.add('{');

        ArrayList<Character> brackClosed = new ArrayList<>();
        brackClosed.add(']');
        brackClosed.add(')');
        brackClosed.add('}');

        Map<Character, Character> brackMap = new HashMap<>();
        brackMap.put(']', '[');
        brackMap.put('}', '{');
        brackMap.put(')', '(');

        for(int i = 0; i < inputString.length(); ++i ) {
            char letter = inputString.charAt(i);
            System.out.print(letter + " ");
            if(brackOpen.contains(letter)) {brackStack.push(letter);}
            if(brackClosed.contains(letter)) {
                if(brackStack.empty() || !brackMap.get(letter).equals(brackStack.peek())) {return false;}
                else {brackStack.pop();}
            }
        }
        return brackStack.empty();
    }
}