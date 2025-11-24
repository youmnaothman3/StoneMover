package stonemover;

import java.util.*;

public class SearchAlgorithms {

    public static List<State> BFS(State start) {
        Queue<State> q = new LinkedList<>();
        Map<State, State> parent = new HashMap<>();
        Set<State> visited = new HashSet<>();
        q.add(start);
        visited.add(start);
        parent.put(start, null);

        while (!q.isEmpty()) {
            State cur = q.poll();
            if (cur.isFinal()) return buildPath(parent, cur);
            for (State nxt : cur.getNextStates()) {
                if (!visited.contains(nxt)) {
                    visited.add(nxt);
                    parent.put(nxt, cur);
                    q.add(nxt);
                }
            }
        }
        return null;
    }

    public static List<State> DFS(State start) {
        Set<State> visited = new HashSet<>();
        Map<State, State> parent = new HashMap<>();
        parent.put(start, null);
        boolean found = dfsRec(start, visited, parent);
        if (!found) return null;
        State goal = null;
        for (State s : visited) if (s.isFinal()) { goal = s; break; }
        return buildPath(parent, goal);
    }

    private static boolean dfsRec(State cur, Set<State> visited, Map<State, State> parent) {
        visited.add(cur);
        if (cur.isFinal()) return true;
        for (State nxt : cur.getNextStates()) {
            if (!visited.contains(nxt)) {
                parent.put(nxt, cur);
                if (dfsRec(nxt, visited, parent)) return true;
            }
        }
        return false;
    }

    private static List<State> buildPath(Map<State, State> parent, State goal) {
        List<State> path = new ArrayList<>();
        State cur = goal;
        while (cur != null) {
            path.add(cur);
            cur = parent.get(cur);
        }
        Collections.reverse(path);
        return path;
    }
}
