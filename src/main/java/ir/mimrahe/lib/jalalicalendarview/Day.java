package ir.mimrahe.lib.jalalicalendarview;


import ir.mimrahe.lib.jalalidate.JalaliDate;

public class Day {
    private int mNo;
    private JalaliDate mDate;

    Day(int no, JalaliDate date){
        mNo = no;
        mDate = date;
    }

    public int getNo(){
        return mNo;
    }

    public JalaliDate getDate(){
        return mDate;
    }

    public String toString()
    {
        return getNo() + ":" + getDate();
    }
}
