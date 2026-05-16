package com.infinitylibrary.navigation;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class RouteRegistry {
    private final Map<String, RouteNode> routes = new ConcurrentHashMap<>();

    public RouteRegistry() {
        register("home", "Home", null);
        register("home/lore", "Lore", "home");
        register("home/history", "History", "home");
        register("home/players", "Players", "home");
        register("home/events", "Events", "home");
    }

    public void register(String path, String label, String parent) { routes.put(path, new RouteNode(path, label, parent)); }

    public Optional<RouteNode> resolve(String path) { return Optional.ofNullable(routes.get(path.toLowerCase(Locale.ROOT))); }

    public List<RouteNode> children(String path) {
        return routes.values().stream().filter(n -> Objects.equals(n.parentPath(), path)).sorted(Comparator.comparing(RouteNode::path)).toList();
    }

    public List<String> breadcrumbs(String path) {
        List<String> crumbs = new ArrayList<>();
        RouteNode current = routes.get(path);
        while (current != null) {
            crumbs.add(current.label());
            current = current.parentPath() == null ? null : routes.get(current.parentPath());
        }
        Collections.reverse(crumbs);
        return crumbs;
    }

    public record RouteNode(String path, String label, String parentPath) {}
}
