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

package au.edu.anu.datacommons.image.magick;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import au.edu.anu.datacommons.image.filter.ImageAndDistFileFilter;
import au.edu.anu.datacommons.image.main.DefaultProperties;

/**
 * ConvertImage
 * 
 * Australian National University Data Commons
 * 
 * Convert Image functions
 *
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		12/04/2013	Genevieve Turner (GT)	Initial
 * </pre>
 *
 */
public class ConvertImage {
	/**
	 * Constructor
	 * 
	 * Constructor for the ConvertImage 
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		12/04/2013	Genevieve Turner(GT)	Initial
	 * </pre>
	 *
	 */
	public ConvertImage() {
		
	}
	
	/**
	 * addTextToImage
	 *
	 * Add a strapline of text to the bottom of an image
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		12/04/2013	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param filename A file or directory to retrieve the image for
	 * @param leftText The text to place on the left of the 
	 * @param rightText
	 * @throws IOException
	 */
	public void addTextToImage(String filename, String leftText, String rightText) throws IOException {
		File file = new File(filename);
		if (file.isFile()) {
			addTextToImage(file, leftText, rightText);
		}
		// Will need to change this if we want to recursively move through directories
		else if (file.isDirectory()) {
			System.out.println("Processing Images for Directory: " + file.getAbsolutePath());
			File[] files = file.listFiles(new ImageAndDistFileFilter());
			for (File filteredFile : files) {
				if (filteredFile.isFile()) {
					addTextToImage(filteredFile, leftText, rightText);
				}
			}
		}
		System.out.println("Adding Captions Complete");
	}
	
	/**
	 * addTextToImage
	 *
	 * Add the strapline text to an image for the given file
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		12/04/2013	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param file
	 * @param leftText
	 * @param rightText
	 * @throws IOException
	 */
	public void addTextToImage(File file, String leftText, String rightText) throws IOException {
		System.out.println("Adding Caption To: " + file.getAbsolutePath());
		boolean performJavaText = true;
		try {
			addTextToImageMagick(file, leftText, rightText);
			performJavaText = false;
		}
		catch (IOException e) {
			System.out.println("Cannot convert image with Image Magick");
		}
		if (performJavaText) {
			System.out.println("Adding caption via Java Images");
			addTextToImageJava(file, leftText, rightText);
		}
	}
	
	/**
	 * addTextToImageJava
	 *
	 * Add text to the image via the java graphics2d class
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		12/04/2013	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param file The file to add text to
	 * @param leftText The left caption to add
	 * @param rightText The right caption to add
	 * @throws IOException
	 */
	private void addTextToImageJava(File file, String leftText, String rightText) throws IOException {
		BufferedImage image = ImageIO.read(file);
		
		String name = file.getName();
		String filename	 = name.substring(0, name.lastIndexOf("."));
		String suffix = name.substring(name.lastIndexOf(".") + 1);
		
		int space = getStrapHeight(image.getHeight());
		
		BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight() + space, BufferedImage.TYPE_INT_BGR);
		
		Graphics2D g2d = (Graphics2D) newImage.createGraphics();
		g2d.addRenderingHints(new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY));
		g2d.addRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));
		g2d.addRenderingHints(new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON));
		
		g2d.drawImage(image, 0, 0, null);
		g2d.setColor(Color.BLACK);
		g2d.drawRect(0, image.getHeight(), image.getWidth(), space);
		g2d.setColor(Color.WHITE);
		g2d.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, space - 4));
		FontMetrics fm = g2d.getFontMetrics();
		
		g2d.drawString(leftText, 2, newImage.getHeight() - 2);
		
		int rightTextWidth = fm.stringWidth(rightText);
		g2d.drawString(rightText, newImage.getWidth() - rightTextWidth - 2, newImage.getHeight() - 2);
		
		g2d.dispose();
		
		File outputFile = new File(file.getParent() + File.separator + filename + "-dist." + suffix);
		ImageIO.write(newImage, suffix, outputFile);
	}
	
	/**
	 * addTextToImageMagick
	 *
	 * Add text to the image via image magick
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		12/04/2013	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param file The file to add text to
	 * @param leftText The left caption to add
	 * @param rightText The right caption to add
	 * @throws IOException
	 */
	private void addTextToImageMagick(File file, String leftText, String rightText) throws IOException {
		String name = file.getName();
		String filename	 = name.substring(0, name.lastIndexOf("."));
		String suffix = name.substring(name.lastIndexOf(".") + 1);
		
		String newFilename = file.getParent() + File.separator + filename + "-dist." + suffix;
		
		Dimension imageSize = getImageSize(file);
		
		//int space = 14;
		int space = getStrapHeight(imageSize.getHeight());
		int pointsize = space - 4;
		String imageMagickLocation = DefaultProperties.getProperty("imagemagick.location");
		
		StringBuilder sb = new StringBuilder();
		sb.append("\"");
		sb.append(imageMagickLocation);
		sb.append("\" \"(\" ");
		sb.append(file.getAbsolutePath());
		sb.append(" -gravity south -background black -splice 0x");
		sb.append(space);
		sb.append(" \")\" -fill white -font Arial -pointsize ");
		sb.append(pointsize);
		sb.append(" -gravity southwest -annotate 0 \"");
		sb.append(leftText);
		sb.append("\" -gravity southeast -annotate 0 \"");
		sb.append(rightText);
		sb.append("\" ");
		sb.append(newFilename);
		Process process = Runtime.getRuntime().exec(sb.toString());
		try {
			process.waitFor();
		}
		catch (InterruptedException e) {
			System.out.println("Image Magick Process Interrupted");
		}
	}
	
	/**
	 * getStrapHeight
	 *
	 * Get the height for the black strapline
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		12/04/2013	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param height
	 * @return
	 */
	private int getStrapHeight(int height) {
		return getStrapHeight(new Double(height));
	}
	
	/**
	 * getStrapHeight
	 *
	 * Get the height for the black strapline
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		12/04/2013	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param height
	 * @return
	 */
	private int getStrapHeight(double height) {
		return Math.max(new Double(height * 0.02).intValue(), 14);
	}
	
	/**
	 * getImageSize
	 *
	 * Retrieve the dimensions of the image
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		12/04/2013	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	private Dimension getImageSize(File file) throws IOException {
		
		ImageInputStream in = ImageIO.createImageInputStream(file);
		try {
			final Iterator readers = ImageIO.getImageReaders(in);
			if (readers.hasNext()) {
				ImageReader reader = (ImageReader) readers.next();
				try {
					reader.setInput(in);
					return new Dimension(reader.getWidth(0), reader.getHeight(0));
				}
				finally {
					reader.dispose();
				}
			}
		}
		finally {
			if (in != null) {
				in.close();
			}
		}
		return null;
	}
}
