package com.inventage.tools.versiontiger.internal.impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.inventage.tools.versiontiger.Version;


/**
 * Immutable version for the generic part of a version (1.2.3). It also remembers
 * if the version is a snapshot or not.
 * 
 * @author Beat Strasser
 */
class GeneralVersion {

	private static final Pattern VERSION_PATTERN = Pattern.compile("^(\\d+)(\\.(\\d+)(\\.(\\d+))?)?$");
	private static final String VERSION_DELIMITER = ".";

	/** The major version part, from 1.2.3 it'd be '1'. */
	private final int major;

	/** The minor version part, from 1.2.3 it'd be '2'. */
	private final Integer minor;

	/** The bugfix version part, from 1.2.3 it'd be '3'. */
	private final Integer bugfix;

	/** Describes if this is a snapshot version. */
	private final boolean snapshot;

	/**
	 * Creates a new {@link GeneralVersion} from the provided version string.
	 */
	GeneralVersion(String version, boolean snapshot) {
		Matcher matcher = VERSION_PATTERN.matcher(version);
		if (!matcher.matches()) {
			throw new IllegalArgumentException("Invalid version part: " + version);
		}

		String majorGroup = matcher.group(1);
		major = majorGroup != null ? Integer.parseInt(majorGroup) : 0;

		String minorGroup = matcher.group(3);
		minor = minorGroup != null ? Integer.parseInt(minorGroup) : null;

		String bugfixGroup = matcher.group(5);
		bugfix = bugfixGroup != null ? Integer.parseInt(bugfixGroup) : null;

		this.snapshot = snapshot;
	}

	/**
	 * Creates a new {@link GeneralVersion} from the provided version
	 * information.
	 * 
	 * Not applicable fields may be set to null.
	 * 
	 * @param major
	 *            an integer major version part. Must not be @{code null}
	 * @param minor
	 *            an integer minor version part, or null if there is none.
	 * @param bugfix
	 *            an integer bugfix version part, or null if there is none.
	 * @param snapshot
	 *            true if this version represents a snapshot version, or false
	 *            otherwise.
	 */
	GeneralVersion(Integer major, Integer minor, Integer bugfix, boolean snapshot) {
		if (major == null) {
			throw new IllegalArgumentException("Major must not be null.");
		}
		if (minor == null && bugfix != null) {
			throw new IllegalArgumentException("Minor must not be null when bugfix is set.");
		}
		this.major = major;
		this.minor = minor;
		this.bugfix = bugfix;
		this.snapshot = snapshot;
	}

	Integer major() {
		return major;
	}

	Integer minor() {
		return minor;
	}

	Integer bugfix() {
		return bugfix;
	}
	
	boolean isSnapshot() {
		return snapshot;
	}
	
	String versionString() {
		StringBuilder result = new StringBuilder();
		
		result.append(major);
		if (minor != null) {
			result.append(VERSION_DELIMITER);
			result.append(minor);
			if (bugfix != null) {
				result.append(VERSION_DELIMITER);
				result.append(bugfix);
			}
		}
		
		return result.toString();
	}
	
	@Override
	public String toString() {
		return versionString() + ", snapshot=" + snapshot;
	}

	boolean isLowerThan(Version other, boolean inclusive) {
		if (nullToZero(major()) == nullToZero(other.major())) {
			if (nullToZero(minor()) == nullToZero(other.minor())) {
				if (nullToZero(bugfix()) == nullToZero(other.bugfix())) {
					return inclusive;
				}
				return nullToZero(bugfix()) < nullToZero(other.bugfix());
			}
			return nullToZero(minor()) < nullToZero(other.minor());
		}
		return nullToZero(major()) < nullToZero(other.major());
	}

	int compareTo(Version o) {
		if (major != nullToZero(o.major())) {
			return major().compareTo(nullToZero(o.major()));
		}
		if (nullToZero(minor()) != nullToZero(o.minor())) {
			return Integer.valueOf(nullToZero(minor())).compareTo(nullToZero(o.minor()));
		}
		if (nullToZero(bugfix()) != nullToZero(o.bugfix())) {
			return Integer.valueOf(nullToZero(bugfix())).compareTo(nullToZero(o.bugfix()));
		}
		return 0;
	}
	
	GeneralVersion incrementMajorAndSnapshot() {
		return new GeneralVersion(major + 1, numbersToZero(minor()), numbersToZero(bugfix()), true);
	}

	GeneralVersion incrementMinorAndSnapshot() {
		return new GeneralVersion(major(), nullToZero(minor()) + 1, numbersToZero(bugfix()), true);
	}

	GeneralVersion incrementBugfixAndSnapshot() {
		return new GeneralVersion(major(), nullToZero(minor()), nullToZero(bugfix()) + 1, true);
	}

	private static int nullToZero(Integer integer) {
		return integer == null ? 0 : integer;
	}

	private static Integer numbersToZero(Integer integer) {
		return integer == null ? null : 0;
	}

}
