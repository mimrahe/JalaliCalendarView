package ir.mimrahe.lib.jalalicalendarview;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import ir.mimrahe.lib.jalalidate.JalaliDate;

public class CalendarAdapter extends ArrayAdapter {
    private List<Day> mDays;
    private JalaliDate mCurrentDay;
    private LayoutInflater mLayoutInflator;
    private NumberFormat mNumberFormat;
    private Typeface mFont;

    public CalendarAdapter(Context context, List days,@Nullable JalaliDate currentDay, Typeface font) {
        super(context, R.layout.jalali_calendar_single_cel);
        mDays = days;
        mCurrentDay = currentDay;
        mLayoutInflator = LayoutInflater.from(context);
        mNumberFormat = NumberFormat.getInstance(new Locale("fa", "IR"));
        mFont = font;
    }

    @Override
    public int getCount() {
        return mDays.size();
    }

    @Nullable
    @Override
    public Day getItem(int position) {
        return mDays.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        if (mDays.get(position).getDate() == null){
            return false;
        }
        return true;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = mLayoutInflator.inflate(R.layout.jalali_calendar_single_cel, parent, false);
        }
        Day day = mDays.get(position);
        CalendarCell calendarCell = (CalendarCell) convertView.findViewById(R.id.jalaliCalendarViewTextViewDay);
        if (day.getDate() == null){
            calendarCell.setEnabled(false);
        }
        if (day.getDate() != null){
            calendarCell.setTag(day);
            if (mCurrentDay != null && day.getDate().equals(mCurrentDay)){
                calendarCell.setBackgroundResource(R.drawable.jalali_calendar_item_today);
            }
        }
        calendarCell.setText(String.valueOf(mNumberFormat.format(day.getNo())));
        if (mFont != null){
            calendarCell.setTypeface(mFont);
        }

        return calendarCell;
    }
}
