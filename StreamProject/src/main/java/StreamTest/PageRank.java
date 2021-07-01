package StreamTest;

import java.text.DecimalFormat;
import java.util.*;

public class PageRank {

    public static void clearnGraph(List<Set<Integer>> graph, List<Set<Integer>> graphT){
        for(Integer i = 0; i < graph.size(); i++){
            Set<Integer> set = graph.get(i);
            if(set != null && set.size() == 0){
                Set<Integer> setT = graphT.get(i);
                if(setT != null){
                    for(Integer integer : setT){
                        graph.get(integer).remove(i);
                    }
                    graphT.set(i, null);
                }
                graph.set(i, null);
            }
        }
        for(Integer i = 0; i < graph.size(); i++){
            Set<Integer> set = graph.get(i);
            if(set != null && set.size() == 0){
                clearnGraph(graph, graphT);
                break;
            }
            Set<Integer> set2 = graphT.get(i);
            if(set2 != null && set2.size() == 0){
                clearnGraph(graphT, graph);
                break;
            }
        }
    }

    public static List<Double> pageRankOnce( List<Set<Integer>> graph, List<Set<Integer>> graphT, Set<Integer> sccNode, List<Double> res){
        List<Double> newRes = new ArrayList<>();
        newRes.addAll(res);
        for(Integer i : sccNode){
            Double sum = 0.0;
            for(Integer in : graphT.get(i)){
                sum = sum + newRes.get(in) / graph.get(in).size();
            }
            newRes.set(i, sum);
        }
        for(Integer i = 0; i < res.size(); i++){
            if(newRes.get(i) - res.get(i) > 0.000001 || res.get(i) - newRes.get(i) > 0.000001){
                return pageRankOnce(graph, graphT, sccNode, newRes);
            }
        }
        return newRes;
    }

    public static void main(String[] args){
        Scanner scanner = new Scanner(System.in);

        Integer m = scanner.nextInt();
        Integer n = scanner.nextInt();
        List<Set<Integer>> graph = new ArrayList<Set<Integer>>();
        List<Set<Integer>> graphT = new ArrayList<Set<Integer>>();
        for(Integer i = 0; i < m; i++){
            graph.add(new HashSet<Integer>());
            graphT.add(new HashSet<Integer>());
        }
        for(Integer i = 0; i < n; i++){
            Integer start = scanner.nextInt();
            Integer end = scanner.nextInt();
            graph.get(start).add(end);
            graphT.get(end).add(start);
        }
        List<Integer> resID = new ArrayList<Integer>();
        Integer resNum = scanner.nextInt();
        for(Integer i = 0; i < resNum; i++){
            resID.add(scanner.nextInt());
        }
        scanner.close();
        clearnGraph(graph, graphT);

        Set<Integer> sccNode = new HashSet<>();
        List<Double> resDouble = new ArrayList<>();
        for(Integer i = 0; i < m; i++){
            if(graph.get(i) != null){
                sccNode.add(i);
            }
        }
        for(Integer i = 0; i < m; i++){
            resDouble.add(1.0/sccNode.size());
        }

        resDouble = pageRankOnce(graph, graphT, sccNode, resDouble);
        double sum = 0;
        for(Integer node: sccNode){
            sum = sum + resDouble.get(node);
        }
        for(Integer node: sccNode){
            resDouble.set(node, resDouble.get(node)/sum);
        }
        DecimalFormat df = new DecimalFormat("0.00000");

        for(Integer i = 0; i < resNum; i++){
            if(!sccNode.contains(resID.get(i))){
                System.out.println("None");
            }
            else{
                System.out.println(df.format(resDouble.get(resID.get(i))));
            }
        }
    }
}
