package com.kelviomatias.emulators;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.kelviomatias.emulators.model.Console;
import com.kelviomatias.emulators.model.Emulator;
import com.kelviomatias.emulators.util.ActivityUtil;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.utils.StorageUtils;

public class EmulatorsActivity extends Activity {

	private Console console;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_emulators);

		final Button downloadButton = (Button) findViewById(R.id.activityEmulatorsDownloadRomsAppButton);

		if (ActivityUtil.appInstalledOrNot(EmulatorsActivity.this,
				"com.kelviomatias.romspremium")) {

			downloadButton.setText(getString(R.string.open_roms_app));
			
			downloadButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					startActivity(ActivityUtil.getLaunchIntentForPackage(
							EmulatorsActivity.this,
							"com.kelviomatias.romspremium"));
					

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

		console = (Console) this.getIntent().getSerializableExtra(
				Console.class.getName());

		this.setTitle(console.getName());

		this.fillEmulatorList();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.emulators, menu);
		return true;
	}

	private void fillEmulatorList() {

		Emulator[] emulatorsArray = new Emulator[console.getEmulators().size()];
		for (int i = 0; i < emulatorsArray.length; i++) {
			emulatorsArray[i] = console.getEmulators().get(i);
		}
		EmulatorItemAdapter itemAdapter = new EmulatorItemAdapter(
				EmulatorsActivity.this, R.layout.emulators_list_item,
				emulatorsArray);

		ListView listView = (ListView) findViewById(R.id.activityEmulatorsListView);
		listView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long id) {
				Intent i = new Intent();
				i.setClass(EmulatorsActivity.this, EmulatorActivity.class);
				i.putExtra(Emulator.class.getName(), console.getEmulators()
						.get(position));
				startActivity(i);

			}
		});
		listView.setAdapter(itemAdapter);

	}

	class EmulatorItemAdapter extends ArrayAdapter<Emulator> {

		private Activity myContext;

		private Emulator[] datas;

		public EmulatorItemAdapter(Context context, int textViewResourceId,
				Emulator[] objects) {
			super(context, textViewResourceId, objects);

			myContext = (Activity) context;
			datas = objects;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			LayoutInflater inflater = myContext.getLayoutInflater();

			if (convertView == null) {
				convertView = inflater.inflate(R.layout.emulators_list_item,
						null);
			}

			ImageLoader imageLoader = ImageLoader.getInstance();

			ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
					EmulatorsActivity.this)
					// You can pass your own memory cache
					// implementation
					.discCache(
							new UnlimitedDiscCache(StorageUtils
									.getOwnCacheDirectory(
											EmulatorsActivity.this,
											datas[position].getId())))
					// You can pass
					// your own disc
					// cache
					// implementation
					.discCacheFileNameGenerator(new HashCodeFileNameGenerator())
					.build();
			// Initialize ImageLoader with created
			// configuration. Do it
			// once.
			imageLoader.init(config);
			DisplayImageOptions options = new DisplayImageOptions.Builder()
					.showStubImage(R.drawable.ic_launcher)
					// display stub image
					.cacheInMemory(true).cacheOnDisc(true)
					.displayer(new RoundedBitmapDisplayer(20)).build();

			imageLoader
					.displayImage(
							datas[position].getIcon(),
							((ImageView) convertView
									.findViewById(R.id.emulatorsListItememulatorsIcon)),
							options);

			TextView infoView = (TextView) convertView
					.findViewById(R.id.consoleListItemRowListItemInfoText);

			TextView nomeCanalView = (TextView) convertView
					.findViewById(R.id.emulatorsListItemTextView);

			nomeCanalView.setText((position + 1) + ". "
					+ datas[position].getName());

			// emulatorListItemrowListItemInfoText

			return convertView;
		}

		private Bitmap getBitmapForCharacter(Character c) {

			Resources r = getResources();
			int w = (int) TypedValue.applyDimension(
					TypedValue.COMPLEX_UNIT_DIP, 100, r.getDisplayMetrics());
			int h = (int) TypedValue.applyDimension(
					TypedValue.COMPLEX_UNIT_DIP, 100, r.getDisplayMetrics());

			Bitmap b = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);

			return b;

		}

	}

}
