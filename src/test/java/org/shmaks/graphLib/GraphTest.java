package org.shmaks.graphLib;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.shmaks.graphLib.Graph.newDirected;
import static org.shmaks.graphLib.Graph.newUndirected;

class GraphTest {

    @Test
    void shouldAddNewVertex() {
        Graph<Integer> g = newDirected();
        assertTrue(g.adjacencyList.isEmpty());

        Vertex<Integer> v1 = g.addVertex(5);

        Map<Vertex<Integer>, Map<Vertex<Integer>, Edge<Integer>>> expected = Collections.singletonMap(
                v1,
                new HashMap<>()
        );
        assertEquals(expected, g.adjacencyList);

        Vertex<Integer> v2 = g.addVertex(7);

        expected = new HashMap<>();
        expected.put(v1, new HashMap<>());
        expected.put(v2, new HashMap<>());
        assertEquals(expected, g.adjacencyList);
    }

    @Test
    void shouldAddNewEdgeForDirectedGraph() {
        Graph<Integer> g = newDirected();

        Vertex<Integer> v1 = g.addVertex(5);
        Vertex<Integer> v2 = g.addVertex(7);

        Edge<Integer> edge = g.addEdge(v1, v2);

        Map<Vertex<Integer>, Map<Vertex<Integer>, Edge<Integer>>> expected = new HashMap<>();
        expected.put(v1, Collections.singletonMap(v2, edge));
        expected.put(v2, new HashMap<>());

        assertEquals(expected, g.adjacencyList);
    }

    @Test
    void shouldAddNewEdgeForUnDirectedGraph() {
        Graph<Integer> g = newUndirected();

        Vertex<Integer> v1 = g.addVertex(5);
        Vertex<Integer> v2 = g.addVertex(7);

        Edge<Integer> edge = g.addEdge(v1, v2);

        Map<Vertex<Integer>, Map<Vertex<Integer>, Edge<Integer>>> expected = new HashMap<>();
        expected.put(v1, Collections.singletonMap(v2, edge));
        expected.put(v2, Collections.singletonMap(v1, edge.reversed()));

        assertEquals(expected, g.adjacencyList);
    }

    @Test
    void shouldNotAddNewVertexIfItExists() {
        Graph<Integer> g = newDirected();

        Integer dupValue = 5;
        Vertex<Integer> v1 = g.addVertex(dupValue);
        Vertex<Integer> v2 = g.addVertex(dupValue + 1);

        g.addEdge(v1, v2);

        Map<Vertex<Integer>, Map<Vertex<Integer>, Edge<Integer>>> expected = new HashMap<>(g.adjacencyList);

        Vertex<Integer> dup = g.addVertex(dupValue);

        assertEquals(expected, g.adjacencyList);
        assertEquals(v1, dup);
    }

    @Test
    void shouldNotAddNewEdgeIfAlreadyExists() {
        Graph<Integer> g = newDirected();

        Vertex<Integer> v1 = g.addVertex(5);
        Vertex<Integer> v2 = g.addVertex(7);

        Edge<Integer> edge = g.addEdge(v1, v2);

        Map<Vertex<Integer>, Map<Vertex<Integer>, Edge<Integer>>> expected = new HashMap<>(g.adjacencyList);

        Edge<Integer> dup = g.addEdge(v1, v2);
        assertEquals(expected, g.adjacencyList);
        assertEquals(edge, dup);
    }

    @Test
    void shouldGetPathForSelf() {
        Graph<Integer> g = newDirected();

        Vertex<Integer> v1 = g.addVertex(5);
        Vertex<Integer> v2 = g.addVertex(7);

        Path<Integer> v1Path = g.getPath(v1, v1);
        Path<Integer> v2Path = g.getPath(v2, v2);

        assertTrue(v1Path.exist);
        assertEquals(new Path<>(v1, v1, Collections.emptyList()), v1Path);
        assertTrue(v2Path.exist);
        assertEquals(new Path<>(v2, v2, Collections.emptyList()), v2Path);
    }

    @Test
    void shouldReturnNonExistingPathForDisconnectedVertices() {
        Graph<Integer> g = newDirected();

        Vertex<Integer> v1 = g.addVertex(5);
        Vertex<Integer> v2 = g.addVertex(7);

        Path<Integer> v1ToV2Path = g.getPath(v1, v2);
        Path<Integer> v2toV1Path = g.getPath(v2, v1);

        assertFalse(v1ToV2Path.exist);
        assertEquals(Path.noPath(v1, v2), v1ToV2Path);
        assertFalse(v2toV1Path.exist);
        assertEquals(Path.noPath(v2, v1), v2toV1Path);
    }

    @Test
    void shouldReturnSingleEdgePathForNeighbors() {
        Graph<Integer> g = newDirected();

        Vertex<Integer> v1 = g.addVertex(5);
        Vertex<Integer> v2 = g.addVertex(7);

        Edge<Integer> edge = g.addEdge(v1, v2);

        Path<Integer> v1ToV2Path = g.getPath(v1, v2);
        Path<Integer> v2toV1Path = g.getPath(v2, v1);

        assertTrue(v1ToV2Path.exist);
        assertEquals(new Path<>(v1, v2, Collections.singletonList(edge)), v1ToV2Path);
        assertFalse(v2toV1Path.exist);
        assertEquals(Path.noPath(v2, v1), v2toV1Path);
    }

    @Test
    void shouldReturnSingleEdgePathForNeighborsInUndirectedGraph() {
        Graph<Integer> g = newUndirected();
        assertTrue(g.adjacencyList.isEmpty());

        Vertex<Integer> v1 = g.addVertex(5);
        Vertex<Integer> v2 = g.addVertex(7);

        Edge<Integer> edge = g.addEdge(v1, v2);

        Path<Integer> v1ToV2Path = g.getPath(v1, v2);
        Path<Integer> v2toV1Path = g.getPath(v2, v1);

        assertTrue(v1ToV2Path.exist);
        assertEquals(new Path<>(v1, v2, Collections.singletonList(edge)), v1ToV2Path);
        assertTrue(v2toV1Path.exist);
        assertEquals(new Path<>(v2, v1, Collections.singletonList(edge.reversed())), v2toV1Path);
    }

    @Test
    void shouldReturnShortestPathForNonNeighbors() {
        Graph<Integer> g = newUndirected();

        List<Vertex<Integer>> vertices = new ArrayList<>(10);
        for (int i = 0; i < 10; i++) {
            vertices.add(g.addVertex(i));
        }

        Edge<Integer> e1_2 = g.addEdge(vertices.get(1), vertices.get(2));
        Edge<Integer> e2_3 = g.addEdge(vertices.get(2), vertices.get(3));
        Edge<Integer> e3_5 = g.addEdge(vertices.get(3), vertices.get(5));
        Edge<Integer> e3_7 = g.addEdge(vertices.get(3), vertices.get(7));

        Path<Integer> path1_7 = g.getPath(vertices.get(1), vertices.get(7));
        Path<Integer> path5_2 = g.getPath(vertices.get(5), vertices.get(2));

        assertTrue(path1_7.exist);
        assertEquals(new Path<>(vertices.get(1), vertices.get(7), Arrays.asList(e1_2, e2_3, e3_7)), path1_7);
        assertTrue(path5_2.exist);
        assertEquals(new Path<>(vertices.get(5), vertices.get(2), Arrays.asList(e3_5.reversed(), e2_3.reversed())), path5_2);

        Edge<Integer> e2_7 = g.addEdge(vertices.get(2), vertices.get(7));
        path1_7 = g.getPath(vertices.get(1), vertices.get(7));

        assertTrue(path1_7.exist);
        assertEquals(new Path<>(vertices.get(1), vertices.get(7), Arrays.asList(e1_2, e2_7)), path1_7);
    }

    @Test
    void shouldReturnShortestPathForNeighborsAfterLinkin() {
        Graph<Integer> g = newDirected();

        List<Vertex<Integer>> vertices = new ArrayList<>(10);
        for (int i = 0; i < 10; i++) {
            vertices.add(g.addVertex(i));
        }

        for (int i = 0; i < 9; i++) {
            g.addEdge(vertices.get(i), vertices.get(i + 1));
        }

        Path<Integer> path0_9 = g.getPath(vertices.get(0), vertices.get(9));

        assertTrue(path0_9.exist);
        assertEquals(vertices.get(0), path0_9.from);
        assertEquals(vertices.get(9), path0_9.to);
        assertEquals(9, path0_9.path.size());

        g.addEdge(vertices.get(9), vertices.get(0));
        Path<Integer> expected = path0_9;
        path0_9 = g.getPath(vertices.get(0), vertices.get(9));
        assertEquals(expected, path0_9);

        Edge<Integer> e0_9 = g.addEdge(vertices.get(0), vertices.get(9));
        path0_9 = g.getPath(vertices.get(0), vertices.get(9));
        assertEquals(new Path<>(vertices.get(0), vertices.get(9), Collections.singletonList(e0_9)), path0_9);
    }

    @Test
    void shouldCheckInput() {
        Graph<Integer> g = newDirected();

        Vertex<Integer> v1 = g.addVertex(5);
        Vertex<Integer> v2 = g.addVertex(7);
        Vertex<Integer> detached = new Vertex<>(6);
        g.addEdge(v1, v2);

        assertThrows(NullPointerException.class, () -> g.addVertex(null));
        assertThrows(NullPointerException.class, () -> g.addEdge(null, v1));
        assertThrows(NullPointerException.class, () -> g.addEdge(v1, null));
        assertThrows(NullPointerException.class, () -> g.getPath(null, v1));
        assertThrows(NullPointerException.class, () -> g.getPath(v1, null));

        assertThrows(IllegalArgumentException.class, () -> g.addEdge(v1, detached));
        assertThrows(IllegalArgumentException.class, () -> g.addEdge(detached, v2));
        assertThrows(IllegalArgumentException.class, () -> g.getPath(v1, detached));
        assertThrows(IllegalArgumentException.class, () -> g.getPath(v2, detached));

    }
}
