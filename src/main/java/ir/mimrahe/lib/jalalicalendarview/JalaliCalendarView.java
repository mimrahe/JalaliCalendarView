package ir.mimrahe.lib.jalalicalendarview;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import ir.mimrahe.lib.jalalidate.CalendarConverter;
import ir.mimrahe.lib.jalalidate.JalaliDate;

public class JalaliCalendarView extends LinearLayout {
    ViewPager viewPagerCalendar;
    ArrayList<GridView> gridViews = new ArrayList<>();
    LinearLayout linearLayoutWeeks;
    private CalendarViewPagerAdapter mCalendarViewPagerAdapter;
    private JalaliDate mJalaliToday;
    private ArrayList<JalaliDate> jalaliDates = new ArrayList<>();
    private LayoutInflater mLayoutInflater;
    private OnDaySelectedListener mOnDaySelectedListener;
    private Typeface mFont = null;
    private static boolean DEBUG = false;
    private static boolean isDayChanging = false;
    private final static int CALENDAR_ITEMS_COUNT = 42;
    private final static String DEBUG_TAG = "Jalali Calendar View";


    public JalaliCalendarView(Context context) {
        super(context);
    }

    public JalaliCalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mLayoutInflater = (LayoutInflater) super.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        Calendar calendar = GregorianCalendar.getInstance();
        mJalaliToday = CalendarConverter.g2Jalali(calendar);

        JalaliDate jalaliThisMonth = mJalaliToday.clone();
        jalaliThisMonth.setDay(1);

        JalaliDate jalaliPrevMonth = CalendarConverter.prevDate(jalaliThisMonth);
        JalaliDate jalaliNextMonth = CalendarConverter.nextDate(jalaliThisMonth);

        jalaliDates.add(jalaliPrevMonth);
        jalaliDates.add(jalaliThisMonth);
        jalaliDates.add(jalaliNextMonth);
    }

    public JalaliCalendarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void init(){
        changeWeeksFont();

        gridViews.add(makeMonthCalendar(jalaliDates.get(2), null));
        gridViews.add(makeMonthCalendar(jalaliDates.get(1), mJalaliToday));
        gridViews.add(makeMonthCalendar(jalaliDates.get(0), null));

        View view = mLayoutInflater.inflate(R.layout.jalali_calendar_layout_calendar, this);

        viewPagerCalendar = (ViewPager) view.findViewById(R.id.viewPagerCalendar);
        linearLayoutWeeks = (LinearLayout) view.findViewById(R.id.linearLayoutWeeks);
        mCalendarViewPagerAdapter = new CalendarViewPagerAdapter();

        viewPagerCalendar.setAdapter(mCalendarViewPagerAdapter);
        viewPagerCalendar.addOnPageChangeListener(new CalendarViewPagerChangeListener());
        viewPagerCalendar.setCurrentItem(1);
    }

    private GridView makeMonthCalendar(JalaliDate jalaliDate,@Nullable JalaliDate jalaliToday) {
        ArrayList<Day> days = new ArrayList<Day>();
        int thisMonthYear = jalaliDate.getYear();
        JalaliDate.Month thisMonth = JalaliDate.getMonth(jalaliDate.getMonth());
        JalaliDate.Month prevMonth = JalaliDate.getPrevMonth(thisMonth);

        JalaliDate firstDay = jalaliDate.clone();
        firstDay.setDay(1);

        int dayOfFirstWeek = CalendarConverter.jalali2G(firstDay).get(Calendar.DAY_OF_WEEK);
        dayOfFirstWeek = CalendarConverter.g2Jalali(dayOfFirstWeek);

        int prevMonthStart = (prevMonth.daysCount - dayOfFirstWeek) + 2;

        for (int i=prevMonthStart; i<= prevMonth.daysCount; i++){
            days.add(new Day(i, null));
        }

        for (int i=1;i<=thisMonth.daysCount;i++){
            days.add(new Day(i, new JalaliDate(thisMonthYear, thisMonth.getOrder(), i)));
        }

        int daysSum = thisMonth.daysCount + dayOfFirstWeek;
        int itemsCount = CALENDAR_ITEMS_COUNT;
        for (int i = 1; i <= (itemsCount - daysSum + 1); i++){
            days.add(new Day(i, null));
        }

        CalendarAdapter calendarAdapter = new CalendarAdapter(super.getContext(), days, jalaliToday, mFont);

        final GridView gridView = (GridView) mLayoutInflater.inflate(R.layout.jalali_calendar_layout_calendar_gridview, null);
        gridView.setAdapter(calendarAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                log("item clicked", view.getTag() + "");
                doOnDaySelected((Day) view.getTag());
            }
        });

        gridView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                log("item selected", view.getTag() + "");
                log("item selected checked", String.valueOf(parent.getSelectedItemPosition()));
                if (view.getTag() != null){
                    log("item selected", "is not null");
                    doOnDaySelected((Day) view.getTag());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                log("item selected", "nothing selected");
            }
        });

        return gridView;
    }

    private void selectFirstEnabledItem(GridView gridView) {
        ListAdapter listAdapter = gridView.getAdapter();
        for (int i = 0; i <= 7; i++){
            if (listAdapter.isEnabled(i)){
                selectItemAtPosition(gridView, i);
                break;
            }
        }
    }

    private void selectLastEnabledItem(GridView gridView){
        ListAdapter listAdapter = gridView.getAdapter();
        for (int i = 41; i >= 28; i--){
            if (listAdapter.isEnabled(i)){
                selectItemAtPosition(gridView, i);
                break;
            }
        }
    }

    private void checkToday(GridView gridView){
        ListAdapter listAdapter = gridView.getAdapter();
        for(int i = 0; i <= 35; i++){
            if (listAdapter.isEnabled(i)){
                Day day = (Day) listAdapter.getItem(i);
                if (day.getDate().equals(mJalaliToday)){
                    gridView.requestFocusFromTouch();
                    gridView.setItemChecked(i, true);
                    return;
                }
            }
        }
    }

    private void selectItemAtPosition(GridView gridView, int position){
        gridView.requestFocusFromTouch();
        gridView.setSelection(position);
        gridView.setItemChecked(position, true);
    }

    public void setFont(Typeface font){
        mFont = font;
    }

    private void changeWeeksFont(){
        View child;
        for(int i = 0; i < linearLayoutWeeks.getChildCount(); ++i)
        {
            child = linearLayoutWeeks.getChildAt(i);
            ((TextView) child).setTypeface(mFont);
        }
    }

    public void next(){
        int page = viewPagerCalendar.getCurrentItem();
        GridView gridView = gridViews.get(page);
        int checkedPosition = gridView.getCheckedItemPosition();

        if (page == 1 && checkedPosition == -1){
            checkToday(gridView);
            checkedPosition = gridView.getCheckedItemPosition();
        }

        ListAdapter listAdapter = gridView.getAdapter();

        if (listAdapter.isEnabled(checkedPosition + 1)){
            selectItemAtPosition(gridView, checkedPosition + 1);
            return;
        }

        if (page == 0){
            return;
        }

        isDayChanging = true;

        viewPagerCalendar.setCurrentItem(page - 1);
        selectFirstEnabledItem(gridViews.get(page - 1));

        isDayChanging = false;
    }

    public void previous(){
        int page = viewPagerCalendar.getCurrentItem();
        GridView gridView = gridViews.get(page);
        int checkedPosition = gridView.getCheckedItemPosition();

        if (page == 1 && checkedPosition == -1){
            checkToday(gridView);
            checkedPosition = gridView.getCheckedItemPosition();
        }

        ListAdapter listAdapter = gridView.getAdapter();

        if (listAdapter.isEnabled(checkedPosition - 1)){
            selectItemAtPosition(gridView, checkedPosition - 1);
            return;
        }

        if (page == 2){
            return;
        }

        isDayChanging = true;

        viewPagerCalendar.setCurrentItem(page + 1);
        selectLastEnabledItem(gridViews.get(page + 1));

        isDayChanging = false;
    }

    public void setDebug(boolean debug){
        DEBUG = debug;
    }

    private void log(String pre, String message){
        if (DEBUG){
            Log.e(DEBUG_TAG, pre + " => " + message);
        }
    }

    private void doOnDaySelected(Day day){
        if (mOnDaySelectedListener != null){
            mOnDaySelectedListener.onDaySelected(day);
        }
    }

    public void setOnDaySelectedListener(OnDaySelectedListener onDaySelectedListener){
        mOnDaySelectedListener = onDaySelectedListener;
    }

    public interface OnDaySelectedListener{
        void onDaySelected(Day day);
    }

    public class CalendarViewPagerAdapter extends PagerAdapter {


        public CalendarViewPagerAdapter(){}

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            log("view pager", "item instantiated " + position);
            container.addView(gridViews.get(position));

            return gridViews.get(position);
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
            log("view pager", "item removed " + position);
            int i = 0;
            if (position == 0)
                i = 2;
            if (position == 2)
                i = 0;
            log("view pager", String.valueOf(gridViews.get(i).getCheckedItemPosition()));
        }
    }

    public class CalendarViewPagerChangeListener implements ViewPager.OnPageChangeListener{

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

        @Override
        public void onPageSelected(int position) {
            log("page selected", String.valueOf(position));
            log("page selected gridview", String.valueOf(gridViews.get(position) == null));
            log("page selected checked", String.valueOf(gridViews.get(position).getCheckedItemPosition()));
            log("page selected selected", String.valueOf(gridViews.get(position).getSelectedItemPosition()));

            GridView gridView = gridViews.get(position);
            ListAdapter adapter = gridView.getAdapter();

            if (!isDayChanging){
                if (gridView.getCheckedItemCount() > 0){
                    log("items checked count", gridView.getCheckedItemCount() + "");
                    log("checked item position", gridView.getCheckedItemPosition() + "");
                    Day day = (Day) adapter.getItem(gridView.getCheckedItemPosition());
                    log("checked item", day.toString());

                    doOnDaySelected(day);
                    return;
                }
                if (position == 0) {
                    selectFirstEnabledItem(gridView);
                }

                if (position == 2) {
                    selectLastEnabledItem(gridView);
                }
            }

            log("page selected checked after selection", String.valueOf(gridView.getCheckedItemPosition()));
            log("page selected selected after selection", String.valueOf(gridView.getSelectedItemPosition()));

        }

        @Override
        public void onPageScrollStateChanged(int state) {}
    }
}
