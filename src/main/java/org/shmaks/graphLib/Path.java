package org.shmaks.graphLib;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Immutable Path Object between 2 vertices, {@link #exist} defines whether this path existing in graph, all fields are non-nullable
 * @param <T> User type, should define meaningful {@link #equals} and {@link #hashCode}
 */
public final class Path<T extends Serializable> implements Comparable<Path<T>>, Serializable {
    private static final long serialVersionUID = -666434532020778422L;

    public final Vertex<T> from;
    public final Vertex<T> to;
    public final List<Edge<T>> path;
    public final boolean exist;

    Path(Vertex<T> from, Vertex<T> to, List<Edge<T>> path) {
        this(from, to, path, true);
    }

    private Path(Vertex<T> from, Vertex<T> to, List<Edge<T>> path, boolean exist) {
        if (from == null) throw new NullPointerException("Null value for 'from'");
        if (to == null) throw new NullPointerException("Null value for 'to'");
        if (path == null) throw new NullPointerException("Null value for 'path'");

        this.from = from;
        this.to = to;
        this.path = path;
        this.exist = exist;
    }

    Path<T> extend(Edge<T> edge) {
        if (!path.isEmpty() && !path.get(path.size() - 1).to.equals(edge.from)) {
            throw new IllegalArgumentException("Wrong edge " + edge + " to extend path " + path);
        }

        List<Edge<T>> newPath = new ArrayList<>(path.size() + 1);
        newPath.addAll(path);
        newPath.add(edge);

        return new Path<>(from, edge.to, newPath);
    }

    @Override
    public int compareTo(Path<T> o) {
        return path.size() - o.path.size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Path<?> path1 = (Path<?>) o;
        return Objects.equals(from, path1.from) &&
                Objects.equals(to, path1.to) &&
                Objects.equals(path, path1.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to, path);
    }

    @Override
    public String toString() {
        return from + "-" + path.toString() + "->" + to;
    }

    static <T extends Serializable> Path<T> noPath(Vertex<T> from, Vertex<T> to) {
        return new Path<>(from, to, Collections.emptyList(), false);
    }
}
