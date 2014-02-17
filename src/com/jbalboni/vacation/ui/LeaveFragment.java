package com.jbalboni.vacation.ui;

import org.joda.time.IllegalFieldValueException;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.actionbarsherlock.app.SherlockFragment;
import com.jbalboni.vacation.LeaveCategory;
import com.jbalboni.vacation.LeaveStateManager;
import com.jbalboni.vacation.R;
import com.jbalboni.vacation.VacationTracker;
import com.jbalboni.vacation.data.LeaveHistoryProvider;
import com.jbalboni.vacation.data.LeaveHistoryTable;
import com.jbalboni.vacation.data.LeaveTrackerDatabase;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

//yep, this is pretty bad
@SuppressLint("ValidFragment")
public class LeaveFragment extends SherlockFragment {
	private VacationTracker vacationTracker;
	private LocalDate asOfDate = new LocalDate();
	private LeaveCategory leaveCategory;

	private Button asOfDatePicker;
	private Button useHoursPicker;
	static final int DATE_DIALOG_ID = 0;
	static final int HOURS_DIALOG_ID = 1;
	static final String HOURS_IN_DAY = "8";
	private static DateTimeFormatter fmt = DateTimeFormat.forPattern("MMM dd, yyyy");

	private SharedPreferences prefs;

	public static LeaveFragment newInstance(int position) {
		LeaveFragment f = new LeaveFragment();

		Bundle args = new Bundle();
		// R.string.leave_category_position
		args.putInt("pos", position);
		f.setArguments(args);

		return f;
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		ContentResolver content = getActivity().getContentResolver();
		// leaveCategory = LeaveCategory.getCategoryByPosition(getArguments() !=
		// null ? getArguments().getInt(
		// getString(R.string.leave_category_position)) : null);
		vacationTracker = LeaveStateManager.createVacationTracker(prefs, content,
				getArguments().getInt(getString(R.string.leave_category_position)));
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View leaveFrag = inflater.inflate(R.layout.leave_fragment, container, false);

		TextView hoursAvailable = (TextView) leaveFrag.findViewById(R.id.hoursAvailable);
		hoursAvailable.setText(String.format("%s %.2f", getString(R.string.hours_avail),
				vacationTracker.calculateHours(asOfDate)));

		asOfDatePicker = (Button) leaveFrag.findViewById(R.id.changeAsOfDate);
		asOfDatePicker.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				DialogFragment datePickerDialog = new DatePickerDialogFragment();
				datePickerDialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.VacationDialog);
				datePickerDialog.show(getFragmentManager(), "date");
			}
		});

		useHoursPicker = (Button) leaveFrag.findViewById(R.id.useHours);
		useHoursPicker.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				UseHoursDialogFragment useHoursDialog = new UseHoursDialogFragment();
				useHoursDialog.setCategory(getArguments().getInt(getString(R.string.leave_category_position)));
				useHoursDialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.VacationDialog);
				useHoursDialog.show(getFragmentManager(), "hours");
			}
		});
		return leaveFrag;
	}

	public class UseHoursDialogFragment extends DialogFragment implements OnClickListener {

		private int catID;
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

			getDialog().setTitle(getString(R.string.quick_use_hours));

			View view = inflater.inflate(R.layout.use_hours_fragment, container);
			EditText hoursUsedInput = (EditText) view.findViewById(R.id.hours);
			hoursUsedInput.setText(HOURS_IN_DAY);

			view.findViewById(R.id.cancel).setOnClickListener(this);
			view.findViewById(R.id.addHours).setOnClickListener(this);

			return view;
		}
		
		public void setCategory(int catID) {
			this.catID = catID;
		}

		@Override
		public void onClick(View v) {
			Button clickedButton = (Button) v;
			if (clickedButton.getId() == R.id.cancel) {
				getDialog().dismiss();
			} else if (clickedButton.getId() == R.id.addHours) {
				ContentResolver content = getActivity().getContentResolver();
				ContentValues values = new ContentValues();
				values.put(LeaveHistoryTable.NUMBER.toString(), LeaveTrackerDatabase
						.getFloat(((EditText) getView().findViewById(R.id.hours)).getText().toString()));
				values.put(LeaveHistoryTable.NOTES.toString(), ((EditText) getView().findViewById(R.id.notes))
						.getText().toString());
				values.put(LeaveHistoryTable.DATE.toString(), (new LocalDate()).toString());
				values.put(LeaveHistoryTable.CATEGORY.toString(), catID);
				if (content.insert(LeaveHistoryProvider.CONTENT_URI, values) != null) {
					Toast.makeText(getActivity(), R.string.added_msg, Toast.LENGTH_LONG).show();
				}
				getDialog().dismiss();
				updateFragment();
			}
		}
	}

	public class DatePickerDialogFragment extends DialogFragment {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			return new DatePickerDialog(getActivity(), mDateSetListener, asOfDate.getYear(),
					asOfDate.getMonthOfYear() - 1, asOfDate.getDayOfMonth());
		}
	}

	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			try {
				setAsOfDate(new LocalDate(year, monthOfYear + 1, dayOfMonth));
			} catch (IllegalFieldValueException e) {
				Toast.makeText(getActivity(), R.string.invalid_date, Toast.LENGTH_SHORT).show();
			}
			updateDisplay();
		}
	};

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

	}

	public void addHoursUsed(float hoursUsed) {
		vacationTracker.addHoursUsed(hoursUsed);
	}

	public void setAsOfDate(LocalDate asOfDate) {
		this.asOfDate = asOfDate;
	}

	@Override
	public void onResume() {
		super.onResume();

		updateFragment();
	}

	public void updateFragment() {
		vacationTracker = LeaveStateManager.createVacationTracker(
				PreferenceManager.getDefaultSharedPreferences(getActivity()), getActivity().getContentResolver(),
				getArguments().getInt(getString(R.string.leave_category_position)));

		updateDisplay();
	}

	public void updateDisplay() {
		TextView hoursAvailable = (TextView) getView().findViewById(R.id.hoursAvailable);
		hoursAvailable.setText(String.format("%.2f", vacationTracker.calculateHours(asOfDate)));
		TextView asOfDateTextView = (TextView) getView().findViewById(R.id.asOfDateDesc);
		if (asOfDate.compareTo(new LocalDate()) != 0) {
			asOfDateTextView.setText(" " + fmt.print(asOfDate));
		} else {
			asOfDateTextView.setText(" " + getString(R.string.default_as_of_date));
		}
	}

	public static Intent createIntent(Context context) {
		Intent i = new Intent(context, LeaveTrackerActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		return i;
	}

	public String getLeavePrefix() {
		return leaveCategory.getPrefix();
	}
}
