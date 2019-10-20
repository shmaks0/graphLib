package org.shmaks.graphLib;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * Simple Graph object which supports 3 operations: {@link #addVertex}, {@link #addEdge} and {@link #getPath}
 * @param <T> User type for graph vertices, should define meaningful {@link #equals} and {@link #hashCode}
 */
public final class Graph<T extends Serializable> {

    private final boolean directed;

    // visible for testing
    final Map<Vertex<T>, Map<Vertex<T>, Edge<T>>> adjacencyList = new HashMap<>();

    /**
     * @return new empty directed graph
     */
    public static <T extends Serializable> Graph<T> newDirected() {
        return new Graph<>(true);
    }

    /**
     * @return new empty undirected graph
     */
    public static <T extends Serializable> Graph<T> newUndirected() {
        return new Graph<>(false);
    }

    private Graph(boolean directed) {
        this.directed = directed;
    }

    /**
     * Add new vertex to the graph if it doesn't exist for provided value,
     * presence of value is determined based on equals and hashcode methods
     *
     * @return vertex for provided value
     */
    public Vertex<T> addVertex(T value) {
        Vertex<T> v = new Vertex<>(value);

        adjacencyList.putIfAbsent(v, new HashMap<>());

        return v;
    }

    /**
     * Add new vertex between to vertices if it doesn't exist yet
     *
     * @return edge between provided vertices
     */
    public Edge<T> addEdge(Vertex<T> from, Vertex<T> to) {
        ensureVertices(from, to);

        Map<Vertex<T>, Edge<T>> fromAdjoins = adjacencyList.get(from);
        Map<Vertex<T>, Edge<T>> toAdjoins = adjacencyList.get(to);

        Edge<T> edge = new Edge<>(from, to);
        fromAdjoins.putIfAbsent(to, edge);

        if (!directed) {
            toAdjoins.putIfAbsent(from, new Edge<>(to, from));
        }

        return edge;
    }

    /**
     * Compute the shortest path between vertices,
     * if the vertices are not connected then returned Path object is marked as non-existent
     *
     * @return the shortest path between vertices if it exists
     */
    public Path<T> getPath(Vertex<T> from, Vertex<T> to) {
        ensureVertices(from, to);

        Map<Vertex<T>, Path<T>> paths = new HashMap<>();
        paths.put(from, new Path<>(from, from, Collections.emptyList()));
        PriorityQueue<Path<T>> queue = new PriorityQueue<>();
        queue.add(paths.get(from));
        while (!queue.isEmpty()) {
            Path<T> path = queue.poll();
            if (path.to.equals(to)) {
                return path;
            }
            adjacencyList.get(path.to).forEach( (nextTo, edge) -> {
                Path<T> newPath = path.extend(edge);
                Path<T> oldPath = paths.get(nextTo);
                if (oldPath == null || newPath.compareTo(oldPath) < 0) {
                    paths.put(nextTo, newPath);
                    queue.add(newPath);
                    if (oldPath != null) {
                        queue.remove(oldPath);
                    }
                }
            });
        }

        return Path.noPath(from, to);
    }

    private void ensureVertices(Vertex<T> from, Vertex<T> to) {
        if (from == null) throw new NullPointerException("Null value for 'from'");
        if (to == null) throw new NullPointerException("Null value for 'to'");
        if (!adjacencyList.containsKey(from)) throw new IllegalArgumentException("Vertex " + from + " is not found");
        if (!adjacencyList.containsKey(to)) throw new IllegalArgumentException("Vertex " + to + " is not found");
    }

}
