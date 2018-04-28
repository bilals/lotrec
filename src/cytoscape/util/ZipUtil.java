/*
 File: ZipMultipleFiles.java 
 
 Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)
 
 The Cytoscape Consortium is: 
 - Institute for Systems Biology
 - University of California San Diego
 - Memorial Sloan-Kettering Cancer Center
 - Institut Pasteur
 - Agilent Technologies
 
 This library is free software; you can redistribute it and/or modify it
 under the terms of the GNU Lesser General Public License as published
 by the Free Software Foundation; either version 2.1 of the License, or
 any later version.
 
 This library is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 documentation provided hereunder is on an "as is" basis, and the
 Institute for Systems Biology and the Whitehead Institute 
 have no obligations to provide maintenance, support,
 updates, enhancements or modifications.  In no event shall the
 Institute for Systems Biology and the Whitehead Institute 
 be liable to any party for direct, indirect, special,
 incidental or consequential damages, including lost profits, arising
 out of the use of this software and its documentation, even if the
 Institute for Systems Biology and the Whitehead Institute 
 have been advised of the possibility of such damage.  See
 the GNU Lesser General Public License for more details.
 
 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation,
 Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 */

package cytoscape.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import java.util.HashMap;

/**
 * Compression-related methods mainly for Session Writer.<br>
 * The created zip files can be decompressed by any zip utilities.
 * 
 * @version 0.9
 * @since Cytoscape 2.3
 * @see cytoscape.data.writers.CytoscapeSessionWriter
 * @author kono
 * 
 */
public class ZipUtil {

	/*
	 * Default Compression Level. Range is 0-9. Basically, 0 is no compression,
	 * and 9 will make the most space-efficeint zip file. However, it takes
	 * long!
	 */
	public static final int DEF_COMPRESSION_LEVEL = 1;

	private String zipArchiveName;
	private String[] inputFiles;
	private String inputFileDir;
	private int fileCount;
	private String sessionDirName;
	private HashMap pluginFileMap = null;

	/**
	 * For zip file, file separator is always "/" in all platforms inclding Win,
	 * Mac, and Unix.
	 */
	private static final String FS = "/";

	/**
	 * Constructor.<br>
	 * 
	 * @param zipFile
	 *            Output zip file name.
	 * @param fileNameList
	 *            List of file names to be compressed.
	 * @param sessionDir
	 *            Root dir created in the zip archive.
	 * 
	 */
	public ZipUtil(final String zipFile, final String[] fileNameList,
			final String sessionDir) {
		this(zipFile, fileNameList, sessionDir, "");
	}

	/**
	 * Constructor.<br>
	 * 
	 * @param zipFile
	 *            Output zip file name.
	 * @param fileNameList
	 *            List of file names to be compressed.
	 * @param sessionDir
	 *            Root dir created in the zip archive.
	 * @param fileDir
	 *            root directory of files in fileList.
	 * 
	 */
	public ZipUtil(final String zipFile, final String[] fileNameList,
			final String sessionDir, final String fileDir) {
		this.zipArchiveName = zipFile;
		this.fileCount = fileNameList.length;
		this.inputFiles = new String[fileCount];
		this.sessionDirName = sessionDir;
		this.inputFileDir = fileDir;

		System.arraycopy(fileNameList, 0, inputFiles, 0, fileCount);
	}

	/**
	 * Delete input files.
	 * 
	 */
	private void clean() {
		for (int i = 0; i < fileCount; i++) {
			final File tempFile = new File(inputFileDir + inputFiles[i]);
			tempFile.delete();
		}
	}

	/**
	 * Faster version of compression method.<br>
	 * 
	 * @param compressionLevel
	 *            Level of compression. Range = 0-9. 0 is no-compression, and 9
	 *            is most space-efficeint. However, 9 is slow.
	 * @param cleanFlag
	 *            If true, remove all imput files.
	 * @throws IOException
	 */
	public void compressFast(final int compressionLevel, final boolean cleanFlag)
			throws IOException {

		// For time measurement
		// final double start = System.currentTimeMillis();

		// FileInputStream fileIS;
		final CRC32 crc32 = new CRC32();
		final byte[] rgb = new byte[5000];
		final ZipOutputStream zipOS = new ZipOutputStream(
				new BufferedOutputStream(new FileOutputStream(zipArchiveName)));

		// Tuning performance
		zipOS.setMethod(ZipOutputStream.DEFLATED);
		if (compressionLevel >= 0 && compressionLevel <= 9) {
			zipOS.setLevel(compressionLevel);
		} else {
			zipOS.setLevel(DEF_COMPRESSION_LEVEL);
		}

		String targetName = "";
		for (int i = 0; i < fileCount; i++) {
			final File file = new File(inputFileDir + inputFiles[i]);
			targetName = sessionDirName + FS + inputFiles[i];
			addEntryToZip(file, targetName, zipOS, crc32, rgb);
		}

		if ((pluginFileMap != null) && (pluginFileMap.size() > 0)) {
			Set<String> pluginSet = pluginFileMap.keySet();

			for (String pluginName : pluginSet) {
				List<File> theFileList = (List<File>) pluginFileMap
						.get(pluginName);
				if ((theFileList == null) || (theFileList.size() == 0))
					continue;
				for (File theFile : theFileList) {
					if ((theFile == null) || (!theFile.exists()))
						continue;
					targetName = sessionDirName + FS + "plugins" + FS
							+ pluginName + FS + theFile.getName();
					addEntryToZip(theFile, targetName, zipOS, crc32, rgb);
				}
			}
		}

		zipOS.close();

		// final double stop = System.currentTimeMillis();
		// final double diff = stop - start;
		// System.out.println("Compression time 3 = " + diff / 1000 + " sec.");

		if (cleanFlag) {
			clean();
		}
	}

	public void setPluginFileMap(HashMap pMap) {
		pluginFileMap = pMap;
	}

	private void addEntryToZip(File srcFile, String targetName,
			ZipOutputStream zipOS, CRC32 crc32, byte[] rgb) throws IOException {
		int numRead;

		// Set CRC
		FileInputStream fileIS = new FileInputStream(srcFile);
		while ((numRead = fileIS.read(rgb)) > -1) {
			crc32.update(rgb, 0, numRead);
		}
		fileIS.close();

		final ZipEntry zipEntry = new ZipEntry(targetName);
		zipEntry.setSize(srcFile.length());
		zipEntry.setTime(srcFile.lastModified());
		zipEntry.setCrc(crc32.getValue());
		zipOS.putNextEntry(zipEntry);

		// Write the file
		fileIS = new FileInputStream(srcFile);
		while ((numRead = fileIS.read(rgb)) > -1) {
			zipOS.write(rgb, 0, numRead);
		}

		fileIS.close();
		zipOS.closeEntry();
	}

	/**
	 * Reads a file contained within a zip file and returns an InputStream.
	 * 
	 * @param zipName
	 *            The name of the zip file to read.
	 * @param fileNameRegEx
	 *            A regular expression that identifies the file to be read. In
	 *            general this should just be the file name you're looking for.
	 *            If more than one file matches the regular expression, only the
	 *            first will be returned. If you're looking for a specific file
	 *            remeber to build your regular expression correctly. For
	 *            example, if you're looking for the file 'vizmap.props', make
	 *            your regular expression '.*vizmap.props' to accomodate any
	 *            clutter from the zip file.
	 * @return An InputStream of the zip entry identified by the regular
	 *         expression or null if nothing matches.
	 */
	public static InputStream readFile(String zipName, String fileNameRegEx)
			throws IOException {
		final ZipFile sessionZipFile = new ZipFile(zipName);
		final Enumeration zipEntries = sessionZipFile.entries();
		while (zipEntries.hasMoreElements()) {
			final ZipEntry zent = (ZipEntry) zipEntries.nextElement();
			if (zent.getName().matches(fileNameRegEx)) {
				return sessionZipFile.getInputStream(zent);
			}
		}
		return null;
	}
}
