package com.jbalboni.vacation.widget;

import com.jbalboni.vacation.R;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.widget.RemoteViews;

public class LeaveWidgetProvider extends AppWidgetProvider {
	
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		final int N = appWidgetIds.length;

        for (int i=0; i<N; i++) {
            int appWidgetId = appWidgetIds[i];

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);

            views.setTextViewText(R.id.hoursAvailable, "32.0");
            views.setTextViewText(R.id.categoryLabel, "Vacation");
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
	}

}
