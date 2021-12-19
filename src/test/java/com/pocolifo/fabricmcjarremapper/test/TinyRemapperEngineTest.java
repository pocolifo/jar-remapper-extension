package com.pocolifo.fabricmcjarremapper.test;

import java.io.File;
import java.io.IOException;

import com.pocolifo.fabricmcjarremapper.TinyRemapperEngine;
import com.pocolifo.jarremapper.JarRemapper;
import com.pocolifo.jarremapper.mapping.JarMapping;
import com.pocolifo.jarremapper.reader.mcp.McpMappingReader;
import net.fabricmc.tinyremapper.TinyRemapper;
import org.junit.jupiter.api.Test;
import static com.pocolifo.fabricmcjarremapper.test.TestUtility.getResourceAsFile;

class TinyRemapperEngineTest {
	@Test
	void main() throws IOException {
		McpMappingReader reader = new McpMappingReader(
				getResourceAsFile("mappings/mcp/1.8.9/joined.srg"),
				getResourceAsFile("mappings/mcp/1.8.9/joined.exc"),

				getResourceAsFile("mappings/mcp/1.8.9/methods.csv"),
				getResourceAsFile("mappings/mcp/1.8.9/fields.csv"),
				getResourceAsFile("mappings/mcp/1.8.9/params.csv")
		);

		TestUtility.remap(
				reader.read(),
				getResourceAsFile("minecraft-1.8.9.jar")
		);

		JarMapping reversed = reader.read();
		reversed.reverse();

		TestUtility.remap(
				reversed,
				new File("output.jar"),
				new File("reversed.jar")
		);
	}
}