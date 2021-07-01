package StreamTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ThePath1 {

    public static Integer findShortPath(List<List<Integer>> graph, List<List<Boolean>> graphReal, Integer i, Integer j, Integer max){
        if(max == 0 || graph.get(i).get(j) == 0 || graphReal.get(i).get(j)){
            return graph.get(i).get(j);
        }

        List<Integer> list = graph.get(i);
        for(Integer k = 0; k < list.size(); k++){
            if(list.get(k) != 0 && list.get(k) != -1 && (list.get(k) < list.get(j) || list.get(j) == -1)){
                Integer pathNext = findShortPath(graph, graphReal, k, j, max-1);
                if(pathNext != -1 && (list.get(k) + pathNext < list.get(j) || list.get(j) == -1)){
                    list.set(j, list.get(k) + pathNext);
                }
            }
        }
        graphReal.get(i).set(j, true);
        return list.get(j);
    }

    public static void main(String[] args){
        List<List<Integer>> graph = new ArrayList<List<Integer>>();
        Scanner scanner = new Scanner(System.in);
        Integer n = scanner.nextInt();
        for(int i = 0; i < n; i++){
            List<Integer> list = new ArrayList<Integer>();
            for(int j = 0; j < n; j++){
                Integer num = scanner.nextInt();
                list.add(num);
            }
            graph.add(list);
        }
        scanner.close();
        List<List<Boolean>> graphReal = new ArrayList<List<Boolean>>();
        for(Integer i = 0; i < graph.size(); i++) {
            List<Boolean> list = new ArrayList<Boolean>();
            for (Integer j = 0; j < graph.size(); j++) {
                list.add(false);
            }
            graphReal.add(list);
        }
        for(Integer i = 0; i < graph.size(); i++){
            for(Integer j = 0; j < graph.size(); j++){
                if(i != j){
                    findShortPath(graph, graphReal, i, j, graph.size());
                }
            }
        }
        for(Integer i = 0; i < n; i++) {
            for (Integer j = 0; j < n-1; j++) {
                System.out.printf("%d ", graph.get(i).get(j));
            }
            System.out.printf("%d\n", graph.get(i).get(n-1));
        }
    }
}
