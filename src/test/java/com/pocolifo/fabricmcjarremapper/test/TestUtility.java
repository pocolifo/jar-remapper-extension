package com.pocolifo.fabricmcjarremapper.test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import com.pocolifo.fabricmcjarremapper.TinyRemapperEngine;
import com.pocolifo.jarremapper.JarRemapper;
import com.pocolifo.jarremapper.mapping.JarMapping;
import com.pocolifo.jarremapper.engine.AbstractRemappingEngine;
import net.fabricmc.tinyremapper.TinyRemapper;

public class TestUtility {
	public static File getResourceAsFile(String resource) {
		try {
			return new File(ClassLoader.getSystemResource(resource).toURI());
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void remap(JarMapping mapping, File input, File output) throws IOException {
		JarRemapper.newRemap()
				.withRemappingEngine(new TinyRemapperEngine().setOptions(TinyRemapper.newRemapper()).excludeMetaInf())
				.withMappings(mapping)
				.withInputFile(input)
				.withOutputFile(output)
				.overwriteOutputFile()
				.remap();
	}

	public static void remap(JarMapping mapping, File input) throws IOException {
		remap(mapping, input, new File("output.jar"));
	}
}
