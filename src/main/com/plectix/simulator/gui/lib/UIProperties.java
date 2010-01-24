package com.plectix.simulator.gui.lib;

import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.UIManager;
import javax.swing.plaf.synth.SynthLookAndFeel;

/**
 * Constants for basic colors, sizes, fonts, etc. defined in a properties file
 * for easy customization, localization, branding, etc.
 * 
 * @author ecemis
 */
public class UIProperties {
	
	private static Properties props = new Properties();
	private static Map<String, Color> colors = new HashMap<String, Color>();
	private static Map<String, Font> fonts = new HashMap<String, Font>();
	private static Map<String, Image> images = new HashMap<String, Image>();
	private static Map<String, Object> otherObjects = new HashMap<String, Object>();
	
	/**
	 * Initialize the UIProperties object from a .properties file. Any
	 * properties whose values look like "Color(R,G,B)",
	 * "Font(face,style,size)", or "Image(filename)" will be converted to
	 * java.awt.Color, java.awt.Font, or java.awt.Image objects respectively and
	 * saved in separate maps for faster retrieval.
	 * 
	 * @see UIProperties#getColor(String)
	 * @see UIProperties#getFont(String)
	 * @see UIProperties#getImage(String)
	 */
    private UIProperties() throws Exception {
		
		// Load properties file
		loadPropertyFile(Settings.PROPERTIES_FILENAME);

		// Set up look-and-feel
		if (UIProperties.propertyExists("lookAndFeelResource")) {
			setLookAndFeel(UIProperties.getString("lookAndFeelResource")); 
		} else {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
	}

	private static void loadPropertyFile(String propertyFilename) throws IOException {

		// Load properties from resource
		Properties properties = new Properties();
		properties.load(new FileReader(propertyFilename));

		// Add to master properties
		props.putAll(properties);

		// Save maps of Color, Font, and Image objects
		for (Iterator it = properties.keySet().iterator(); it.hasNext();) {
			
			String key = (String) it.next();
			String value = properties.getProperty(key).trim();
			if (value.startsWith("Color(")) {
			
				// Skip if we've already made this color
				if (colors.containsKey(value))
					continue;

				// Convert "Color(1,2,3)" to "1,2,3"
				String temp = value.substring(6, value.length() - 1);

				// Separate RGB values
				String[] split = temp.split(",");
				int red = Integer.parseInt(split[0].trim());
				int green = Integer.parseInt(split[1].trim());
				int blue = Integer.parseInt(split[2].trim());

				// Save new Color object in map
				if (split.length == 3)
					colors.put(value, new Color(red, green, blue));
				else
					colors.put(value, new Color(red, green, blue, Integer.parseInt(split[3].trim())));
			
			} else if (value.startsWith("Font(")) {

				// Skip if we've already made this font
				if (fonts.containsKey(value))
					continue;

				// Convert "Font(x,y,z)" to "x,y,z"
				String temp = value.substring(5, value.length() - 1);

				// Separate parameters
				String[] split = temp.split(",");
				String face = split[0].trim();
				int style = Font.PLAIN;
				if (split[1].trim().equalsIgnoreCase("bold"))
					style = Font.BOLD;
				else if (split[1].trim().equalsIgnoreCase("italic"))
					style = Font.ITALIC;
				float size = Float.parseFloat(split[2]);

				// Save new Font object in map
				// log.debug("Creating " + value);
				Font font = makeFont(face, style, size);
				fonts.put(value, font);

			} else if (value.startsWith("Image(")) {
			
				// Skip if we've already made this image
				if (images.containsKey(value))
					continue;

				// Convert "Image(filename)" to "filename"
				String temp = value.substring(6, value.length() - 1);

				// Create the image object
				String resourcePath = Settings.IMAGES_RESOURCE_PATH + temp;
				URL url = UIProperties.class.getResource("/" + resourcePath);
				if (url == null)
					throw new FileNotFoundException(resourcePath);
				// log.debug("Creating image " + resourcePath);
				Image image = Toolkit.getDefaultToolkit().getImage(url);

				// Force it to load now so the UI won't be glitchy later
				// when the image is first displayed
				image.getHeight(null);

				// Save new Image object in map
				images.put(value, image);
			}
		}
	}

	private static String getCheckedProperty(String property) {
		String result = props.getProperty(property);
		if (result == null) {
			// log.error("UI properties key " + property + " not found");
			throw new IllegalArgumentException(property);
		}
		return result.trim();
	}

	/**
	 * Return true if the specified property key exists.
	 * @param property property key to check
	 * @return true if property key exists
	 */
	private static boolean propertyExists(String property) {
		return props.containsKey(property);
	}

	/**
	 * Get a string from the property map.
	 * @param property property key from conf/ui.properties
	 * @return String value of property
	 * @throws IllegalArgumentException if property key is not found
	 */
	public static String getString(String property) {
		return getCheckedProperty(property);
	}

	/**
	 * Get an integer value from the property map.
	 * @param property property key from conf/ui.properties
	 * @return int value of property
	 * @throws IllegalArgumentException if property key is not found
	 */
	public static int getInt(String property) {
		String result = getCheckedProperty(property);
		return Integer.parseInt(result);
	}

	/**
	 * Get a double value from the property map.
	 * @param property property key from conf/ui.properties
	 * @return double value of property
	 * @throws IllegalArgumentException if property key is not found
	 */
	public static double getDouble(String property) {
		String result = getCheckedProperty(property);
		return Double.parseDouble(result);
	}

	/**
	 * Get a java.awt.Color object from the color map.  The property
	 * should be a value like "Color(x,y,z)".
	 * @param property property key from conf/ui.properties
	 * @return java.awt.Color with specified values
	 * @throws IllegalArgumentException if property key is not found
	 */
	public static Color getColor(String property) {
		String result = getCheckedProperty(property);
		return colors.get(result);
	}

	/**
	 * Get a java.awt.Font object from the font map.
	 * 
	 * @param property property key from conf/ui.properties
	 * @return java.awt.Font Font with specified values
	 * @throws IllegalArgumentException if property key is not found
	 */
	public static Font getFont(String property) {
		String result = getCheckedProperty(property);
		return fonts.get(result);
	}

	/**
	 * Get a java.awt.Font object from the font map, specified by
	 * name, style, and size.  In almost all cases, you should call
	 * getFont(String) instead, to specify the font by property key.
	 * 
	 * @param name Font name, e.g. "Gill Sans"
	 * @param style Font style
	 * @param size Font size
	 * @return Font matching name, style, and size, or null if not found
	 */
	public static Font getFont(String name, int style, int size) {

		String styleString = "plain";
		if (style == Font.BOLD)
			styleString = "bold";
		else if (style == Font.ITALIC)
			styleString = "italic";

		StringBuffer key = new StringBuffer();
		key.append("Font(").append(name).append(",").append(styleString).append(",").append(size).append(")");

		Font result = fonts.get(key.toString());
		if (result == null) {
			// log.warn("Unexpected font: " + key.toString() + " (make sure font is defined in ui.properties)");
		}
		return result;
	
	}

	/**
	 * Get a java.awt.Image object from the image map.  The property
	 * should be a value like "Image(filename)".  The filename should
	 * be relative to images/.
	 * @param property property key from ui.properties
	 * @return java.awt.Image image with specified filename
	 * @throws IllegalArgumentException if property key is not found
	 */
	private static Image getImage(String property) {
		String result = getCheckedProperty(property);
		return images.get(result);
	}

	/**
	 * Creates and returns an object. This method uses reflection 
	 * to create them by name. Examples:
	 * 
	 * <pre>
	      point.origin   = java.awt.Point(int=0, int=0)
          my.rectangle   = java.awt.Rectangle(int=130, int=10, int=120, int=20)
          my.dimension   = java.awt.Dimension(int=800, int=600)
	 * </pre>
	 * 
	 * Note that this function also shares the same objects 
	 * through a hashmap.
	 *  
	 * @param property a property, e.g. <code>point.origin</code>
	 * @return cashed or created object 
	 */
	public static Object getObject(String property)
	{
		String objectString = getCheckedProperty(property);
		Object ret = otherObjects.get(objectString); 
		if (ret != null) {
			return ret;
		}
			
		int openParan = objectString.indexOf("(");
		int closeParan = objectString.indexOf(")");

		String className = objectString.substring(0, openParan);
		String[] arguments = objectString.substring(openParan+1, closeParan).split(",");

		Class[] args = new Class[arguments.length];
		Object[] objs = new Object[arguments.length];

		for (int i= 0; i< args.length; i++){
			String[] argumentFields = arguments[i].trim().split("=");
			// primitive Java types:
			if (argumentFields[0].trim().equalsIgnoreCase("boolean")){
				args[i] = boolean.class;
				objs[i] = Boolean.parseBoolean(argumentFields[1].trim());
			} else if (argumentFields[0].trim().equalsIgnoreCase("byte")){
				args[i] = byte.class;
				objs[i] = Byte.parseByte(argumentFields[1].trim());
			} else if (argumentFields[0].trim().equalsIgnoreCase("char")){
				args[i] = char.class;
				objs[i] = argumentFields[1].trim().charAt(0);
			} else if (argumentFields[0].trim().equalsIgnoreCase("short")){
				args[i] = short.class;
				objs[i] = Short.parseShort(argumentFields[1].trim());
			} else if (argumentFields[0].trim().equalsIgnoreCase("int")){
				args[i] = int.class;
				objs[i] = Integer.parseInt(argumentFields[1].trim());
			} else if (argumentFields[0].trim().equalsIgnoreCase("long")){
				args[i] = long.class;
				objs[i] = Long.parseLong(argumentFields[1].trim());
			} else if (argumentFields[0].trim().equalsIgnoreCase("float")){
				args[i] = float.class;
				objs[i] = Float.parseFloat(argumentFields[1].trim());
			} else if (argumentFields[0].trim().equalsIgnoreCase("double")){
				args[i] = double.class;
				objs[i] = Double.parseDouble(argumentFields[1].trim());
			} else {
				return null;
			}
		}

		try {
			ret = Class.forName(className).getConstructor(args).newInstance(objs);
			otherObjects.put(objectString, ret); 
			return ret;
		} catch (Exception e) {
			e.printStackTrace();
			// log.error("UI properties key " + property + " is ill-defined!");
			throw new IllegalArgumentException(property);
		}
	}


	/**
	 * Return an Icon for an image retrieved from getImage().
	 * @see #getImage(String)
	 * @param property property key from ui.properties
	 * @return icon for image with specified filename
	 */
	public static Icon getIcon(String property) {
		return new ImageIcon(getImage(property));
	}
	
	private static void setLookAndFeel(String lookAndFeelResource) throws Exception {
		SynthLookAndFeel synth = new SynthLookAndFeel();
		InputStream lookAndFeelStream = UIProperties.class.getResourceAsStream("/" + lookAndFeelResource);
		synth.load(lookAndFeelStream, UIProperties.class);
		UIManager.setLookAndFeel(synth);
	}

	private static Font makeFont(String face, int style, float size) {

		// Look for match in available fonts
		Font[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
		for (Font f : fonts) {
			if (f.getName().equalsIgnoreCase(face))
				return f.deriveFont(style, size);
		}

		// Use system font by default
		// log.warn("Font " + face + " not found; using SansSerif");
		return new Font("SansSerif", style, (int) size);
	}

	public static void setProperty(String name, int value) {
		props.setProperty(name, Integer.toString(value));
	}

	public static void setProperty(String name, String value) {
		props.setProperty(name, value);
	}

	public static void setProperty(String name, Color value) {
		colors.put(name, value);
	}

	/**
	 * Format a message specified by a property key.  The message is
	 * may use {0}, {1}, etc. as placeholders for the values supplied
	 * as additional parameters to this method.
	 * @see MessageFormat#format(String, Object[])
	 * @param propertyName property key name
	 * @param args variable-length list of replacement values for {0} etc.
	 * @return formatted message
	 */
	public static String getMessageString(String propertyName, Object... args) {
		String message = UIProperties.getString(propertyName);
		return MessageFormat.format(message, args);
	}

}
