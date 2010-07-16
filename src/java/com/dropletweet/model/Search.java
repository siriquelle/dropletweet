/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dropletweet.model;

import com.dropletweet.domain.*;
import java.util.LinkedList;

/**
 *
 * @author Siriquelle
 */
public class Search {

    private LinkedList<Tweet> results;
    private Long max_id;
    private Long since_id;
    private String refresh_url;
    private Integer results_per_page;
    private Integer page;
    private double completed_in;
    private String warning;
    private String query;

    /**
     * Get the value of query
     *
     * @return the value of query
     */
    public String getQuery() {
        return query;
    }

    /**
     * Set the value of query
     *
     * @param query new value of query
     */
    public void setQuery(String query) {
        this.query = query;
    }

    /**
     * Get the value of warning
     *
     * @return the value of warning
     */
    public String getWarning() {
        return warning;
    }

    /**
     * Set the value of warning
     *
     * @param warning new value of warning
     */
    public void setWarning(String warning) {
        this.warning = warning;
    }

    /**
     * Get the value of completed_in
     *
     * @return the value of completed_in
     */
    public double getCompleted_in() {
        return completed_in;
    }

    /**
     * Set the value of completed_in
     *
     * @param completed_in new value of completed_in
     */
    public void setCompleted_in(double completed_in) {
        this.completed_in = completed_in;
    }

    /**
     * Get the value of page
     *
     * @return the value of page
     */
    public Integer getPage() {
        return page;
    }

    /**
     * Set the value of page
     *
     * @param page new value of page
     */
    public void setPage(Integer page) {
        this.page = page;
    }

    /**
     * Get the value of results_per_page
     *
     * @return the value of results_per_page
     */
    public Integer getResults_per_page() {
        return results_per_page;
    }

    /**
     * Set the value of results_per_page
     *
     * @param results_per_page new value of results_per_page
     */
    public void setResults_per_page(Integer results_per_page) {
        this.results_per_page = results_per_page;
    }

    /**
     * Get the value of refresh_url
     *
     * @return the value of refresh_url
     */
    public String getRefresh_url() {
        return refresh_url;
    }

    /**
     * Set the value of refresh_url
     *
     * @param refresh_url new value of refresh_url
     */
    public void setRefresh_url(String refresh_url) {
        this.refresh_url = refresh_url;
    }

    /**
     * Get the value of since_id
     *
     * @return the value of since_id
     */
    public Long getSince_id() {
        return since_id;
    }

    /**
     * Set the value of since_id
     *
     * @param since_id new value of since_id
     */
    public void setSince_id(Long since_id) {
        this.since_id = since_id;
    }

    /**
     * Get the value of max_id
     *
     * @return the value of max_id
     */
    public Long getMax_id() {
        return max_id;
    }

    /**
     * Set the value of max_id
     *
     * @param max_id new value of max_id
     */
    public void setMax_id(Long max_id) {
        this.max_id = max_id;
    }

    /**
     * Get the value of results
     *
     * @return the value of results
     */
    public LinkedList<Tweet> getResults() {
        return results;
    }

    /**
     * Set the value of results
     *
     * @param results new value of results
     */
    public void setResults(LinkedList<Tweet> results) {
        this.results = results;
    }
}
