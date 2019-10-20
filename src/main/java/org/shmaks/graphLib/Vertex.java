package org.shmaks.graphLib;

import java.io.Serializable;
import java.util.Objects;

/**
 * Immutable Vertex Object, all fields are non-nullable
 * @param <T> User type, should define meaningful {@link #equals} and {@link #hashCode}
 */
public final class Vertex<T extends Serializable> implements Serializable {

    private static final long serialVersionUID = -4915526725264445074L;

    @SuppressWarnings("WeakerAccess")
    public final T value;

    Vertex(T value) {
        if (value == null) throw new NullPointerException("Null value for 'vertex'");
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vertex<?> vertex = (Vertex<?>) o;
        return Objects.equals(value, vertex.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return '[' + value.toString() + ']';
    }
}
