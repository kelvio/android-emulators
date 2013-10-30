package com.kelviomatias.emulators;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.kelviomatias.emulators.model.Emulator;
import com.kelviomatias.emulators.util.ActivityUtil;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.utils.StorageUtils;

public class EmulatorActivity extends Activity {

	private Emulator emulator;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_emulator);

		this.emulator = (Emulator) this.getIntent().getSerializableExtra(
				Emulator.class.getName());

		this.setTitle(this.emulator.getName());

		((TextView) findViewById(R.id.activityEmulatorAppName))
				.setText(emulator.getName());

		((TextView) findViewById(R.id.activityAndroidDescriptionTextTextView))
				.setVisibility(View.INVISIBLE);

		Button downloadButton = (Button) findViewById(R.id.activityEmulatorDownloadButton);

		if (ActivityUtil.appInstalledOrNot(this, this.emulator.getId())) {

			downloadButton.setText(getString(R.string.open));
			startActivity(ActivityUtil.getLaunchIntentForPackage(
					EmulatorActivity.this, this.emulator.getId()));

		} else {

			downloadButton.setText(getString(R.string.download));
			downloadButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {

					try {
						startActivity(new Intent(Intent.ACTION_VIEW, Uri
								.parse("market://details?id="
										+ emulator.getId())));
					} catch (android.content.ActivityNotFoundException anfe) {
						startActivity(new Intent(
								Intent.ACTION_VIEW,
								Uri.parse("http://play.google.com/store/apps/details?id="
										+ emulator.getId())));
					}

				}
			});

		}

		// Set icon
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {

				
				
				try {
					
					if (emulator.getDescription() == null
							|| emulator.getDescription().equals("")) {

							emulator.setDescription(getAppDescriptionText(getJsoupDocument("http://play.google.com/store/apps/details?id="
										+ emulator.getId())));
						
					}

					runOnUiThread(new Runnable() {

						@Override
						public void run() {

							try {

								TextView tv = ((TextView) findViewById(R.id.activityAndroidDescriptionTextTextView));

								
								
								tv.setText(Html.fromHtml(emulator
										.getDescription()));
								tv.setVisibility(View.VISIBLE);
								((ProgressBar) findViewById(R.id.activityEmulatorDescriptionTextProgressBar))
										.setVisibility(View.INVISIBLE);

								ImageLoader imageLoader = ImageLoader
										.getInstance();

								ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
										EmulatorActivity.this)
										// You can pass your own memory cache
										// implementation
										.discCache(
												new UnlimitedDiscCache(
														StorageUtils
																.getOwnCacheDirectory(
																		EmulatorActivity.this,
																		emulator.getId())))
										// You can pass
										// your own disc
										// cache
										// implementation
										.discCacheFileNameGenerator(
												new HashCodeFileNameGenerator())
										.build();
								// Initialize ImageLoader with created
								// configuration. Do it
								// once.
								imageLoader.init(config);
								DisplayImageOptions options = new DisplayImageOptions.Builder()
										.showStubImage(R.drawable.ic_launcher)
										// display stub image
										.cacheInMemory(true)
										.cacheOnDisc(true)
										.displayer(
												new RoundedBitmapDisplayer(20))
										.build();

								imageLoader
										.displayImage(
												emulator.getIcon(),
												((ImageView) findViewById(R.id.activityEmulatorImageView)),
												options);

							} catch (Exception e) {
								e.printStackTrace();
							}

						}
					});
				} catch (Exception e) {
					e.printStackTrace();
				}

				return null;
			}

		}.execute();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.emulator, menu);
		return true;
	}

	private static Document getJsoupDocument(String url)
			throws MalformedURLException, IOException {
		return Jsoup.parse(new URL(url), 10000);
	}

	
	private static String getAppDescriptionText(Document document) {
		try {
			return document.select(".details-section .app-orig-desc").first()
					.html();
		} catch (Exception e) {
			return null;
		}
	}

}
