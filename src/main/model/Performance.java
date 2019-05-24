package main.model;

import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;

public class Performance {

    private String mCategory;

    private double mTpRate=0.0;
    private double mFpRate=0.0;
    private double mFnRate=0.0;

    public Performance(String category) {
        this.mCategory = category;
    }

    public void addValue(String realCategory, String assignedCategory)
    {
        if(realCategory.equals(mCategory) && assignedCategory.equals(mCategory))
            mTpRate++;
        else if(!realCategory.equals(mCategory) && assignedCategory.equals(mCategory))
            mFpRate++;
        else if(realCategory.equals(mCategory))
            mFnRate++;
    }

    public double calculatePrecision() {
        return mTpRate/(mTpRate+mFpRate);
    }

    public double calculateRecall() {
        return mTpRate/(mTpRate+mFnRate);
    }

    public double calculateFScore() {
        double pr = calculatePrecision();
        double rc = calculateRecall();

        return 2*((pr*rc)/(pr+rc));
    }
}
