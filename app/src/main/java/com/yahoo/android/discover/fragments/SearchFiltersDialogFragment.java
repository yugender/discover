package com.yahoo.android.discover.fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.yahoo.android.discover.R;
import com.yahoo.android.discover.models.SearchFilters;

import org.parceler.Parcels;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by yboini on 11/16/16.
 */

public class SearchFiltersDialogFragment extends DialogFragment implements View.OnClickListener,
        DatePickerDialog.OnDateSetListener {
    private static String FILTER_NEWS_DESK_ARTS = "Arts";
    private static String FILTER_NEWS_DESK_FASHION = "Fashion";
    private static String FILTER_NEWS_DESK_SPORTS = "Sports";

    boolean isArtsChecked;
    boolean isFashionChecked;
    boolean isSportsChecked;
    private SearchFilters mFilters;
    Spinner spinner;
    EditText etDate;
    public SearchFiltersDialogFragment() {

    }

    public static SearchFiltersDialogFragment newInstance(SearchFilters filters) {
        SearchFiltersDialogFragment frag = new SearchFiltersDialogFragment();
        // Store this filters object inside a bundle to be accessed later
        Bundle args = new Bundle();
        args.putParcelable("filters", Parcels.wrap(filters));
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_filters, container);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Store the filters to a member variable
        mFilters = (SearchFilters) Parcels.unwrap(getArguments().getParcelable("filters"));
        // Get access to the button
        Button btnSave =(Button) view.findViewById(R.id.btnSearchFiltersSave);
        btnSave.setOnClickListener(this);
        CheckBox checkArts = (CheckBox) view.findViewById(R.id.chbArts);
        CheckBox checkFashion = (CheckBox) view.findViewById(R.id.chbFashion);
        CheckBox checkSports = (CheckBox) view.findViewById(R.id.chbSports);
        checkArts.setOnCheckedChangeListener(checkListener);
        checkFashion.setOnCheckedChangeListener(checkListener);
        checkSports.setOnCheckedChangeListener(checkListener);
        spinner = (Spinner) view.findViewById(R.id.spnSortOrder);
        etDate = (EditText) view.findViewById(R.id.etBeginDate);
        if (mFilters.getBeginDate() != null) {
            etDate.setText(mFilters.getBeginDate());
        }
        if (mFilters.getSortOrder() != null) {
            spinner.setSelection(mFilters.getSortOrder().ordinal());
        }
        List<String> newsDeskValues = mFilters.getNewsDesks();
        if (newsDeskValues != null) {
            for (String newsDesk: newsDeskValues) {
                if (FILTER_NEWS_DESK_ARTS.equals(newsDesk)) {
                    checkArts.setChecked(true);
                } else if (FILTER_NEWS_DESK_FASHION.equals(newsDesk)) {
                    checkFashion.setChecked(true);
                } else if (FILTER_NEWS_DESK_SPORTS.equals(newsDesk)) {
                    checkSports.setChecked(true);
                }
            }
        }
        etDate.setOnClickListener(this::showDatePickerDialog);
    }

    @Override
    public void onClick(View view) {
        // Update the mFilters attribute values based on the input views
        mFilters.setSortOrder(spinner.getSelectedItem().toString());
        List<String> newsDeskValues = new ArrayList<>(3);
        if (isArtsChecked) {
            newsDeskValues.add(FILTER_NEWS_DESK_ARTS);
        }
        if (isFashionChecked) {
            newsDeskValues.add(FILTER_NEWS_DESK_FASHION);
        }
        if (isSportsChecked) {
            newsDeskValues.add(FILTER_NEWS_DESK_SPORTS);
        }
        //newsDeskValues.retainAll(newsDeskValuesNew);
        mFilters.setNewsDesks(newsDeskValues);
        if (etDate != null && etDate.getText() != null) {
            mFilters.setBeginDate(etDate.getText().toString());
        }
        // Return filters back to activity through the implemented listener
        OnFilterSearchListener listener = (OnFilterSearchListener) getActivity();
        listener.onUpdateFilters(mFilters);
        // Close the dialog to return back to the parent activity
        dismiss();
    }

    public void showDatePickerDialog(View v) {
        DatePickerFragment newFragment = new DatePickerFragment();
        newFragment.setTargetFragment(this, 300);
        newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
        final Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        // Get the beginDate here from the calendar parsed to correct format
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        etDate.setText(format.format(c.getTime()));
    }

    public interface OnFilterSearchListener {
        void onUpdateFilters(SearchFilters filters);
    }

    CompoundButton.OnCheckedChangeListener checkListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton view, boolean checked) {
            // compoundButton is the checkbox
            // boolean is whether or not checkbox is checked
            // Check which checkbox was clicked
            switch(view.getId()) {
                case R.id.chbArts:
                    if (checked) {
                        isArtsChecked = true;
                    } else {
                        isArtsChecked = false;
                    }
                    break;
                case R.id.chbFashion:
                    if (checked) {
                        isFashionChecked = true;
                    } else {
                        isFashionChecked = false;
                    }
                    break;
                case R.id.chbSports:
                    if (checked) {
                        isSportsChecked = true;
                    } else {
                        isSportsChecked = false;
                    }
                    break;
            }
        }
    };
}
