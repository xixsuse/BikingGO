package com.kingwaytek.cpami.bykingTablet.app.track;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
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

public class KmlWriter {

	static final SimpleDateFormat TIMESTAMP_FORMAT = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss'Z'");
	static {
		TIMESTAMP_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
	}

	private final Context context;
	private final FileUtil fileUtils = new FileUtil();
	// private int segmentIdx = 0;
	// private int numFields = -1;
	private PrintWriter pw;
	private Track track;
	private Runnable onCompletion;
	private File directory;
	private File file;

	private boolean success;
	private int errorMessage;

	// TODO: setup outputstream to pw.
	public KmlWriter(Context context, Track track) {
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
				writeKml();
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
	 * Writes kml header. google myTrack example.
	 */
	public void writeHeader() {
		if (pw != null) {
			pw.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			pw.print("<kml");
			pw.print(" xmlns=\"http://earth.google.com/kml/2.0\"");
			pw.println(" xmlns:atom=\"http://www.w3.org/2005/Atom\">");
			pw.println("<Document>");
			pw.println("<atom:author><atom:name>My Tracks running on Android"
					+ "</atom:name></atom:author>");
			pw.println("<name>" + stringAsCData(track.getName()) + "</name>");
			pw.println("<description>" + stringAsCData(track.getDescription())
					+ "</description>");
			writeStyles();
		}
	}

	/**
	 * Writes kml first record. google myTrack example.
	 */
	public void writeBeginTrack(Location firstPoint) {
		if (pw != null) {
			writePlacemark("(Start)", track.getDescription(),
					"#sh_green-circle", firstPoint);
			pw.println("<Placemark>");
			pw.println("<name>" + stringAsCData(track.getName()) + "</name>");
			pw.println("<description>" + stringAsCData(track.getDescription())
					+ "</description>");
			pw.println("<styleUrl>#track</styleUrl>");
			pw.println("<MultiGeometry>");
		}
	}

	public void writeOpenSegment() {
		if (pw != null) {
			pw.print("<LineString><coordinates>");
		}
	}

	/**
	 * Writes kml record. google myTrack example.
	 * 
	 * @param location
	 */
	public void writeLocation(Location location) {
		if (pw != null) {
			pw.print(location.getLongitude() + "," + location.getLatitude()
					+ "," + location.getAltitude() + " ");
		}
	}

	public void writeCloseSegment() {
		if (pw != null) {
			pw.println("</coordinates></LineString>");
		}
	}

	public void writeEndTrack(Location lastPoint) {
		if (pw != null) {
			pw.println("</MultiGeometry>");
			pw.println("</Placemark>");
			String description = generateTrackDescription(track);
			writePlacemark("(End)", description, "#sh_red-circle", lastPoint);
		}
	}

	public void writeFooter() {
		if (pw != null) {
			pw.println("</Document>");
			pw.println("</kml>");
		}
	}

	private void writeStyles() {
		pw.println("<Style id=\"track\"><LineStyle><color>7f0000ff</color>"
				+ "<width>4</width></LineStyle></Style>");

		pw.print("<Style id=\"sh_green-circle\"><IconStyle><scale>1.3</scale>");
		pw.print("<Icon><href>http://maps.google.com/mapfiles/kml/paddle/"
				+ "grn-circle.png</href></Icon>");
		pw
				.println("<hotSpot x=\"32\" y=\"1\" xunits=\"pixels\" yunits=\"pixels\"/>"
						+ "</IconStyle></Style>");

		pw.print("<Style id=\"sh_red-circle\"><IconStyle><scale>1.3</scale>");
		pw.print("<Icon><href>http://maps.google.com/mapfiles/kml/paddle/"
				+ "red-circle.png</href></Icon>");
		pw
				.println("<hotSpot x=\"32\" y=\"1\" xunits=\"pixels\" yunits=\"pixels\"/>"
						+ "</IconStyle></Style>");

		pw.print("<Style id=\"sh_ylw-pushpin\"><IconStyle><scale>1.3</scale>");
		pw.print("<Icon><href>http://maps.google.com/mapfiles/kml/pushpin/"
				+ "ylw-pushpin.png</href></Icon>");
		pw
				.println("<hotSpot x=\"20\" y=\"2\" xunits=\"pixels\" yunits=\"pixels\"/>"
						+ "</IconStyle></Style>");

		pw.print("<Style id=\"sh_blue-pushpin\"><IconStyle><scale>1.3</scale>");
		pw.print("<Icon><href>http://maps.google.com/mapfiles/kml/pushpin/"
				+ "blue-pushpin.png</href></Icon>");
		pw
				.println("<hotSpot x=\"20\" y=\"2\" xunits=\"pixels\" yunits=\"pixels\"/>"
						+ "</IconStyle></Style>");

		pw
				.print("<Style id=\"sh_green-pushpin\"><IconStyle><scale>1.3</scale>");
		pw.print("<Icon><href>http://maps.google.com/mapfiles/kml/pushpin/"
				+ "grn-pushpin.png</href></Icon>");
		pw
				.println("<hotSpot x=\"20\" y=\"2\" xunits=\"pixels\" yunits=\"pixels\"/>"
						+ "</IconStyle></Style>");

		pw.print("<Style id=\"sh_red-pushpin\"><IconStyle><scale>1.3</scale>");
		pw.print("<Icon><href>http://maps.google.com/mapfiles/kml/pushpin/"
				+ "red-pushpin.png</href></Icon>");
		pw
				.println("<hotSpot x=\"20\" y=\"2\" xunits=\"pixels\" yunits=\"pixels\"/>"
						+ "</IconStyle></Style>");
	}

	private void writePlacemark(String name, String description, String style,
			Location location) {
		if (location != null) {
			pw.println("<Placemark>");
			pw.println("  <name>" + stringAsCData(name) + "</name>");
			pw.println("  <description>" + stringAsCData(description)
					+ "</description>");
			pw.println("  <styleUrl>" + style + "</styleUrl>");
			pw.println("  <Point>");
			pw.println("    <coordinates>" + location.getLongitude() + ","
					+ location.getLatitude() + "</coordinates>");
			pw.println("  </Point>");
			pw.println("</Placemark>");
		}
	}

	private String stringAsCData(String unescaped) {
		// "]]>" needs to be broken into multiple CDATA segments, like:
		// "Foo]]>Bar" becomes "<![CDATA[Foo]]]]><![CDATA[>Bar]]>"
		// (the end of the first CDATA has the "]]", the other has ">")
		String escaped = unescaped.replaceAll("]]>", "]]]]><![CDATA[>");
		return "<![CDATA[" + escaped + "]]>";
	}

	public void close() {
		if (pw != null) {
			pw.close();
			pw = null;
		}
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
		Log.i("KmlWriter", "directory:" + directory.toString()
				+ ", track name:" + track.getName());
		String fileName = fileUtils.buildFileName(directory, track.getName(),
				TrackExportExt.KML.getExtension());
		if (fileName == null) {
			Log.e("KmlWriter", "Unable to get a unique filename for "
					+ fileName);
			return false;
		}

		Log.i("KmlWriter", "Writing track to: " + fileName);
		try {
			pw = new PrintWriter(newOutputStream(fileName));
			// writer.prepare(track, newOutputStream(fileName));
		} catch (FileNotFoundException e) {
			Log.e("KmlWriter", "Failed to open output file.", e);
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
			Log.i("KmlWriter", "Could not find SD card.");
			errorMessage = R.string.io_no_external_storage_found;
			return false;
		}
		if (!fileUtils.isDirectoryExists(directory)) {
			Log.i("KmlWriter", "Could not create export directory.");
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

	public String generateTrackDescription(Track track) {
		return context.getString(R.string.cpami_byking_link) + "<p>";
	}

	/**
	 * Does the actual work of writing the track to the now open file.
	 */
	private void writeKml() {
		Log.d("KmlWriter", "Started writing track.");
		writeHeader();
		// TrackBuffer buffer = new TrackBuffer(1024);
		Location last = null;
		boolean wroteFirst = false;
		boolean segmentOpen = false;
		int nValidLocations = 0;

		// Fetch small pieces of the track.
		if (!wroteFirst) {
			Location first = createLocation(track.getTrackPoints().get(0));
			writeBeginTrack(first);
			wroteFirst = true;
		}

		for (TrackPoint tp : track.getTrackPoints().values()) {
			Location location = createLocation(tp);
			nValidLocations++;
			if (!segmentOpen) {
				writeOpenSegment();
				segmentOpen = true;
			}
			// main job
			writeLocation(location);

			if (nValidLocations >= 2) {
				last = location;
			}
		}
		if (segmentOpen) {
			writeCloseSegment();
			segmentOpen = false;
		}
		if (wroteFirst) {
			writeEndTrack(last);
		}
		// writeWaypoints(track.getId());
		writeFooter();
		close();
		success = true;
		Log.d("KmlWriter", "Done writing track.");
		errorMessage = R.string.io_write_finished;
	}
}
