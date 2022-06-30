package com.vmtranslator.jasonbeh;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;

public class Main {
    public static void main(String[] args) {
        // No source file
        if (args.length == 0) {
            System.err.println("Usage: Main.java [source-file]");
            System.exit(1);
        }

        // Invalid source file
        File sourceFile = new File(args[0].trim());
        if (!sourceFile.exists()) {
            System.err.println("The specified source file doesn't exist");
            System.exit(2);
        }

        // Get output file
        String sourceAbsolutePath = sourceFile.getAbsolutePath();
        String fileName = sourceFile.getName();
        String fileNameWithoutExtension = fileName.substring(0, fileName.lastIndexOf("."));
        String sourceDirectory = sourceAbsolutePath.substring(0, sourceFile.getAbsolutePath().indexOf(sourceFile.getName()));

        // Get absolute path and add asm file
        String outputFileAbsolutePath = sourceDirectory + fileNameWithoutExtension + ".asm";
        System.out.println(outputFileAbsolutePath);
        File outputFile = new File(outputFileAbsolutePath);

        try {
            StringWriter message = new StringWriter();

            // Delete old file if exist
            if (outputFile.exists()) {
                if (outputFile.delete()) {
                    message.append("Deleted previous file at ").append(outputFileAbsolutePath);
                }
            }

            // Create new file
            if (outputFile.createNewFile()) {
                message.append("\nCreated new file at ").append(outputFileAbsolutePath);
            }

            long startTime = System.currentTimeMillis();

            // Translate source file.
            VMTranslator vmTranslator = new VMTranslator();
            vmTranslator.translate(sourceFile, outputFile);

            long endTime = System.currentTimeMillis();
            long elapsedTime = endTime - startTime;

            message.append("\nTranslated VM to ASM file in ").append(Long.toString(elapsedTime)).append("ms.");

            System.out.println(message);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(3);
        }
    }
}
