package com.droitfintech.licensing;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.droitfintech.exceptions.DroitException;
import com.droitfintech.utils.FilesystemDataLoader;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;


public class ObfuscatedLicenseProvider implements LicenseProvider {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private String filename = "client/adeptWebTool/droit-license.txt";

    public License retrieveLicense() {

        License answer;

        InputStream stream = FilesystemDataLoader
                .loadFileAsInputStream(filename);

        String obfuscated;
        try {
            obfuscated = IOUtils.toString(stream);
        } catch (IOException e) {
            throw new DroitException(
                    "Unable to convert stream to string for file '" + filename
                            + "'", e);
        }

        try {
            answer = decodeObfuscatedLicense(obfuscated);
        } catch (Exception e) {
            throw new DroitException(
                    "Unable to deserialize '" + filename + "'", e);
        }

        return answer;
    }

    public License decodeObfuscatedLicense(String obfuscated) {
        String plaintext;

        try {
            plaintext = new String(Base64.decodeBase64(obfuscated), "UTF-8");
            logger.debug(plaintext);
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(plaintext, License.class);
        } catch (Exception e) {
            throw new DroitException("Unable to deserialize '" + obfuscated
                    + "'", e);
        }
    }
}
