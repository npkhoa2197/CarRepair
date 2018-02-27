package vn.apcs.cs426.a1551016_miniproject.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import vn.apcs.cs426.a1551016_miniproject.entities.Result;

/**
 * Created by khoanguyen on 6/20/17.
 */
public class ResultResponse {

    List<Result> results;

    public ResultResponse() {
        results = new ArrayList<Result>();
    }

    public List<Result> getResults() {
        return results;
    }

    public void setResults(List<Result> locations) {
        this.results = locations;
    }
}
