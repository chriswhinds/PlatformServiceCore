package com.droitfintech.dataframes.sources;

import java.io.InputStream;
import java.nio.file.Paths;

import com.droitfintech.exceptions.DroitException;
import com.droitfintech.utils.FilesystemDataLoader;
import com.droitfintech.dataframes.Dataframe;


public class FilesystemDataframeSource implements DataframeSource {

	private String sourceDir;


	public void initialize() {
		DroitException.assertNotNull(sourceDir, "Data root dir not specified");
	}


	public Dataframe load(String name) {
            try {
                String path = Paths.get(sourceDir, name + ".csv").toString();
                InputStream stream = FilesystemDataLoader.loadFileAsInputStream(path);
                Dataframe frame = Dataframe.fromCsv(stream);
                frame.setName(name);
                return frame;
            } catch (Exception e) {
                throw new DroitException("Error loading file: " + name, e);
            }
	}

	public String getSourceDir() {
		return sourceDir;
	}

	public void setSourceDir(String sourceDir) {
		this.sourceDir = sourceDir;
	}

	public boolean isInitialized() {
		return sourceDir != null;
	}
}
