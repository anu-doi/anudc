/*******************************************************************************
 * Australian National University Data Commons
 * Copyright (C) 2013  The Australian National University
 * 
 * This file is part of Australian National University Data Commons.
 * 
 * Australian National University Data Commons is free software: you
 * can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package au.edu.anu.datacommons.storage.completer.preserve;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.gov.naa.digipres.xena.core.Xena;
import au.gov.naa.digipres.xena.kernel.XenaException;
import au.gov.naa.digipres.xena.kernel.XenaInputSource;
import au.gov.naa.digipres.xena.kernel.normalise.AbstractNormaliser;
import au.gov.naa.digipres.xena.kernel.normalise.NormaliserResults;
import au.gov.naa.digipres.xena.kernel.type.Type;

/**
 * Calls on Xena File converter by National Archives of Australia to convert a file into its preservation format.
 * 
 * <p>Convertable file list and corresponding convertable format is stored in resource ConvertableTypes.txt
 * 
 * @author Rahul Khanna
 * 
 */
public class PreservationFormatConverter {
	private static final Logger LOGGER = LoggerFactory.getLogger(PreservationFormatConverter.class);

	private static final String RES_CONVERTABLE_TYPES = "ConvertableTypes.txt";
	private static final boolean CONVERT_ONLY = true;
	private static Set<String> convertibleTypes = null;

	static {
		readTypes();
	}

	private Xena xena;
	private CustomXenaInputSource xis;
	private File destDir;

	public PreservationFormatConverter(String filename, InputStream fileStream, File destDir) throws IOException {
		initXena();
		xis = new CustomXenaInputSource(filename, fileStream);
		this.destDir = destDir;
	}

	private void initXena() {
		xena = new Xena();
		loadPlugins();
	}

	/**
	 * Calls Xena to perform the file format conversion. The source file is left intact.
	 * 
	 * @return Results of the Normalisation process.
	 * @throws IOException
	 */
	public NormaliserResults convert() throws IOException {
		NormaliserResults results = null;
		try {
			// Type type = xena.getMostLikelyType(xis);
			Type type = xena.getBestGuess(xis).getType();
			if (convertibleTypes.contains(type.toString())) {
				LOGGER.trace("Converting stream of type [{}] to preservation format...", type.toString());
				AbstractNormaliser normaliser = xena.getNormaliser(type);
				if (normaliser.isConvertible()) {
					results = xena.normalise(xis, destDir, CONVERT_ONLY);
					if (results != null) {
						File converted = new File(results.getDestinationDirString(), results.getOutputFileName());
						if (converted.isFile() && converted.length() > 0) {
							LOGGER.trace("Converted stream to {}.", converted.getAbsolutePath());
						} else {
							LOGGER.error("0 byte file generated when converting stream to preservation format.");
							if (!converted.delete()) {
								converted.deleteOnExit();
							}
							results = null;
						}
					}
				} else {
					LOGGER.trace("Stream not converted to preservation format: Normaliser {} not convertible.",
							type.getName());
				}
			} else {
				LOGGER.trace("Stream not converted to preservation format: Type {} not in convertible list.",
						type.getName());
			}
		} catch (XenaException e) {
			LOGGER.trace(e.getMessage());
		}
		return results;
	}

	/**
	 * Loads the appropriate plugins required by Xena for file format conversion.
	 */
	private void loadPlugins() {
		List<String> pluginList = new ArrayList<String>();
		pluginList.add("au.gov.naa.digipres.xena.plugin.archive.ArchivePlugin");
		pluginList.add("au.gov.naa.digipres.xena.plugin.audio.AudioPlugin");
		pluginList.add("au.gov.naa.digipres.xena.plugin.csv.CsvPlugin");
		pluginList.add("au.gov.naa.digipres.xena.plugin.email.EmailPlugin");
		pluginList.add("au.gov.naa.digipres.xena.plugin.html.HtmlPlugin");
		pluginList.add("au.gov.naa.digipres.xena.plugin.image.ImagePlugin");
		pluginList.add("au.gov.naa.digipres.xena.plugin.metadata.MetadataPlugin");
		pluginList.add("au.gov.naa.digipres.xena.plugin.multipage.MultiPagePlugin");
		pluginList.add("au.gov.naa.digipres.xena.plugin.office.OfficePlugin");
		pluginList.add("au.gov.naa.digipres.xena.plugin.pdf.PdfPlugin");
		pluginList.add("au.gov.naa.digipres.xena.plugin.plaintext.PlainTextPlugin");
		pluginList.add("au.gov.naa.digipres.xena.plugin.project.MsProjectPlugin");
		pluginList.add("au.gov.naa.digipres.xena.plugin.website.WebsitePlugin");
		pluginList.add("au.gov.naa.digipres.xena.plugin.xml.XmlPlugin");

		for (String plugin : pluginList) {
			try {
				xena.loadPlugin(plugin);
			} catch (XenaException e) {
				LOGGER.error("Error loading plugin: {}", plugin);
			}
		}
	}

	private synchronized static void readTypes() {
		if (convertibleTypes == null) {
			convertibleTypes = new HashSet<String>();
			BufferedReader bufferedReader = null;
			try {
				InputStream typesStream = PreservationFormatConverter.class.getResourceAsStream(RES_CONVERTABLE_TYPES);
				bufferedReader = new BufferedReader(new InputStreamReader(typesStream, Charset.defaultCharset()));
				for (String line = bufferedReader.readLine(); line != null; line = bufferedReader.readLine()) {
					if (!line.trim().startsWith("#") && line.trim().length() != 0) {
						convertibleTypes.add(line);
					}
				}
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException(e);
			} catch (IOException e) {
				throw new RuntimeException(e);
			} finally {
				IOUtils.closeQuietly(bufferedReader);
			}
		}
	}
	
	private static class CustomXenaInputSource extends XenaInputSource {
		private String filename;
		private byte[] byteArray;
		
		public CustomXenaInputSource(String filename, InputStream is) throws IOException {
			super(is);
			this.filename = filename;
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			IOUtils.copy(is, baos);
			this.byteArray = baos.toByteArray();
			IOUtils.closeQuietly(is);
		}
		
		@Override
		public InputStream getByteStream() {
			return new ByteArrayInputStream(this.byteArray);
		}
		
		@Override
		public String getSystemId() {
			return this.filename;
		}
		
		@Override
		public String getFileNameExtension() {
			int extIndex = this.filename.lastIndexOf(".");
			return this.filename.substring(extIndex + 1);
		}
	}
}
