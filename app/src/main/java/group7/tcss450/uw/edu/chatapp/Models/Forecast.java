package group7.tcss450.uw.edu.chatapp.Models;

public class Forecast {

    String mDay, mHighLow, mConditions;

    public Forecast (final String theDay, final String theHighLow, final String theConditions) {
        mDay = theDay;
        mHighLow = theHighLow;
        mConditions = theConditions;

    }

    public String getDay() {
        return mDay;
    }

    public String getHighLow() {
        return mHighLow;
    }

    public String getConditions() {
        return mConditions;
    }



}
