package main.model;

import java.util.Objects;

public class Feature {
    private String mName;
    private int mRawFreq;
    //private int mTfIdfFreq;

    public Feature(String name, int count) {
        this.mName = name;
        this.mRawFreq = count;
    }

    public void increaseFreq()
    {
        this.mRawFreq +=1;
    }

    public String getName() {
        return mName;
    }

    public int getRawFreq() {
        return mRawFreq;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Feature feature = (Feature) o;
        return mName.equals(feature.mName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mName);
    }
}
