"""Download a road network around Spiddal, County Galway, and export it
in the weighted edge-list format used by GraphLists.java.

Output format:
    V E
    u v w
    u v w

Vertices are relabelled to integers starting at 1. Edge weights are road
lengths in metres.
"""

from __future__ import annotations

from pathlib import Path

import networkx as nx
import osmnx as ox


PLACE_NAME = "Spiddal, County Galway, Ireland" # Replace with your chosen location
OUTPUT_FILE = Path("wGraph_Large.txt") # Replace with your desired output file path
MAX_NODES = 3600
NETWORK_TYPE = "drive"
SEARCH_DISTANCE_METERS = 12000


def build_graph(place_name: str, search_distance_meters: int) -> nx.Graph:
    """Download and clean the road network around the chosen place."""
    center_point = ox.geocoder.geocode(place_name)
    graph = ox.graph_from_point(
        center_point,
        dist=search_distance_meters,
        network_type=NETWORK_TYPE,
        simplify=True,
    )

    # Keep the largest connected component so the result is usable by traversal
    # and MST algorithms.
    graph = ox.truncate.largest_component(graph, strongly=False)

    # Convert the directed street graph into a simple undirected graph and keep
    # only one edge between any pair of vertices.
    graph = ox.convert.to_undirected(graph)
    graph = nx.Graph(graph)

    return graph


def relabel_nodes(graph: nx.Graph) -> nx.Graph:
    """Relabel graph nodes to consecutive integers starting at 1."""
    mapping = {node: index + 1 for index, node in enumerate(graph.nodes())}
    return nx.relabel_nodes(graph, mapping, copy=True)


def export_weighted_edges(graph: nx.Graph, output_file: Path) -> None:
    """Write the graph in the assignment's plain-text weighted edge format."""
    with output_file.open("w", encoding="utf-8") as handle:
        handle.write(f"{graph.number_of_nodes()} {graph.number_of_edges()}\n")

        for u, v, data in graph.edges(data=True):
            length = int(round(data.get("length", 1)))
            handle.write(f"{u} {v} {length}\n")


def main() -> None:
    ox.settings.use_cache = True
    ox.settings.log_console = True

    graph = build_graph(PLACE_NAME, SEARCH_DISTANCE_METERS)
    graph = relabel_nodes(graph)

    node_count = graph.number_of_nodes()
    edge_count = graph.number_of_edges()

    print(f"Downloaded graph for: {PLACE_NAME}")
    print(f"Search distance: {SEARCH_DISTANCE_METERS} meters")
    print(f"Nodes: {node_count}")
    print(f"Edges: {edge_count}")

    if node_count > MAX_NODES:
        raise ValueError(
            f"Graph is too large for this assignment: {node_count} nodes > {MAX_NODES}. "
            "Reduce the area size before exporting."
        )

    export_weighted_edges(graph, OUTPUT_FILE)
    print(f"Saved to {OUTPUT_FILE.resolve()}")


if __name__ == "__main__":
    main()
