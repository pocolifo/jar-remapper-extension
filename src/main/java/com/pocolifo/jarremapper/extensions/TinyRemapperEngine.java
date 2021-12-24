package com.pocolifo.jarremapper.extensions;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.pocolifo.jarremapper.Utility;
import com.pocolifo.jarremapper.engine.AbstractRemappingEngine;
import com.pocolifo.jarremapper.mapping.ClassMapping;
import com.pocolifo.jarremapper.mapping.FieldMapping;
import com.pocolifo.jarremapper.mapping.JarMapping;
import com.pocolifo.jarremapper.mapping.MethodMapping;
import net.fabricmc.tinyremapper.IMappingProvider;
import net.fabricmc.tinyremapper.OutputConsumerPath;
import net.fabricmc.tinyremapper.TinyRemapper;

public class TinyRemapperEngine extends AbstractRemappingEngine {
	private TinyRemapper.Builder options = TinyRemapper.newRemapper();
	private boolean excludeMetaInf;

	public TinyRemapperEngine setOptions(TinyRemapper.Builder options) {
		this.options = options;
		return this;
	}

	public TinyRemapperEngine excludeMetaInf() {
		this.excludeMetaInf = !this.excludeMetaInf;
		return this;
	}

	@Override
	public void remap() throws IOException {

		TinyRemapper remapper = this.options
				.withMappings(createProvider(this.mapping))
				.ignoreFieldDesc(true)
				.build();

		remapper.readInputs(this.inputFile.toPath());

		try (OutputConsumerPath path = new OutputConsumerPath.Builder(this.outputFile.toPath()).build()) {
			remapper.apply(path);
		} finally {
			remapper.finish();
		}

		// copy resources
		Map<String, String> env = new HashMap<>();
		env.put("create", "true");

		URI uri = URI.create("jar:" + this.outputFile.toURI());

		try (FileSystem fileSystem = FileSystems.newFileSystem(uri, env)) {
			try (ZipInputStream stream = new ZipInputStream(new FileInputStream(this.inputFile))) {
				for (ZipEntry entry; (entry = stream.getNextEntry()) != null;) {
					if (!entry.getName().endsWith(".class") && !entry.isDirectory()) {
						if (this.excludeMetaInf && entry.getName().startsWith("META-INF")) continue;

						Path path = fileSystem.getPath(entry.getName());

						if (path.getParent() != null) {
							Files.createDirectories(path.getParent());
						}

						Files.write(path, Utility.readInputStream(stream));
					}
				}
			}
		}
	}

	public static IMappingProvider createProvider(JarMapping mapping) {
		return acceptor -> {
			for (ClassMapping cls : mapping.classMappings) {
				acceptor.acceptClass(cls.fromClassName, cls.toClassName);

				for (FieldMapping fld : cls.fieldMappings) {
					acceptor.acceptField(new IMappingProvider.Member(cls.fromClassName, fld.fromFieldName, null), fld.toFieldName);
				}

				for (MethodMapping mtd : cls.methodMappings) {
					IMappingProvider.Member mtdMember = new IMappingProvider.Member(cls.fromClassName, mtd.fromMethodName, mtd.fromMethodDescriptor);

					acceptor.acceptMethod(mtdMember, mtd.toMethodName);

					/*
					TODO: does this work??

					for (int i = 0; mtd.parameterNames.size() > i; i++) {
						acceptor.acceptMethodArg(mtdMember, i, mtd.parameterNames.get(i));
					}
					*/
				}
			}
		};
	}
}
