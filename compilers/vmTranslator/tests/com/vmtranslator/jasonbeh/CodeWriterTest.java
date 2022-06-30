package com.vmtranslator.jasonbeh;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.*;

import static org.junit.Assert.assertEquals;

public class CodeWriterTest {

    private BufferedWriter writer;
    private File currentFile;

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Before
    public void init() throws IOException {
        currentFile = folder.newFile();
    }

    @Test
    public void testWritingToFile() throws IOException {
        CodeWriter codeWriter = new CodeWriter(currentFile);
        codeWriter.writeBootstrapCode();
        codeWriter.close();

        BufferedReader reader = new BufferedReader(new FileReader(currentFile));
        String line = reader.readLine();
        assertEquals("@256", line);
        line = reader.readLine();
        assertEquals("D=A", line);
        line = reader.readLine();
        assertEquals("@SP", line);
        line = reader.readLine();
        assertEquals("M=D", line);
    }

}
