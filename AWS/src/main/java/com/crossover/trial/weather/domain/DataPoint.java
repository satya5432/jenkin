package com.crossover.trial.weather.domain;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * A collected point, including some information about the range of collected values
 *
 * @author code test administrator
 */
public class DataPoint {

    private double mean = 0.0;

    private int first = 0;

    private  int second = 0;

    private  int third = 0;

    private  int count = 0;

    /** private constructor, use the builder to create this object */
    private DataPoint() { }

    protected DataPoint(Builder builder) {
        this.setFirst(builder.first);
        this.setMean(builder.mean);
        this.setSecond(builder.median);
        this.setThird(builder.last);
        this.setCount(builder.count);
    }

    /** the mean of the observations */
    public double getMean() {
        return mean;
    }

    public void setMean(double mean) { this.mean = mean; }

    /** 1st quartile -- useful as a lower bound */
    public int getFirst() {
        return first;
    }

    public void setFirst(int first) {
        this.first = first;
    }

    /** 2nd quartile -- median value */
    public int getSecond() {
        return second;
    }

    public void setSecond(int second) {
        this.second = second;
    }

    /** 3rd quartile value -- less noisy upper value */
    public int getThird() {
        return third;
    }

    public void setThird(int third) {
        this.third = third;
    }

    /** the total number of measurements */
    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.NO_CLASS_NAME_STYLE);
    }

    public boolean equals(Object that) {
        return this.toString().equals(that.toString());
    }

    static public class Builder {
    	private int first;
    	private int mean;
    	private int median;
    	private int last;
    	private int count;

        public Builder() { }

        public Builder withFirst(int first) {
        	this.first= first;
            return this;
        }

        public Builder withMean(int mean) {
        	this.mean = mean;
            return this;
        }

        public Builder withMedian(int median) {
        	this.median = median;
            return this;
        }

        public Builder withCount(int count) {
        	this.count = count;
            return this;
        }

        public Builder withLast(int last) {
        	this.last = last;
            return this;
        }

        public DataPoint build() {
            return new DataPoint(this);
        }
    }
}
