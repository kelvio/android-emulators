package com.kelviomatias.emulators;

import java.net.URL;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.simpleframework.xml.core.Persister;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.kelviomatias.emulators.model.Console;
import com.kelviomatias.emulators.model.Data;
import com.kelviomatias.emulators.util.ActivityUtil;

public class MainActivity extends Activity {

	private List<Console> consoles;

	private Map<Character, Integer> characterColorMap = new HashMap<Character, Integer>() {

		{
			this.put('A', Color.rgb(131, 70, 44));
			this.put('B', Color.rgb(109, 110, 113));
			this.put('C', Color.rgb(227, 155, 53));
			this.put('D', Color.rgb(0, 174, 239));
			this.put('E', Color.rgb(88, 101, 162));
			this.put('F', Color.rgb(0, 146, 165));
			this.put('G', Color.DKGRAY);
			this.put('H', Color.LTGRAY);
			this.put('I', Color.MAGENTA);
			this.put('J', Color.rgb(131, 70, 44));
			this.put('K', Color.rgb(109, 110, 113));
			this.put('L', Color.rgb(227, 155, 53));
			this.put('M', Color.rgb(88, 101, 162));
			this.put('N', Color.rgb(0, 174, 239));
			this.put('O', Color.DKGRAY);
			this.put('P', Color.LTGRAY);
			this.put('Q', Color.MAGENTA);
			this.put('R', Color.rgb(131, 70, 44));
			this.put('S', Color.MAGENTA);
			this.put('T', Color.rgb(227, 155, 53));
			this.put('U', Color.rgb(0, 174, 239));
			this.put('V', Color.rgb(0, 146, 165));
			this.put('W', Color.rgb(88, 101, 162));
			this.put('X', Color.LTGRAY);
			this.put('Y', Color.LTGRAY);
			this.put('Z', Color.rgb(131, 70, 44));
			this.put('0', Color.rgb(109, 110, 113));
			this.put('1', Color.rgb(227, 155, 53));
			this.put('2', Color.rgb(0, 174, 239));
			this.put('3', Color.rgb(0, 146, 165));
			this.put('4', Color.DKGRAY);
			this.put('5', Color.LTGRAY);
			this.put('6', Color.LTGRAY);
			this.put('7', Color.rgb(131, 70, 44));
			this.put('8', Color.rgb(109, 110, 113));
			this.put('9', Color.rgb(227, 155, 53));
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		final Button downloadButton = (Button) findViewById(R.id.activityMainDownloadRomsButtom);

		if (ActivityUtil.appInstalledOrNot(MainActivity.this,
				"com.kelviomatias.romspremium")) {

			downloadButton.setText(getString(R.string.open_roms_app));
			
			downloadButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					startActivity(ActivityUtil.getLaunchIntentForPackage(
							MainActivity.this, "com.kelviomatias.romspremium"));
					

				}
			});

		} else {

			downloadButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					try {
						startActivity(new Intent(
								Intent.ACTION_VIEW,
								Uri.parse("market://details?id=com.kelviomatias.romspremium")));
					} catch (android.content.ActivityNotFoundException anfe) {
						startActivity(new Intent(
								Intent.ACTION_VIEW,
								Uri.parse("http://play.google.com/store/apps/details?id=com.kelviomatias.romspremium")));
					}

				}
			});

		}

		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {

				try {
					URL url = new URL(
							"http://1.androidemulatorsdataserver.appspot.com/index.xml");
					List<Console> tmp = new Persister().read(Data.class,
							url.openStream()).getConsoles();

					for (int i = 0; i < tmp.size(); i++) {
						if (tmp.get(i).getEmulators().isEmpty()) {
							tmp.remove(i);
						}
					}
					consoles = tmp;
					Collections.sort(consoles, new Comparator<Console>() {

						@Override
						public int compare(Console lhs, Console rhs) {

							return lhs.getName().compareTo(rhs.getName());
						}

					});
					

					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							findViewById(R.id.activityMainProgressBar)
									.setVisibility(View.INVISIBLE);
							fillConsoleList();
						}
					});

				} catch (Exception e) {
					e.printStackTrace();
				}

				return null;
			}

		}.execute();

	}

	private void fillConsoleList() {

		Console[] consolesArray = new Console[consoles.size()];
		for (int i = 0; i < consolesArray.length; i++) {
			consolesArray[i] = consoles.get(i);
		}
		ConsoleItemAdapter itemAdapter = new ConsoleItemAdapter(
				MainActivity.this, R.layout.consoles_list_item, consolesArray);

		ListView listView = (ListView) findViewById(R.id.activityMainListView);
		listView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long id) {
				Intent i = new Intent();
				i.setClass(MainActivity.this, EmulatorsActivity.class);
				i.putExtra(Console.class.getName(), consoles.get(position));
				startActivity(i);

			}
		});
		listView.setAdapter(itemAdapter);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	class ConsoleItemAdapter extends ArrayAdapter<Console> {

		private Activity myContext;

		private Console[] datas;

		public ConsoleItemAdapter(Context context, int textViewResourceId,
				Console[] objects) {
			super(context, textViewResourceId, objects);

			myContext = (Activity) context;
			datas = objects;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			LayoutInflater inflater = myContext.getLayoutInflater();

			if (convertView == null) {
				convertView = inflater.inflate(R.layout.consoles_list_item,
						null);
			}

			// TextView infoView = (TextView) convertView
			// .findViewById(R.id.conso);

			TextView nomeCanalView = (TextView) convertView
					.findViewById(R.id.consolesListItemTextView);

			nomeCanalView.setText((position + 1) + ". "
					+ datas[position].getName());

			((ImageView) convertView
					.findViewById(R.id.consolesListItemconsolesIcon))
					.setImageBitmap(getBitmapForCharacter(datas[position]
							.getName().charAt(0)));

			((TextView) convertView
					.findViewById(R.id.consoleListItemRowListItemInfoText))
					.setText("(" + datas[position].getEmulators().size() + ")");
			// emulatorListItemrowListItemInfoText

			return convertView;
		}

		private Bitmap getBitmapForCharacter(Character c) {

			Resources r = getResources();
			int w = (int) TypedValue.applyDimension(
					TypedValue.COMPLEX_UNIT_DIP, 50, r.getDisplayMetrics());
			int h = (int) TypedValue.applyDimension(
					TypedValue.COMPLEX_UNIT_DIP, 50, r.getDisplayMetrics());

			Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);

			Canvas canvas = new Canvas(bitmap);

			String gText = "" + c;

			// new antialised Paint
			Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
			paint.setColor(getColorForCharacter(c));
			canvas.drawRect(0, 0, w, h, paint);

			float scale = 1.0f;
			paint.setColor(Color.WHITE);

			// text size in pixels
			paint.setTextSize((int) (50 * scale));
			// text shadow
			// paint.setShadowLayer(1f, 0f, 1f, Color.WHITE);

			// draw text to the Canvas center
			Rect bounds = new Rect();
			paint.getTextBounds(gText, 0, gText.length(), bounds);
			int x = (bitmap.getWidth() - bounds.width()) / 2;
			int y = (bitmap.getHeight() + bounds.height()) / 2;

			canvas.drawText(gText, x * scale, y * scale, paint);

			return bitmap;

		}

	}

	public int getColorForCharacter(Character c) {
		return this.characterColorMap.get(c);
	}

}
