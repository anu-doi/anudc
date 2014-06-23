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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
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
	private File input;
	private XenaInputSource xis;
	private File destDir;

	public PreservationFormatConverter(File input, File destDir) throws FileNotFoundException {
		initXena();
		this.input = input;
		xis = new XenaInputSource(input);
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
			Type type = xena.getMostLikelyType(xis);
			if (convertibleTypes.contains(type.toString())) {
				LOGGER.trace("Converting file {} of type [{}] to preservation format...", this.input.getAbsolutePath(),
						type.toString());
				AbstractNormaliser normaliser = xena.getNormaliser(type);
				if (normaliser.isConvertible()) {
					results = xena.normalise(xis, destDir, CONVERT_ONLY);
					if (results != null) {
						File converted = new File(results.getDestinationDirString(), results.getOutputFileName());
						if (converted.isFile() && converted.length() > 0) {
							LOGGER.trace("Converted file {} to {}.", this.input.getAbsolutePath(),
									converted.getAbsolutePath());
						} else {
							LOGGER.error("0 byte file generated when converting {} to preservation format.",
									this.input.getName());
							if (!converted.delete()) {
								converted.deleteOnExit();
							}
							results = null;
						}
					}
				} else {
					LOGGER.trace("File {} not converted to preservation format: Normaliser {} not convertible.",
							this.input.getAbsolutePath(), type.getName());
				}
			} else {
				LOGGER.trace("File {} not converted to preservation format: Type {} not in convertible list.",
						this.input.getAbsolutePath(), type.getName());
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
				bufferedReader = new BufferedReader(new InputStreamReader(typesStream, "UTF-8"));
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
}
