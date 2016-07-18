package com.droitfintech.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.jasypt.util.binary.BasicBinaryEncryptor;
import org.jasypt.util.binary.StrongBinaryEncryptor;
import org.jasypt.util.password.StrongPasswordEncryptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.droitfintech.exceptions.DroitException;

public class FilesystemDataLoader {

    private static final Logger log = LoggerFactory.getLogger(FilesystemDataLoader.class);

    private final static String SYS_PROP = "com.droitfintech.home";
    private final static String ENV_PROP = "DROIT_HOME";
    private final static String DEFAULT_DIR = "/etc/droit";

    private static String rootLocation;
    private static boolean useClasspath = false;
    private static Class classloadedClass = FilesystemDataLoader.class;

    public static File loadFile(String relativePath) {

        Path path = buildAndCheckPath(relativePath);

        if (Files.isDirectory(path)) {
            throw new DroitException("File " + path
                    + " is a directory.  Expected a file.");
        }

        return path.toFile();
    }

    public static File createFile(String relativePath) {

        Path path = buildPath(relativePath);

        if (Files.exists(path)) {
            throw new DroitException(
                    "File "
                            + path
                            + " already exists.");
        }

        return path.toFile();
    }

    public static InputStream loadFileAsInputStream(String relativePath) {

        FileInputStream answer = null;

        try {
            answer = new FileInputStream(loadFile(relativePath));
        } catch (FileNotFoundException e) {
            throw new DroitException("File does not exist.", e);
        }

        return answer;
    }

    public static File loadDirectory(String relativePath) {

        Path path = buildAndCheckPath(relativePath);

        if (!Files.isDirectory(path)) {
            throw new DroitException(path
                    + " is a file.  Expected a directory.");
        }

        return path.toFile();
    }

    public static Path getAbsolutePath(String relativePath) {

        return Paths.get(getRootLocation(), relativePath);

    }


    public static Path buildPath(String relativePath) {
        Path path = null;

        if (useClasspath) {
            log.warn("Loading files from classpath instead of filesystem.  This should only be used during testing.");
            URL url = classloadedClass.getResource(relativePath);
            if (url == null) {
                throw new DroitException(relativePath
                        + " not found on classpath");
            }
            URI uri;
            try {
                uri = url.toURI();
            } catch (URISyntaxException e) {
                throw new DroitException("Could not convert url '" + url
                        + "' to uri", e);
            }
            path = Paths.get(uri);
        } else {
            path = getAbsolutePath(relativePath);
        }

        log.trace("Resolving relative path '{}' to absolute path '{}'",
                relativePath, path.toAbsolutePath().toString());
        return path;
    }

    private static Path buildAndCheckPath(String relativePath) {

        Path path = buildPath(relativePath);

        if (!Files.exists(path)) {
            throw new DroitException(
                    "File "
                            + path
                            + " does not exist.  Did you correctly initialize Droit Home?");
        } else if (!Files.isReadable(path)) {
            throw new DroitException("File " + path + " is not readable.");
        }

        return path;
    }

    private static String getRootLocation() {

        if (rootLocation == null) {
            synchronized (FilesystemDataLoader.class) {
                if (rootLocation == null) {
                    rootLocation = _determineRootLocation();
                }
            }
        }

        return rootLocation;
    }

    private static String _determineRootLocation() {

        String home = null;

        // check system property

        if (System.getProperty(SYS_PROP) != null) {
            home = System.getProperty(SYS_PROP);
            log.info("Setting Droit Home to '{}' from System Property '{}'",
                    home, SYS_PROP);
            return home;
        } else {
            log.debug("Droit Home not found in system property '{}'", SYS_PROP);
        }

        // check env property

        if (System.getenv(ENV_PROP) != null) {
            home = System.getenv(ENV_PROP);
            log.info(
                    "Setting Droit Home to '{}' from environment property '{}'",
                    home, ENV_PROP);
            return home;
        } else {
            log.debug("Droit Home not found in environment variable '{}'",
                    ENV_PROP);
        }

        // check default directory
        if (Files.exists(Paths.get(DEFAULT_DIR))) {
            log.info(
                    "Setting Droit Home to '{}' as this is the default "
                            + "and both system property '{}' and environment variable '{}' were not set.",
                    DEFAULT_DIR, SYS_PROP, ENV_PROP);

            home = DEFAULT_DIR;
        } else {
            log.debug("Default Droit Home directory '{}' does not exist",
                    DEFAULT_DIR);
        }

        if (home == null) {
            throw new DroitException(
                    String.format(
                            "Could not set Droit Home.  The default directory "
                                    + "'%s' does not exist, and neither system property "
                                    + "'%s' nor environment variable '%s' were set.",
                            DEFAULT_DIR, SYS_PROP, ENV_PROP));
        }

        return home;
    }

    /**
     * Set to true for unit tests
     *
     * @param useCp
     */
    public static void useClasspath(boolean useCp) {
        useClasspath = useCp;
    }

    public static void setClassloadedClass(Class classloadedClass) {
        FilesystemDataLoader.classloadedClass = classloadedClass;
    }

    public static byte[] decrypt(byte[] data, String key) {

        BasicBinaryEncryptor decryptor = new BasicBinaryEncryptor();
        decryptor.setPassword(key);
        byte[] answer = decryptor.decrypt(data);
        return answer;

    }
}
