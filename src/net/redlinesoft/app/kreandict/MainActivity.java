package net.redlinesoft.app.kreandict;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.ads.*;

public class MainActivity extends Activity {

	private AdView adView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Create the adView
		adView = new AdView(this, AdSize.BANNER, "a15082bc5732b54");
		// Lookup your LinearLayout assuming it’s been given
		// the attribute android:id="@+id/mainLayout"
		LinearLayout layout = (LinearLayout) findViewById(R.id.mainLayout);
		// Add the adView to it
		layout.addView(adView);
		// Initiate a generic request to load it with an ad
		adView.loadAd(new AdRequest());

		final DatabaseHandler myDb = new DatabaseHandler(this);

		// alert dialog
		final AlertDialog.Builder dDialog = new AlertDialog.Builder(this);

		// write database if not exist
		myDb.getWritableDatabase();

		if (myDb.getTotalRow() <= 0) {
			try {
				replaceDB();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// AutoCompleteText
		final String[] myData = myDb.SelectAllData();
		// Sort
		Arrays.sort(myData);
		final AutoCompleteTextView autoCom = (AutoCompleteTextView) findViewById(R.id.autoCompleteText);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_dropdown_item_1line,myData);
		autoCom.setAdapter(adapter);

		// searchButton
		final Button searchButton = (Button) findViewById(R.id.buttonSearch);
		searchButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (isNotNullNotEmptyNotWhiteSpaceOnlyByJava(autoCom.getText()
						.toString())) {
					String arrData[] = myDb.SearchWord(autoCom.getText()
							.toString());
					if (arrData != null) {
						TextView titleText = (TextView) findViewById(R.id.textMeaningTitle);
						titleText.setText(arrData[1] + " "
								+ getString(R.string.result_title));
						TextView textMeaning = (TextView) findViewById(R.id.textMeaningText);
						textMeaning.setText(arrData[2]);
					} else {
						dDialog.setTitle(R.string.text_alert);
						dDialog.setIcon(android.R.drawable.ic_dialog_alert);
						dDialog.setMessage(R.string.text_data_notfound);
						dDialog.setPositiveButton(R.string.button_close, null);
						dDialog.show();
						clearTextResult();
					}

				} else {
					clearTextResult();
				}
			}
		});

	}

	public static boolean isNotNullNotEmptyNotWhiteSpaceOnlyByJava(
			final String string) {
		return string != null && !string.isEmpty() && !string.trim().isEmpty();
	}

	public void clearTextResult() {
		TextView titleText = (TextView) findViewById(R.id.textMeaningTitle);
		titleText.setText("");
		TextView textMeaning = (TextView) findViewById(R.id.textMeaningText);
		textMeaning.setText("");
	}

	public void replaceDB() throws Exception {
		String FICHIER_BLOW = "teenword.db";
		File f = new File(getDataDir().toString(), FICHIER_BLOW);

		BufferedOutputStream bufEcrivain = new BufferedOutputStream(
				(new FileOutputStream(f)));
		BufferedInputStream VideoReader = new BufferedInputStream(
				getResources().openRawResource(R.raw.teenword));
		byte[] buff = new byte[32 * 1024];
		int len;
		while ((len = VideoReader.read(buff)) > 0) {
			bufEcrivain.write(buff, 0, len);
			Log.d("FILE", "move database to external storage");
		}
		bufEcrivain.flush();
		bufEcrivain.close();
	}

	public String getDataDir() {
		return this.getExternalFilesDir(null).toString();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub

		switch (item.getItemId()) {
		case R.id.menu_updatedb:
			Log.d("MENU", "select menu update");
			Intent newActivity = new Intent(MainActivity.this,
					UpdateActivity.class);
			startActivity(newActivity);
			break;
		case R.id.menu_settings:
			Log.d("MENU", "select menu setting");
			Intent newActivity2 = new Intent(MainActivity.this,
					AboutActivity.class);
			startActivity(newActivity2);
			break;

		}
		return false;

	}

}
