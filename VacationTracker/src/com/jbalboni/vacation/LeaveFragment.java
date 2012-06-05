package com.jbalboni.vacation;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

public class LeaveFragment extends Fragment {
	private VacationTracker vacationTracker;
	private LocalDate asOfDate = new LocalDate();
	private LeaveCategory leaveCategory;

	private Button asOfDatePicker;
	private Button useHoursPicker;
	static final int DATE_DIALOG_ID = 0;
	static final int HOURS_DIALOG_ID = 1;
	static final String HOURS_IN_DAY = "8";
	DateTimeFormatter fmt = DateTimeFormat.forPattern("MM/dd/yyyy");

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
		leaveCategory = LeaveCategory.getCategoryByPosition(getArguments() != null ? getArguments().getInt(
				getString(R.string.leave_category_position)) : null);
		vacationTracker = LeaveStateManager.createVacationTracker(prefs, leaveCategory.getPrefix());
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
				datePickerDialog.show(getFragmentManager(), "date");
			}
		});

		useHoursPicker = (Button) leaveFrag.findViewById(R.id.useHours);
		useHoursPicker.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				DialogFragment useHoursDialog = new UseHoursDialogFragment();
				useHoursDialog.show(getFragmentManager(), "hours");
			}
		});
		return leaveFrag;
	}

	public class UseHoursDialogFragment extends DialogFragment {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {

			AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

			alert.setTitle(getString(R.string.quick_use_hours));
			alert.setMessage(getString(R.string.use_hours_alert_msg));

			// Set an EditText view to get user input
			final EditText hoursUsedInput = new EditText(getActivity());
			hoursUsedInput.setRawInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
			hoursUsedInput.setText(HOURS_IN_DAY);
			alert.setView(hoursUsedInput);

			alert.setPositiveButton(getString(R.string.quick_use_hours), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					Float hoursUsed = Float.parseFloat(hoursUsedInput.getText().toString());
					vacationTracker.addHoursUsed(hoursUsed);
					updateDisplay();
					LeaveStateManager.saveVacationTracker(vacationTracker, prefs, getLeavePrefix());
				}
			});

			alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					// Canceled.
				}
			});

			return alert.create();
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
			setAsOfDate(new LocalDate(year, monthOfYear + 1, dayOfMonth));
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

		vacationTracker = LeaveStateManager.createVacationTracker(
				PreferenceManager.getDefaultSharedPreferences(getActivity()), leaveCategory.getPrefix());

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
		Intent i = new Intent(context, VacationTrackerActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		return i;
	}

	public String getLeavePrefix() {
		return leaveCategory.getPrefix();
	}
}
