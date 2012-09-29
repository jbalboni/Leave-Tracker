package com.jbalboni.vacation.ui;

import java.lang.ref.WeakReference;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.jbalboni.vacation.LeaveStateManager;
import com.jbalboni.vacation.R;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.view.View;
import android.widget.TextView;

/*
 * Updates the main Leave Fragment with the values from calculations in VacationTracker
 */
public class LeaveTrackerUpdaterTask extends AsyncTask<LocalDate, Void, LeaveTrackerUpdate> {

	private final WeakReference<View> leaveFragment;
	private SharedPreferences prefs;
	private ContentResolver resolver;
	private int position;
	private static DateTimeFormatter fmt = DateTimeFormat.forPattern("MMM dd, yyyy");
	private String defaultDate;

	public LeaveTrackerUpdaterTask(View fragmentView, SharedPreferences prefs, ContentResolver resolver, int position,
			Context context) {
		leaveFragment = new WeakReference<View>(fragmentView);
		this.prefs = prefs;
		this.resolver = resolver;
		this.position = position;
		this.defaultDate = context.getString(R.string.default_as_of_date);
	}

	@Override
	protected LeaveTrackerUpdate doInBackground(LocalDate... params) {
		LeaveTrackerUpdate update = new LeaveTrackerUpdate();
		update.tracker = LeaveStateManager.createVacationTracker(prefs, resolver, position);
		update.hours = update.tracker.calculateHours(params[0]);
		update.asOfDate = params[0];
		return update;
	}

	protected void onPostExecute(LeaveTrackerUpdate leaveTrackerUpdate) {
		View fragmentView = leaveFragment.get();
		TextView hoursAvailable = (TextView) fragmentView.findViewById(R.id.hoursAvailable);
		hoursAvailable.setText(String.format("%.2f", leaveTrackerUpdate.hours));
		TextView asOfDateTextView = (TextView) fragmentView.findViewById(R.id.asOfDateDesc);
		if (leaveTrackerUpdate.asOfDate.compareTo(new LocalDate()) != 0) {
			asOfDateTextView.setText(" " + fmt.print(leaveTrackerUpdate.asOfDate));
		} else {
			asOfDateTextView.setText(" " + defaultDate);
		}
	}

}
