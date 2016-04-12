package com.kingwaytek.cpami.bykingTablet.app.track;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.kingwaytek.cpami.bykingTablet.R;
import com.kingwaytek.cpami.bykingTablet.app.FileUtil;
import com.kingwaytek.cpami.bykingTablet.app.track.TrackContent.TrackExportExt;
import com.kingwaytek.cpami.bykingTablet.sql.Track;
import com.kingwaytek.cpami.bykingTablet.sql.TrackPoint;

public class CsvWriter {

	static final SimpleDateFormat TIMESTAMP_FORMAT = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss'Z'");
	static {
		TIMESTAMP_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
	}

	private final Context context;
	private final FileUtil fileUtils = new FileUtil();
	private int segmentIdx = 0;
	private int numFields = -1;
	private PrintWriter pw;
	private Track track;
	private Runnable onCompletion;
	private File directory;
	private File file;

	private boolean success;
	private int errorMessage;

	// TODO: setup outputstream to pw.
	public CsvWriter(Context context, Track track) {
		this.context = context;
		this.track = track;
		pw = null;
		onCompletion = null;
		directory = null;
		file = null;
		success = false;
		errorMessage = -1;
	}

	/**
	 * Sets a completion callback.
	 * 
	 * @param onCompletion
	 *            Runnable that will be executed when finished
	 */
	public void setOnCompletion(Runnable onCompletion) {
		this.onCompletion = onCompletion;
	}

	public boolean wasSuccess() {
		return this.success;
	}

	public int getErrorMsg() {
		return this.errorMessage;
	}

	/**
	 * Writes the given track id to the SD card. This is non-blocking.
	 */
	public void writeTrackAsync() {
		Thread t = new Thread() {
			@Override
			public void run() {
				writeTrack();
			}
		};
		t.start();
	}

	/**
	 * Writes the given track id to the SD card. This is blocking.
	 */
	public void writeTrack() {
		// Open the input and output
		success = false;
		errorMessage = R.string.error_track_does_not_exist;
		if (track != null) {
			if (openFile()) {
				writeCsv();
			}
		}
		finished();
	}

	private void finished() {
		if (onCompletion != null) {
			runOnUiThread(onCompletion);
			return;
		}
	}

	/**
	 * Runs the given runnable in the UI thread.
	 */
	protected void runOnUiThread(Runnable runnable) {
		if (context instanceof Activity) {
			((Activity) context).runOnUiThread(runnable);
		}
	}

	/**
	 * Writes csv header. google myTrack example.
	 */
	public void writeHeader() {
		writeCommaSeparatedLine("TYPE", "TIME", "LAT", "LON", "ALT", "BEARING",
				"ACCURACY", "SPEED", "NAME", "DESCRIPTION", "SEGMENT");
	}

	/**
	 * Writes csv first record. google myTrack example.
	 */
	public void writeBeginTrack(Location firstPoint) {
		writeCommaSeparatedLine("TRACK", null, null, null, null, null, null,
				null, track.getName(), track.getDescription(), "");
	}

	public void writeOpenSegment() {
		// Do nothing
	}

	/**
	 * Writes csv record. google myTrack example.
	 * 
	 * @param location
	 */
	public void writeLocation(Location location) {
		String timeStr = TIMESTAMP_FORMAT.format(new Date(location.getTime()));
		writeCommaSeparatedLine("P", timeStr, Double.toString(location
				.getLatitude()), Double.toString(location.getLongitude()),
				Double.toString(location.getAltitude()), Double
						.toString(location.getBearing()), Double
						.toString(location.getAccuracy()), Double
						.toString(location.getSpeed()), null, null, Integer
						.toString(segmentIdx));
	}

	public void writeCloseSegment() {
		segmentIdx++;
	}

	public void writeEndTrack(Location lastPoint) {
		// Do nothing
	}

	public void writeFooter() {
		// Do nothing
	}

	/**
	 * Writes a single line of a comma-separated-value file. google myTrack
	 * example.
	 * 
	 * @param strs
	 *            the values to be written as comma-separated
	 */
	private void writeCommaSeparatedLine(String... strs) {
		if (numFields == -1) {
			numFields = strs.length;
		} else if (strs.length != numFields) {
			throw new IllegalArgumentException(
					"CSV lines with different number of fields");
		}

		boolean isFirst = true;
		for (String str : strs) {
			if (!isFirst) {
				pw.print(',');
			}
			isFirst = false;

			if (str != null) {
				pw.print('"');
				pw.print(str.replaceAll("\"", "\"\""));
				pw.print('"');
			}
		}
		pw.println();
	}

	public void close() {
		pw.close();
	}

	/**
	 * File Processing
	 */

	/**
	 * Opens the file and prepares the format writer for it.
	 * 
	 * @return true on success, false otherwise (and errorMessage is set)
	 */
	protected boolean openFile() {
		if (!canWriteFile()) {
			return false;
		}

		// Make sure the file doesn't exist yet (possibly by changing the
		// filename)
		Log.i("CsvWriter", "directory:" + directory.toString()
				+ ", track name:" + track.getName());
		String fileName = fileUtils.buildFileName(directory, track.getName(),
				TrackExportExt.CSV.getExtension());
		if (fileName == null) {
			Log.e("CsvWriter", "Unable to get a unique filename for "
					+ fileName);
			return false;
		}

		Log.i("CsvWriter", "Writing track to: " + fileName);
		try {
			pw = new PrintWriter(newOutputStream(fileName));
			// writer.prepare(track, newOutputStream(fileName));
		} catch (FileNotFoundException e) {
			Log.e("CsvWriter", "Failed to open output file.", e);
			errorMessage = R.string.io_write_failed;
			return false;
		}
		return true;
	}

	/**
	 * Checks and returns whether we're ready to create the output file.
	 */
	protected boolean canWriteFile() {
		if (directory == null) {
			String dirName = context.getString(R.string.Track_Export_Path);
			directory = newFile(dirName);
		}

		if (!fileUtils.isSdCardPresent()) {
			Log.i("CsvWriter", "Could not find SD card.");
			errorMessage = R.string.io_no_external_storage_found;
			return false;
		}
		if (!fileUtils.isDirectoryExists(directory)) {
			Log.i("CsvWriter", "Could not create export directory.");
			errorMessage = R.string.io_create_dir_failed;
			return false;
		}

		return true;
	}

	/**
	 * Creates a new output stream to write to the given filename.
	 * 
	 * @throws FileNotFoundException
	 *             if the file could't be created
	 */
	protected OutputStream newOutputStream(String fileName)
			throws FileNotFoundException {
		file = new File(directory, fileName);
		return new FileOutputStream(file);
	}

	/**
	 * Creates a new file object for the given path.
	 */
	protected File newFile(String path) {
		return new File(path);
	}

	private Location createLocation(TrackPoint tp) {
		Location location = new Location("");
		location.setLatitude(tp.getLatitude());
		location.setLongitude(tp.getLongitude());
		location.setAltitude(tp.getAltitude());
		location.setTime(tp.getDate().getTime());
		location.setBearing(0.0f);
		location.setSpeed(0.0f);
		location.setAccuracy(0.0f);
		// if (!cursor.isNull(idxLatitude)) {
		// location.setLatitude(1. * cursor.getInt(idxLatitude) / 1E6);
		// }
		// if (!cursor.isNull(idxLongitude)) {
		// location.setLongitude(1. * cursor.getInt(idxLongitude) / 1E6);
		// }

		return location;
	}

	/**
	 * Does the actual work of writing the track to the now open file.
	 */
	private void writeCsv() {
		Log.d("CsvWriter", "Started writing track.");
		writeHeader();
		// TrackBuffer buffer = new TrackBuffer(1024);
		Location last = null;
		boolean wroteFirst = false;
		boolean segmentOpen = false;
		int nValidLocations = 0;

		// Fetch small pieces of the track.
		if (!wroteFirst) {
			// useless for csv
			Location first = createLocation(track.getTrackPoints().get(0));
			writeBeginTrack(first);
			wroteFirst = true;
		}

		for (TrackPoint tp : track.getTrackPoints().values()) {
			Location location = createLocation(tp);
			nValidLocations++;
			if (!segmentOpen) {
				// useless for csv
				writeOpenSegment();
				segmentOpen = true;
			}
			// main job
			writeLocation(location);

			// useless for csv
			if (nValidLocations >= 2) {
				last = location;
			}
		}
		if (segmentOpen) {
			// useless for csv
			writeCloseSegment();
			segmentOpen = false;
		}
		if (wroteFirst) {
			// useless for csv
			writeEndTrack(last);
		}
		// writeWaypoints(track.getId());
		writeFooter();
		close();
		success = true;
		Log.d("CsvWriter", "Done writing track.");
		errorMessage = R.string.io_write_finished;
	}
}
