package com.google.android.material.datepicker;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ListAdapter;
import androidx.core.util.Pair;
import androidx.core.view.AccessibilityDelegateCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.R;
import com.google.android.material.button.MaterialButton;
import java.util.Calendar;
import java.util.Iterator;

/* loaded from: classes.dex */
public final class MaterialCalendar<S> extends PickerFragment<S> {
    private static final String CALENDAR_CONSTRAINTS_KEY = "CALENDAR_CONSTRAINTS_KEY";
    private static final String CURRENT_MONTH_KEY = "CURRENT_MONTH_KEY";
    private static final String DAY_VIEW_DECORATOR_KEY = "DAY_VIEW_DECORATOR_KEY";
    private static final String GRID_SELECTOR_KEY = "GRID_SELECTOR_KEY";
    private static final int SMOOTH_SCROLL_MAX = 3;
    private static final String THEME_RES_ID_KEY = "THEME_RES_ID_KEY";
    private CalendarConstraints calendarConstraints;
    private CalendarSelector calendarSelector;
    private CalendarStyle calendarStyle;
    private Month current;
    private DateSelector<S> dateSelector;
    private View dayFrame;
    private DayViewDecorator dayViewDecorator;
    private View monthNext;
    private View monthPrev;
    private RecyclerView recyclerView;
    private int themeResId;
    private View yearFrame;
    private RecyclerView yearSelector;
    static final Object MONTHS_VIEW_GROUP_TAG = "MONTHS_VIEW_GROUP_TAG";
    static final Object NAVIGATION_PREV_TAG = "NAVIGATION_PREV_TAG";
    static final Object NAVIGATION_NEXT_TAG = "NAVIGATION_NEXT_TAG";
    static final Object SELECTOR_TOGGLE_TAG = "SELECTOR_TOGGLE_TAG";

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public enum CalendarSelector {
        DAY,
        YEAR
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public interface OnDayClickListener {
        void onDayClick(long j);
    }

    public static <T> MaterialCalendar<T> newInstance(DateSelector<T> dateSelector, int themeResId, CalendarConstraints calendarConstraints) {
        return newInstance(dateSelector, themeResId, calendarConstraints, null);
    }

    public static <T> MaterialCalendar<T> newInstance(DateSelector<T> dateSelector, int themeResId, CalendarConstraints calendarConstraints, DayViewDecorator dayViewDecorator) {
        MaterialCalendar<T> materialCalendar = new MaterialCalendar<>();
        Bundle args = new Bundle();
        args.putInt(THEME_RES_ID_KEY, themeResId);
        args.putParcelable(GRID_SELECTOR_KEY, dateSelector);
        args.putParcelable(CALENDAR_CONSTRAINTS_KEY, calendarConstraints);
        args.putParcelable(DAY_VIEW_DECORATOR_KEY, dayViewDecorator);
        args.putParcelable(CURRENT_MONTH_KEY, calendarConstraints.getOpenAt());
        materialCalendar.setArguments(args);
        return materialCalendar;
    }

    @Override // androidx.fragment.app.Fragment
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putInt(THEME_RES_ID_KEY, this.themeResId);
        bundle.putParcelable(GRID_SELECTOR_KEY, this.dateSelector);
        bundle.putParcelable(CALENDAR_CONSTRAINTS_KEY, this.calendarConstraints);
        bundle.putParcelable(DAY_VIEW_DECORATOR_KEY, this.dayViewDecorator);
        bundle.putParcelable(CURRENT_MONTH_KEY, this.current);
    }

    @Override // androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Bundle activeBundle = bundle == null ? getArguments() : bundle;
        this.themeResId = activeBundle.getInt(THEME_RES_ID_KEY);
        this.dateSelector = (DateSelector) activeBundle.getParcelable(GRID_SELECTOR_KEY);
        this.calendarConstraints = (CalendarConstraints) activeBundle.getParcelable(CALENDAR_CONSTRAINTS_KEY);
        this.dayViewDecorator = (DayViewDecorator) activeBundle.getParcelable(DAY_VIEW_DECORATOR_KEY);
        this.current = (Month) activeBundle.getParcelable(CURRENT_MONTH_KEY);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v1, types: [android.content.Context, com.google.android.material.datepicker.CalendarStyle] */
    /* JADX WARN: Type inference failed for: r0v16, types: [android.content.Context, android.view.ContextThemeWrapper, androidx.recyclerview.widget.RecyclerView] */
    /* JADX WARN: Type inference failed for: r0v2, types: [android.content.Context, com.google.android.material.datepicker.CalendarConstraints] */
    /* JADX WARN: Type inference failed for: r21v0, types: [android.view.LayoutInflater] */
    /* JADX WARN: Type inference failed for: r3v2, types: [com.google.android.material.datepicker.CalendarConstraints] */
    /* JADX WARN: Type inference failed for: r8v1, types: [android.view.View] */
    /* JADX WARN: Type inference failed for: r8v2 */
    /* JADX WARN: Type inference failed for: r8v3 */
    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        int i;
        int i2;
        ?? calendarStyle = new CalendarStyle(new ContextThemeWrapper(getContext(), this.themeResId));
        this.calendarStyle = calendarStyle;
        LayoutInflater cloneInContext = layoutInflater.cloneInContext(calendarStyle);
        ?? r0 = this.calendarConstraints;
        Month start = r0.getStart();
        if (MaterialDatePicker.isFullscreen(r0)) {
            i = R.layout.mtrl_calendar_vertical;
            i2 = 1;
        } else {
            i = R.layout.mtrl_calendar_horizontal;
            i2 = 0;
        }
        View inflate = cloneInContext.inflate(i, viewGroup, false);
        inflate.setMinimumHeight(getDialogPickerHeight(requireContext()));
        GridView gridView = (GridView) inflate.findViewById(R.id.mtrl_calendar_days_of_week);
        ViewCompat.setAccessibilityDelegate(gridView, new AccessibilityDelegateCompat() { // from class: com.google.android.material.datepicker.MaterialCalendar.1
            @Override // androidx.core.view.AccessibilityDelegateCompat
            public void onInitializeAccessibilityNodeInfo(View view, AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
                super.onInitializeAccessibilityNodeInfo(view, accessibilityNodeInfoCompat);
                accessibilityNodeInfoCompat.setCollectionInfo(null);
            }
        });
        int firstDayOfWeek = this.calendarConstraints.getFirstDayOfWeek();
        gridView.setAdapter((ListAdapter) (firstDayOfWeek > 0 ? new DaysOfWeekAdapter(firstDayOfWeek) : new DaysOfWeekAdapter()));
        gridView.setNumColumns(start.daysInWeek);
        gridView.setEnabled(false);
        ?? r02 = (RecyclerView) inflate.findViewById(R.id.mtrl_calendar_months);
        this.recyclerView = r02;
        final int i3 = i2;
        this.recyclerView.setLayoutManager(new SmoothCalendarLayoutManager(getContext(), i2, false) { // from class: com.google.android.material.datepicker.MaterialCalendar.2
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // androidx.recyclerview.widget.LinearLayoutManager
            public void calculateExtraLayoutSpace(RecyclerView.State state, int[] ints) {
                if (i3 == 0) {
                    ints[0] = MaterialCalendar.this.recyclerView.getWidth();
                    ints[1] = MaterialCalendar.this.recyclerView.getWidth();
                } else {
                    ints[0] = MaterialCalendar.this.recyclerView.getHeight();
                    ints[1] = MaterialCalendar.this.recyclerView.getHeight();
                }
            }
        });
        this.recyclerView.setTag(MONTHS_VIEW_GROUP_TAG);
        DateSelector<S> dateSelector = this.dateSelector;
        CalendarConstraints calendarConstraints = this.calendarConstraints;
        MonthsPagerAdapter monthsPagerAdapter = new MonthsPagerAdapter(r02, dateSelector, i, this.dayViewDecorator, new OnDayClickListener() { // from class: com.google.android.material.datepicker.MaterialCalendar.3
            /* JADX WARN: Multi-variable type inference failed */
            @Override // com.google.android.material.datepicker.MaterialCalendar.OnDayClickListener
            public void onDayClick(long j) {
                if (MaterialCalendar.this.calendarConstraints.getDateValidator().isValid(j)) {
                    MaterialCalendar.this.dateSelector.select(j);
                    Iterator<OnSelectionChangedListener<S>> it2 = MaterialCalendar.this.onSelectionChangedListeners.iterator();
                    while (it2.hasNext()) {
                        it2.next().onSelectionChanged(MaterialCalendar.this.dateSelector.getSelection());
                    }
                    MaterialCalendar.this.recyclerView.getAdapter().notifyDataSetChanged();
                    if (MaterialCalendar.this.yearSelector != null) {
                        MaterialCalendar.this.yearSelector.getAdapter().notifyDataSetChanged();
                    }
                }
            }
        });
        this.recyclerView.setAdapter(monthsPagerAdapter);
        int integer = r02.getResources().getInteger(R.integer.mtrl_calendar_year_selector_span);
        this.yearSelector = (RecyclerView) inflate.findViewById(R.id.mtrl_calendar_year_selector_frame);
        ?? r8 = inflate;
        if (this.yearSelector != null) {
            r8 = 1;
            this.yearSelector.setHasFixedSize(true);
            this.yearSelector.setLayoutManager(new GridLayoutManager((Context) r02, integer, 1, false));
            this.yearSelector.setAdapter(new YearGridAdapter(this));
            this.yearSelector.addItemDecoration(createItemDecoration());
        }
        if (r8.findViewById(R.id.month_navigation_fragment_toggle) != null) {
            addActionsToMonthNavigation(r8, monthsPagerAdapter);
        }
        if (!MaterialDatePicker.isFullscreen(r02)) {
            new PagerSnapHelper().attachToRecyclerView(this.recyclerView);
        }
        this.recyclerView.scrollToPosition(monthsPagerAdapter.getPosition(this.current));
        setUpForAccessibility();
        return r8;
    }

    private void setUpForAccessibility() {
        ViewCompat.setAccessibilityDelegate(this.recyclerView, new AccessibilityDelegateCompat() { // from class: com.google.android.material.datepicker.MaterialCalendar.4
            @Override // androidx.core.view.AccessibilityDelegateCompat
            public void onInitializeAccessibilityNodeInfo(View view, AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
                super.onInitializeAccessibilityNodeInfo(view, accessibilityNodeInfoCompat);
                accessibilityNodeInfoCompat.setScrollable(false);
            }
        });
    }

    private RecyclerView.ItemDecoration createItemDecoration() {
        return new RecyclerView.ItemDecoration() { // from class: com.google.android.material.datepicker.MaterialCalendar.5
            private final Calendar startItem = UtcDates.getUtcCalendar();
            private final Calendar endItem = UtcDates.getUtcCalendar();

            @Override // androidx.recyclerview.widget.RecyclerView.ItemDecoration
            public void onDraw(Canvas canvas, RecyclerView recyclerView, RecyclerView.State state) {
                YearGridAdapter adapter;
                int left;
                int width;
                GridLayoutManager layoutManager;
                Iterator<Pair<Long, Long>> it2;
                Pair<Long, Long> range;
                int firstHighlightPosition;
                if (!(recyclerView.getAdapter() instanceof YearGridAdapter) || !(recyclerView.getLayoutManager() instanceof GridLayoutManager)) {
                    return;
                }
                YearGridAdapter adapter2 = (YearGridAdapter) recyclerView.getAdapter();
                GridLayoutManager layoutManager2 = (GridLayoutManager) recyclerView.getLayoutManager();
                Iterator<Pair<Long, Long>> it3 = MaterialCalendar.this.dateSelector.getSelectedRanges().iterator();
                while (it3.hasNext()) {
                    Pair<Long, Long> range2 = it3.next();
                    if (range2.first != null && range2.second != null) {
                        this.startItem.setTimeInMillis(range2.first.longValue());
                        this.endItem.setTimeInMillis(range2.second.longValue());
                        int firstHighlightPosition2 = adapter2.getPositionForYear(this.startItem.get(1));
                        int lastHighlightPosition = adapter2.getPositionForYear(this.endItem.get(1));
                        View firstView = layoutManager2.findViewByPosition(firstHighlightPosition2);
                        View lastView = layoutManager2.findViewByPosition(lastHighlightPosition);
                        int firstRow = firstHighlightPosition2 / layoutManager2.getSpanCount();
                        int lastRow = lastHighlightPosition / layoutManager2.getSpanCount();
                        int row = firstRow;
                        while (row <= lastRow) {
                            int firstPositionInRow = layoutManager2.getSpanCount() * row;
                            View viewInRow = layoutManager2.findViewByPosition(firstPositionInRow);
                            if (viewInRow != null) {
                                int top = viewInRow.getTop() + MaterialCalendar.this.calendarStyle.year.getTopInset();
                                adapter = adapter2;
                                int bottom = viewInRow.getBottom() - MaterialCalendar.this.calendarStyle.year.getBottomInset();
                                if (row == firstRow && firstView != null) {
                                    left = firstView.getLeft() + (firstView.getWidth() / 2);
                                } else {
                                    left = 0;
                                }
                                if (row == lastRow && lastView != null) {
                                    width = lastView.getLeft() + (lastView.getWidth() / 2);
                                } else {
                                    width = recyclerView.getWidth();
                                }
                                int right = width;
                                layoutManager = layoutManager2;
                                it2 = it3;
                                range = range2;
                                firstHighlightPosition = firstHighlightPosition2;
                                canvas.drawRect(left, top, right, bottom, MaterialCalendar.this.calendarStyle.rangeFill);
                            } else {
                                adapter = adapter2;
                                layoutManager = layoutManager2;
                                it2 = it3;
                                range = range2;
                                firstHighlightPosition = firstHighlightPosition2;
                            }
                            row++;
                            adapter2 = adapter;
                            layoutManager2 = layoutManager;
                            range2 = range;
                            it3 = it2;
                            firstHighlightPosition2 = firstHighlightPosition;
                        }
                    }
                }
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Month getCurrentMonth() {
        return this.current;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public CalendarConstraints getCalendarConstraints() {
        return this.calendarConstraints;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setCurrentMonth(Month moveTo) {
        MonthsPagerAdapter adapter = (MonthsPagerAdapter) this.recyclerView.getAdapter();
        int moveToPosition = adapter.getPosition(moveTo);
        int distance = moveToPosition - adapter.getPosition(this.current);
        boolean jump = Math.abs(distance) > 3;
        boolean isForward = distance > 0;
        this.current = moveTo;
        if (jump && isForward) {
            this.recyclerView.scrollToPosition(moveToPosition - 3);
            postSmoothRecyclerViewScroll(moveToPosition);
        } else if (jump) {
            this.recyclerView.scrollToPosition(moveToPosition + 3);
            postSmoothRecyclerViewScroll(moveToPosition);
        } else {
            postSmoothRecyclerViewScroll(moveToPosition);
        }
    }

    @Override // com.google.android.material.datepicker.PickerFragment
    public DateSelector<S> getDateSelector() {
        return this.dateSelector;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public CalendarStyle getCalendarStyle() {
        return this.calendarStyle;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int getDayHeight(Context context) {
        return context.getResources().getDimensionPixelSize(R.dimen.mtrl_calendar_day_height);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setSelector(CalendarSelector selector) {
        this.calendarSelector = selector;
        if (selector == CalendarSelector.YEAR) {
            this.yearSelector.getLayoutManager().scrollToPosition(((YearGridAdapter) this.yearSelector.getAdapter()).getPositionForYear(this.current.year));
            this.yearFrame.setVisibility(0);
            this.dayFrame.setVisibility(8);
            this.monthPrev.setVisibility(8);
            this.monthNext.setVisibility(8);
            return;
        }
        if (selector == CalendarSelector.DAY) {
            this.yearFrame.setVisibility(8);
            this.dayFrame.setVisibility(0);
            this.monthPrev.setVisibility(0);
            this.monthNext.setVisibility(0);
            setCurrentMonth(this.current);
        }
    }

    void toggleVisibleSelector() {
        if (this.calendarSelector == CalendarSelector.YEAR) {
            setSelector(CalendarSelector.DAY);
        } else if (this.calendarSelector == CalendarSelector.DAY) {
            setSelector(CalendarSelector.YEAR);
        }
    }

    private void addActionsToMonthNavigation(View root, final MonthsPagerAdapter monthsPagerAdapter) {
        final MaterialButton monthDropSelect = (MaterialButton) root.findViewById(R.id.month_navigation_fragment_toggle);
        monthDropSelect.setTag(SELECTOR_TOGGLE_TAG);
        ViewCompat.setAccessibilityDelegate(monthDropSelect, new AccessibilityDelegateCompat() { // from class: com.google.android.material.datepicker.MaterialCalendar.6
            @Override // androidx.core.view.AccessibilityDelegateCompat
            public void onInitializeAccessibilityNodeInfo(View view, AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
                String string;
                super.onInitializeAccessibilityNodeInfo(view, accessibilityNodeInfoCompat);
                if (MaterialCalendar.this.dayFrame.getVisibility() == 0) {
                    string = MaterialCalendar.this.getString(R.string.mtrl_picker_toggle_to_year_selection);
                } else {
                    string = MaterialCalendar.this.getString(R.string.mtrl_picker_toggle_to_day_selection);
                }
                accessibilityNodeInfoCompat.setHintText(string);
            }
        });
        this.monthPrev = root.findViewById(R.id.month_navigation_previous);
        this.monthPrev.setTag(NAVIGATION_PREV_TAG);
        this.monthNext = root.findViewById(R.id.month_navigation_next);
        this.monthNext.setTag(NAVIGATION_NEXT_TAG);
        this.yearFrame = root.findViewById(R.id.mtrl_calendar_year_selector_frame);
        this.dayFrame = root.findViewById(R.id.mtrl_calendar_day_selector_frame);
        setSelector(CalendarSelector.DAY);
        monthDropSelect.setText(this.current.getLongName());
        this.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() { // from class: com.google.android.material.datepicker.MaterialCalendar.7
            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int currentItem;
                if (dx < 0) {
                    currentItem = MaterialCalendar.this.getLayoutManager().findFirstVisibleItemPosition();
                } else {
                    currentItem = MaterialCalendar.this.getLayoutManager().findLastVisibleItemPosition();
                }
                MaterialCalendar.this.current = monthsPagerAdapter.getPageMonth(currentItem);
                monthDropSelect.setText(monthsPagerAdapter.getPageTitle(currentItem));
            }

            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == 0) {
                    CharSequence announcementText = monthDropSelect.getText();
                    recyclerView.announceForAccessibility(announcementText);
                }
            }
        });
        monthDropSelect.setOnClickListener(new View.OnClickListener() { // from class: com.google.android.material.datepicker.MaterialCalendar.8
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                MaterialCalendar.this.toggleVisibleSelector();
            }
        });
        this.monthNext.setOnClickListener(new View.OnClickListener() { // from class: com.google.android.material.datepicker.MaterialCalendar.9
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                int currentItem = MaterialCalendar.this.getLayoutManager().findFirstVisibleItemPosition();
                if (currentItem + 1 < MaterialCalendar.this.recyclerView.getAdapter().getItemCount()) {
                    MaterialCalendar.this.setCurrentMonth(monthsPagerAdapter.getPageMonth(currentItem + 1));
                }
            }
        });
        this.monthPrev.setOnClickListener(new View.OnClickListener() { // from class: com.google.android.material.datepicker.MaterialCalendar.10
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                int currentItem = MaterialCalendar.this.getLayoutManager().findLastVisibleItemPosition();
                if (currentItem - 1 >= 0) {
                    MaterialCalendar.this.setCurrentMonth(monthsPagerAdapter.getPageMonth(currentItem - 1));
                }
            }
        });
    }

    private void postSmoothRecyclerViewScroll(final int position) {
        this.recyclerView.post(new Runnable() { // from class: com.google.android.material.datepicker.MaterialCalendar.11
            @Override // java.lang.Runnable
            public void run() {
                MaterialCalendar.this.recyclerView.smoothScrollToPosition(position);
            }
        });
    }

    private static int getDialogPickerHeight(Context context) {
        Resources resources = context.getResources();
        int navigationHeight = resources.getDimensionPixelSize(R.dimen.mtrl_calendar_navigation_height) + resources.getDimensionPixelOffset(R.dimen.mtrl_calendar_navigation_top_padding) + resources.getDimensionPixelOffset(R.dimen.mtrl_calendar_navigation_bottom_padding);
        int daysOfWeekHeight = resources.getDimensionPixelSize(R.dimen.mtrl_calendar_days_of_week_height);
        int calendarHeight = (MonthAdapter.MAXIMUM_WEEKS * resources.getDimensionPixelSize(R.dimen.mtrl_calendar_day_height)) + ((MonthAdapter.MAXIMUM_WEEKS - 1) * resources.getDimensionPixelOffset(R.dimen.mtrl_calendar_month_vertical_padding));
        int calendarPadding = resources.getDimensionPixelOffset(R.dimen.mtrl_calendar_bottom_padding);
        return navigationHeight + daysOfWeekHeight + calendarHeight + calendarPadding;
    }

    LinearLayoutManager getLayoutManager() {
        return (LinearLayoutManager) this.recyclerView.getLayoutManager();
    }

    @Override // com.google.android.material.datepicker.PickerFragment
    public boolean addOnSelectionChangedListener(OnSelectionChangedListener<S> listener) {
        return super.addOnSelectionChangedListener(listener);
    }
}
