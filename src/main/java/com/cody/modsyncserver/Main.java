package com.cody.modsyncserver;

import javax.swing.*;
import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class Main {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java -jar ModSyncServer.jar <file path> <url>");
            return;
        }
        File file;
        try {
            file = new File(args[0]);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Invalid file path", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (file.exists()) {
            if (!file.delete()) {
                JOptionPane.showMessageDialog(null, "Failed to delete file", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } else {
            JOptionPane.showMessageDialog(null, "File does not exist", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            File newFile = downloadFile(new URL(args[0]), file.getParentFile());
            // run the jar file
            ProcessBuilder pb = new ProcessBuilder(
                    System.getProperty("java.home") + File.separator + "bin" + File.separator + "java",
                    "-jar",
                    newFile.getAbsolutePath()
            );
            pb.directory(newFile.getParentFile().getParentFile());
            pb.start();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Failed to download file", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Assumes that the directory exists and is a directory, not a file
    private static File downloadFile(URL url, File directory) {

        String fileName = new File(url.getPath()).getName();
        File outputFile = new File(directory, fileName);

        HttpURLConnection connection = null;

        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            InputStream in = connection.getInputStream();
            Files.copy(in, outputFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            return outputFile;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Failed to download file " + fileName + ": " + e, "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}