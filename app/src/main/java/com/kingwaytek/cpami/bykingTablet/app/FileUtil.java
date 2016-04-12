package com.kingwaytek.cpami.bykingTablet.app;

import java.io.File;
import java.util.regex.Pattern;

import android.os.Environment;

/**
 * File Utility to handle file open, read, or write. part referenced from google
 * mytrack example.
 * 
 * @author Andy Chiao
 * 
 */
public class FileUtil {

	/**
	 * The maximum length of a filename, as per the FAT32 specification.
	 */
	private static final int MAX_FILENAME = 260;

	/**
	 * A set of characters that are prohibited from being in file names.
	 * \u0800-\u9fa5 accept utf8 Chinese Characters
	 */
	private static final Pattern PROHIBITED_CHARACTORS = Pattern
			.compile("[^ \u0800-\u9fa5A-Za-z0-9_.()]+");

	public boolean isSdCardPresent() {
		return Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState());
	}

	/**
	 * Normalizes the input string and make sure it is a valid fat32 file name.
	 * 
	 * @param name
	 *            the name to normalize
	 * @param overheadSize
	 *            the number of additional characters that will be added to the
	 *            name after sanitization
	 * @return the sanitized name
	 */
	String sanitizeName(String name) {
		String resultName = PROHIBITED_CHARACTORS.matcher(name).replaceAll("");

		return (resultName.length() > MAX_FILENAME) ? resultName.substring(0,
				MAX_FILENAME) : resultName.toString();
	}

	/**
	 * Ensures the given directory exists by creating it and its parents if
	 * necessary.
	 * 
	 * @return whether the directory exists (either already existed or was
	 *         successfully created)
	 */
	public boolean isDirectoryExists(File dir) {
		if (dir.exists() && dir.isDirectory()) {
			return true;
		}

		if (dir.mkdirs()) {
			return true;
		}

		return false;
	}

	/**
	 * Checks whether a file with the given name exists in the given directory.
	 * This is isolated so it can be overridden in tests.
	 */
	protected boolean isFileExists(File directory, String fullName) {
		File file = new File(directory, fullName);
		return file.exists();
	}

	/**
	 * Builds a filename with the given base name (prefix) and the given
	 * extension, possibly adding a suffix to ensure the file doesn't exist.
	 * 
	 * @param directory
	 *            the directory the file will live in
	 * @param fileBaseName
	 *            the prefix for the file name
	 * @param extension
	 *            the file's extension
	 * @return the complete file name, without the directory
	 */
	public synchronized String buildFileName(File directory,
			String fileBaseName, String extension) {
		return buildFileName(directory, fileBaseName, extension, 0);
	}

	/**
	 * Builds a filename with the given base name (prefix) and the given
	 * extension, possibly adding a suffix to ensure the file doesn't exist.
	 * 
	 * @param directory
	 *            the directory the file will live in
	 * @param fileBaseName
	 *            the prefix for the file name
	 * @param extension
	 *            the file's extension
	 * @param suffix
	 *            the first numeric suffix to try to use, or 0 for none
	 * @return the complete file name, without the directory
	 */
	private String buildFileName(File directory, String fileBaseName,
			String extension, int suffix) {
		String suffixedBaseName = fileBaseName;
		if (suffix > 0) {
			suffixedBaseName += " (" + Integer.toString(suffix) + ")";
		}

		String fullName = suffixedBaseName + "." + extension;
		String sanitizedName = sanitizeName(fullName);
		if (!isFileExists(directory, sanitizedName)) {
			return sanitizedName;
		}

		return buildFileName(directory, fileBaseName, extension, suffix + 1);
	}
}
