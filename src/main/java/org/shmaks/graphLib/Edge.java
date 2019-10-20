package org.shmaks.graphLib;

import java.io.Serializable;
import java.util.Objects;

/**
 * Immutable Edge Object, all fields are non-nullable
 * @param <T> User type, should define meaningful {@link #equals} and {@link #hashCode}
 */
public final class Edge<T extends Serializable> implements Comparable<Edge<T>>, Serializable {

    private static final long serialVersionUID = -960821923165521814L;

    @SuppressWarnings("WeakerAccess")
    public final Vertex<T> from;
    @SuppressWarnings("WeakerAccess")
    public final Vertex<T> to;

    Edge(Vertex<T> from, Vertex<T> to) {
        if (from == null) throw new NullPointerException("Null value for 'from'");
        if (to == null) throw new NullPointerException("Null value for 'to'");
        this.from = from;
        this.to = to;
    }

    Edge<T> reversed() {
        return new Edge<>(to, from);
    }

    @Override
    public int compareTo(Edge<T> o) {
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Edge<?> edge = (Edge<?>) o;
        return Objects.equals(from, edge.from) &&
                Objects.equals(to, edge.to);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to);
    }

    @Override
    public String toString() {
        return from + "->" + to;
    }
}
