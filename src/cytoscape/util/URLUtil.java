package cytoscape.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.jar.JarInputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipInputStream;

public class URLUtil {

	private static final String GZIP = ".gz";
	private static final String ZIP = ".zip";
	private static final String JAR = ".jar";
	
	public static InputStream getInputStream(URL source) throws IOException {
		
		final InputStream newIs;

		if (source.toString().endsWith(GZIP)) {
			newIs = new GZIPInputStream(source.openStream());
		} else if(source.toString().endsWith(ZIP)) {
			newIs = new ZipInputStream(source.openStream());
		} else if(source.toString().endsWith(JAR)) {
			newIs = new JarInputStream(source.openStream());
		} else {
			newIs = source.openStream();
		}
		return newIs;
	}
}
