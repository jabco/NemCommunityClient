package org.nem.ncc;

import org.apache.commons.io.IOUtils;
import org.nem.core.deploy.CommonConfiguration;
import org.nem.core.node.NodeEndpoint;
import org.nem.core.serialization.JsonSerializer;
import org.nem.core.utils.ExceptionUtils;
import org.nem.ncc.model.*;

import java.io.*;
import java.nio.file.Paths;
import java.util.logging.Logger;

/**
 * The central NCC class.
 * <br/>
 * Did not find a better way of launching Jetty in combination with WebStart.
 * The physical location of the downloaded files is not pre-known, so passing a WAR
 * file to the Jetty runner does not work.
 * Servlet API 3.x with programmatic configuration was chosen.
 * The client starter can be launched from within the scope of WebStart which is the usual way for regular users.
 * In case WebStart is not available, it is assumed that both WAR files are available in the current directory.
 * - first command line argument is the name of the WAR file of API component
 * - second command line argument is the name of the WAR file of the WEB component.
 */
public class NccMain {
	private static final Logger LOGGER = Logger.getLogger(NccMain.class.getName());
	private static final String CONFIG_FILE_NAME = "ncc.cfg";
	private static final CommonConfiguration commonConfiguration = new CommonConfiguration();
	private final org.nem.ncc.model.Configuration configuration;
	private final NccScheduler scheduler;

	public NccMain(final NccScheduler scheduler) {
		final String nccFolder = Paths.get(commonConfiguration.getNemFolder(), "ncc").toString();
		verifyDirectory(new File(nccFolder));

		final String qualifiedConfigFileName = Paths.get(nccFolder, CONFIG_FILE_NAME).toString();
		try (final InputStream inputStream = new ByteArrayInputStream(loadConfigurationStream(nccFolder))) {
			this.configuration = new StreamBackedConfiguration(
					nccFolder,
					inputStream,
					() -> ExceptionUtils.propagate(() -> new FileOutputStream(qualifiedConfigFileName)));
		} catch (final IOException ex) {
			throw new ConfigurationException("unable to initialize NCC", ex);
		}

		this.scheduler = scheduler;
	}

	private static byte[] loadConfigurationStream(final String storagePath) {
		final String qualifiedConfigFileName = Paths.get(storagePath, CONFIG_FILE_NAME).toString();
		try (final InputStream inputStream = new FileInputStream(qualifiedConfigFileName)) {
			return IOUtils.toByteArray(inputStream);
		} catch (final IOException ex) {
			LOGGER.warning(String.format("unable to load NCC configuration: %s", ex));
			final Configuration defaultConfiguration = new Configuration(
					"en",
					NodeEndpoint.fromHost("localhost"),
					NisBootInfo.createLocal(),
					storagePath);
			return JsonSerializer.serializeToBytes(defaultConfiguration);
		}
	}

	private static void verifyDirectory(final File directory) {
		if ((directory.exists() || directory.mkdirs()) && directory.isDirectory()) {
			return;
		}

		// the directory is either not a directory or could not be created
		final String message = String.format("Cannot use <%s> as a directory to store wallets.", directory.getAbsolutePath());
		LOGGER.severe(message);
		throw new ConfigurationException(message);
	}

	/**
	 * Gets the web context string.
	 *
	 * @return The web context string.
	 */
	public static String getWebContext() {
		return commonConfiguration.getWebContext();
	}

	/**
	 * Gets the full url of the start page as string.
	 *
	 * @return The home url as string.
	 */
	public static String getHomeUrl() {
		return commonConfiguration.getHomeUrl();
	}

	/**
	 * Gets the configuration (for configuring of private stuff).
	 *
	 * @return The configuration.
	 */
	public org.nem.ncc.model.Configuration getConfiguration() {
		return this.configuration;
	}
}
