package vn.apcs.cs426.a1551016_miniproject.api;

import java.util.ArrayList;
import java.util.List;

import vn.apcs.cs426.a1551016_miniproject.entities.Direction.Route;

/**
 * Created by khoanguyen on 6/21/17.
 */

public class DirectionResponse {

    List<Route> routes;

    public DirectionResponse() {
        routes = new ArrayList<>();
    }

    public List<Route> getRoutes() {
        return routes;
    }

    public void setRoutes(List<Route> routes) {
        this.routes = routes;
    }
}
