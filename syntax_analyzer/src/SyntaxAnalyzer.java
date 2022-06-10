import com.sun.org.apache.xpath.internal.operations.Bool;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class SyntaxAnalyzer {
    Vector<Production> pros;
    Map<String, Integer> map;
    Set<String> term;
    Set<String> nonterm;
    Map<String, Set<String>> first;
    Map<String, Set<String>> follow;
    String[][] parsetable;
    Boolean[] hasFollow ;

    SyntaxAnalyzer() {
        pros = new Vector<>();
        map = new HashMap<>();
        term = new LinkedHashSet<>();
        nonterm = new LinkedHashSet<>();
        first = new HashMap<>();
        follow = new HashMap<>();
        hasFollow = new Boolean[100];
        for(Boolean b : hasFollow){
            b=false;
        }
    }

    void getInput(int n) throws FileNotFoundException {
        Scanner scanner = new Scanner(new File("./grammar.txt"));
        scanner.nextLine();
        for (int i = 0; i < n; i++) {
            String a = scanner.nextLine();
            String[] b = a.split("->");
            pros.add(new Production(b[0], b[1]));
        }
        for (Production p :
                pros) {
            if (!(p.left.charAt(0) >= 'A' && p.left.charAt(0) <= 'Z') || p.left.length() > 1) {
                System.out.println("Wrong production " + (pros.indexOf(p) + 1) + ":" + p.left + "->" + p.right);
                System.exit(1);
            }
            if (p.left.charAt(0) == p.right.charAt(0)) {
                System.out.println("Find left recursion in production:" + p.left + "->" + p.right);
                System.exit(1);
            }
            nonterm.add(p.left);
            for (int i = 0; i < p.right.length(); i++) {
                if (!(p.right.charAt(i) >= 'A' && p.right.charAt(i) <= 'Z')) {
                    term.add(String.valueOf(p.right.charAt(i)));
                }
            }
        }
        term.add("$");
        int i = 0;
        for (String a : term) {
            map.put(a, i++);
        }
        i = 0;
        for (String a : nonterm) {
            map.put(a, i++);
            first.put(a, new HashSet<>());
            follow.put(a, new HashSet<>());
        }
        parsetable = new String[nonterm.size()][term.size()];

    }

    void getFirst(String s) {
        Boolean flag = false;
        int num = 0;
        for (int i = 0; i < pros.size(); i++) {
            String left = pros.elementAt(i).left;
            String right = pros.elementAt(i).right;
            if (left.equals(s)) {
                if (!(right.charAt(0) >= 'A' && right.charAt(0) <= 'Z')) {
                    first.get(s).add(String.valueOf(right.charAt(0)));
                } else {
                    for (int j = 0; j < right.length(); j++) {
                        if (!(right.charAt(j) >= 'A' && right.charAt(j) <= 'Z')) {
                            break;
                        } else {
                            getFirst(String.valueOf(right.charAt(j)));
                            Iterator<String> it = first.get(String.valueOf(right.charAt(j))).iterator();
                            while (it.hasNext()) {
                                String temp = it.next();
                                if (temp.equals("@")) {
                                    flag = true;

                                } else {
                                    first.get(s).add(temp);
                                }
                            }
                            if (!flag) {
                                break;
                            } else {
                                flag = false;
                                num++;
                            }
                        }
                    }
                    if (num == right.length()) {
                        first.get(s).add("@");
                    }
                }

            }

        }

    }


    void getFirst() {
        for (String a : nonterm) {
            getFirst(a);
        }
    }

    void getFollow(String s) {
        char c = s.charAt(0);
        if (s.equals(pros.elementAt(0).left)) {
            //初始非终结符的follow中加入$
            follow.get(s).add("$");
        }
        for (int i = 0; i < pros.size(); i++) {
            String t = pros.elementAt(i).right;
            for (int j = 0; j < t.length(); j++) {
                if (t.charAt(j) == c) {
                    if (j < t.length() - 1) {
                        char next = t.charAt(j + 1);
                        if (!(next >= 'A' && next <= 'Z')) {
                            follow.get(s).add(String.valueOf(next));
                        } else {

                            follow.get(s).addAll(first.get(String.valueOf(next)));
                            if (first.get(String.valueOf(next)).contains("@")) {
                                follow.get(s).remove("@");
                                if (!pros.elementAt(i).left.equals(s)) {
                                    if(!hasFollow[map.get(pros.elementAt(i).left)]) {
                                        getFollow(pros.elementAt(i).left);
                                    }
                                    follow.get(s).addAll(follow.get(pros.elementAt(i).left));
                                }

                            }
                        }
                    } else if (!pros.elementAt(i).left.equals(String.valueOf(c))) {
                        if(!hasFollow[map.get(pros.elementAt(i).left)]) {
                            getFollow(pros.elementAt(i).left);
                        }
                        follow.get(s).addAll(follow.get(pros.elementAt(i).left));
                    }
                }
            }
        }


    }


    void getFollow() {
        for (String s : nonterm) {
            getFollow(s);
            hasFollow[map.get(s)]=true;
        }
    }


    void createTable() {
        for (Production p : pros) {
            String left = p.left;
            String right = p.right;
            if (right.charAt(0) >= 'A' && right.charAt(0) <= 'Z') {
                for (String s : first.get(String.valueOf(right.charAt(0)))) {
                    parsetable[map.get(left).intValue()][map.get(s).intValue()] = right;
                }
            } else if (right.charAt(0) == '@') {
                for (String s : follow.get(left)) {
                    parsetable[map.get(left).intValue()][map.get(s).intValue()] = right;
                }
            } else {
                parsetable[map.get(left).intValue()][map.get(right.substring(0, 1)).intValue()] = right;
            }

        }
    }


    void printTable() {
        System.out.println("预测分析表：");
        System.out.format("%8s", "");
        for (String s : term) {
            System.out.format("%8s", s);
        }
        System.out.println();
        Iterator<String> it = nonterm.iterator();
        int row = 0;
        while (it.hasNext()) {
            System.out.format("%8s", it.next());
            for (int i = 0; i < term.size(); i++) {
                System.out.format("%8s", parsetable[row][i]);
            }
            row++;
            System.out.println();
        }
    }


    void check(){
        while(true) {
            try{
            System.out.println("Please enter an expression to check:");
            Scanner s = new Scanner(System.in);
            String input = s.nextLine();
            Stack<String> stack = new Stack<>();
            Queue<String> queue = new ArrayDeque<>();
            stack.push("$");
            stack.push("E");
            for (int i = 0; i < input.length(); i++) {
                queue.add(String.valueOf(input.charAt(i)));
            }
            queue.add("$");

            while (true) {
                String stk = stack.lastElement();

                String q = queue.element();

                if (stk.equals("$")) {
                    if (q.equals("$")) {
                        System.out.println("Matched");
                    } else {
                        System.out.println("Not Matched");
                    }
                    break;
                }

                if (stk.equals(q)) {
                    stack.pop();
                    queue.remove();
                    continue;
                } else if (!(stk.charAt(0) >= 'A' && stk.charAt(0) <= 'Z')) {
                    System.out.println("Not Matched");
                    break;
                } else {

                    String pro = parsetable[map.get(stk)][map.get(q)];
                    if (pro == null) {
                        System.out.println("Not Matched");
                        break;
                    }
                    System.out.println(stk + "->" + pro);
                    if (pro.equals("@")) {

                        stack.pop();
                        continue;
                    }

                    stack.pop();
                    for (int j = pro.length() - 1; j >= 0; j--) {
                        stack.push(String.valueOf(pro.charAt(j)));
                    }
                }

            }
        }catch (NullPointerException e){
                System.out.println("not matched");
            }
        }
    }

    public static void main(String[] args) throws FileNotFoundException {
        SyntaxAnalyzer sa = new SyntaxAnalyzer();
        //System.out.println("Enter the number of Expressions of the LL(1) grammar:");
        Scanner s = new Scanner(new File("./grammar.txt"));
        sa.getInput(Integer.valueOf(s.nextLine()));
        sa.getFirst();
        sa.getFollow();
        sa.createTable();
        sa.printTable();
        sa.check();



    }
}
