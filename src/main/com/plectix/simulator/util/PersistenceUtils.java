package com.plectix.simulator.util;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.core.ReferenceByIdMarshallingStrategy;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * PersistenceUtils provides static methods to persist objects into XML or Serialized binary files
 * 
 * @author ecemis
 */
public class PersistenceUtils {
	public static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	
	/** common xml extension */
	public static final String XML_EXTENSION = ".xml";

	/** common zip extension */
	public static final String ZIP_EXTENSION = ".zip";

	private static XStream xStream = null;

	public static XStream getXStream(){
		initialize();
		return xStream;
	}
	
	// ************************************************************************************
	
	public static void saveToXML(Object object, String filename, boolean zipped)
			throws IOException {
		initialize();
		OutputStream outputStream = getOutputStream(filename, zipped);
		xStream.toXML(object, outputStream);
		outputStream.flush();
		outputStream.close();
	}
	
	// ************************************************************************************
	
	public static Object loadFromXML(String filename, boolean zipped)
			throws IOException {
		initialize();
		return xStream.fromXML(getInputStream(filename, zipped));
	}
	
	// ************************************************************************************
	/**
	 * 
	 * @param object
	 * @param filename
	 * @throws IOException
	 */
	public static void saveToSerializedBinary(Object object, String filename, boolean zipped) throws IOException {
		OutputStream outputStream = getOutputStream(filename, zipped);
		ObjectOutputStream objStream = new ObjectOutputStream(outputStream);
		objStream.writeObject(object);
		outputStream.flush();
		outputStream.close();
	}

	// ************************************************************************************
	/**
	 * 
	 * @param filename
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static Object loadFromSerializedBinary(String filename,
			boolean zipped) throws IOException, ClassNotFoundException {
		ObjectInputStream objStream = new ObjectInputStream(getInputStream(filename, zipped));
		return objStream.readObject();
	}

	// ************************************************************************************
	/**
	 * 
	 * @param fromFilename
	 * @param fromXML
	 * @param toFilename
	 * @param toXML
	 * @param zipped
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static void convertFile(String fromFilename, boolean fromXML,
			String toFilename, boolean toXML, boolean zipped)
			throws IOException, ClassNotFoundException {
		Object object;
		if (fromXML) {
			object = loadFromXML(fromFilename, fromFilename
					.endsWith(ZIP_EXTENSION));
		} else {
			object = loadFromSerializedBinary(fromFilename, fromFilename
					.endsWith(ZIP_EXTENSION));
		}
		if (toXML) {
			saveToXML(object, toFilename, zipped);
		} else {
			saveToSerializedBinary(object, toFilename, zipped);
		}
	}

	// ************************************************************************************
	/**
	 * 
	 * @param filename
	 * @param zipped
	 * @return
	 * @throws IOException
	 */
	public static InputStream getInputStream(String filename, boolean zipped)
			throws IOException {
		if (zipped) {
			ZipInputStream zipInputStream = new ZipInputStream(
					new BufferedInputStream(new FileInputStream(filename
							+ ZIP_EXTENSION)));
			zipInputStream.getNextEntry();
			return zipInputStream;
		} else {
			return new BufferedInputStream(new FileInputStream(filename));
		}
	}

	// ************************************************************************************
	/**
	 * 
	 * @param filename
	 * @param zipped
	 * @return
	 * @throws IOException
	 */
	public static OutputStream getOutputStream(String filename, boolean zipped)
			throws IOException {
		if (zipped) {
			OutputStream outputStream = new BufferedOutputStream(
					new FileOutputStream(filename + ZIP_EXTENSION));
			ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream);
			ZipEntry zipEntry = new ZipEntry(new File(filename).getName());
			zipOutputStream.putNextEntry(zipEntry);
			return zipOutputStream;
		} else {
			return new BufferedOutputStream(new FileOutputStream(filename));
		}
	}

	// ************************************************************************************
	/**
	 * 
	 * 
	 */
	private static void initialize() {
		if (xStream != null) {
			return;
		}
		xStream = new XStream();
		xStream.setMarshallingStrategy(new ReferenceByIdMarshallingStrategy());

		// Register converters: For example:
		// xStream.registerConverter(new DateConverter());

		// Set aliases:
		// addAlias(Class.class);
	}

	// ************************************************************************************
	/**
	 * 
	 * @param clazz
	 */
	private static final void addAlias(Class clazz) {
		xStream.alias(clazz.getSimpleName(), clazz);
	}

	// ************************************************************************************
	/**
	 * Usage: <br>
	 * <code>-i /tmp/localDataService.xml -o /tmp/convertedLocalDataService.bin</code>
	 * 
	 * @param args
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException,
			ClassNotFoundException {
		String fromFilename = null;
		String toFilename = null;
		boolean zipped = false;

		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-i")) {
				fromFilename = args[++i];
			} else if (args[i].equals("-o")) {
				toFilename = args[++i];
			} else if (args[i].equals("-z")) {
				zipped = true;
			} else {
				System.err.println("\nUnknown argument: " + args[i]
						+ "\nAborting...\n\n");
				System.exit(-1);
			}
		}

		if (fromFilename == null || toFilename == null) {
			System.err.println("\nFilename(s) not set! \nAborting...\n\n");
			System.exit(-1);
		}
		assert fromFilename!=null;
		assert toFilename!=null;
		System.err.println("\nStarting to convert...");
		convertFile(fromFilename, fromFilename.endsWith(XML_EXTENSION),
				toFilename, toFilename.endsWith(XML_EXTENSION), zipped);
		System.err.println("\nDone.\n");
		System.exit(0);
	}

	// ************************************************************************************
	/**
	 * 
	 * 
	 */
	public static class DateConverter implements Converter {
		public DateConverter() {
			super();
		}

		public boolean canConvert(Class clazz) {
			return clazz.equals(Date.class);
		}

		public void marshal(Object value, HierarchicalStreamWriter writer,
				MarshallingContext context) {
			writer.setValue(DATE_FORMAT.format((Date) value));
		}

		public Object unmarshal(HierarchicalStreamReader reader,
				UnmarshallingContext context) {
			try {
				// return DateUtils.removeUnnecessaryFields(DATE_FORMAT.parse(reader.getValue()));
				return DATE_FORMAT.parse(reader.getValue());
			} catch (ParseException e) {
				throw new ConversionException(e.getMessage(), e);
			}
		}
	}

	// -------------------------------------------------------------------------


	/**
	 * reads the values of non-transient fields of an object o into a map keyed
	 * by their names. Static fields are excluded as well. The returned map may
	 * contain null values.
	 * 
	 * DO NOT USE THIS AS A GENERAL PURPOSE SERIALIZATION MECHANISM. it is meant
	 * for enumerating field for objects that have an otherwise prohibitively
	 * large number of members.
	 * 
	 * @Throws Error
	 *             if a field is not accessible, typically because runtime
	 *             security settings prevent access to private fields. Also
	 *             fails if a field name is repeated in a class's hierarchy.
	 */
	public static Map<String, Object> getNonTransientFields(Object o) {
		Map<String, Object> ret = new HashMap<String, Object>();
		// copied from DMComposite r6104
		Class c = o.getClass();
		// ERROR must call c.setAccessible true if we have a private inner class
		do {
			Field[] fields = c.getDeclaredFields();

			for (Field field : fields) {
				try {
					int mods = field.getModifiers();
					// skip transient
					if (Modifier.isTransient(mods))
						continue;
					if (Modifier.isStatic(mods))
						continue;

					field.setAccessible(true);
					// TODO deal with element repeated in super class and
					// subclass
					Object old = ret.put(field.getName(), field.get(o));
					if (old != null) {
						throw new Error("field " + field.getName()
								+ " does not have a unique name");
					}
				} catch (Exception e) {
					throw new Error("failed to read field: " + field.getName(),
							e);
				}
			}
			c = c.getSuperclass();
		} while (c != null);
		return ret;
	}

	/**
	 * Set all fields of o named in m. <code>m</code> contain null values.
	 * Transient/static fields are skipped. First occurrence of name walking up
	 * inheritance hierarchy will be assigned new value.
	 * 
	 * @throws IOException
	 *             if a field name can't be found or the specified value can't
	 *             be assigned to that field (typically a ClassCastException).
	 */
	public static void setFields(Object o, Map<String,?> m) throws IOException {
		Class c = o.getClass();
		// ERROR must call c.setAccessible true if we have a private inner class
		do {
			Field[] fields = c.getDeclaredFields();

			for (Field field : fields) {
				try {
					int mods = field.getModifiers();
					// skip transient
					if (Modifier.isTransient(mods))
						continue;
					if (Modifier.isStatic(mods))
						continue;

					field.setAccessible(true);
					// TODO deal with element repeated in super class and
					// subclass
					String n = field.getName();
					if (m.containsKey(n))
						field.set(o, m.remove(n));
				} catch (Exception e) {
					String msg = "failed to read field: " + field.getName();
					IOException e2 = new IOException(msg);
					e2.initCause(e);
					throw e2;
				}
			}
			c = c.getSuperclass();
		} while (c != null);
		if (!m.isEmpty()) {
			String msg = "the following fields could not be resolved:"
					+ m.toString();
			throw new IOException(msg);
		}
	}
}
