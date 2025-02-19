package com.cody.jarupdater;

import javax.swing.*;
import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class Main {
    public static void main(String[] args) {
        // Wait for other process to stop
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            JOptionPane.showMessageDialog(null, "Failed to wait for other process to stop", "Error", JOptionPane.ERROR_MESSAGE);
        }
//======================================================================================================================
        // Check args
        if (args.length != 2) {
            System.out.println("Usage: java -jar ModSyncServer.jar <file path> <url>");
            return;
        }
//======================================================================================================================
        // Get old file
        File file;
        try {
            file = new File(args[0]);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Invalid file path", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
//======================================================================================================================
        // Download new file
        File newFile = null;
        try {
            newFile = downloadFile(new URL(args[1]), file.getParentFile());
        } catch (MalformedURLException e) {
            JOptionPane.showMessageDialog(null, "Failed to download file", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        if (newFile == null) {
            JOptionPane.showMessageDialog(null, "Failed to download file", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
//======================================================================================================================
        // Delete old file
        if (file.exists()) {
            if (!file.delete()) {
                JOptionPane.showMessageDialog(null, "Downloaded new file, but failed to delete the old one!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } else {
            JOptionPane.showMessageDialog(null, "Downloaded new file, but the original file does not exist!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
//======================================================================================================================
        // Show success message
        JOptionPane.showMessageDialog(null, "Update completed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    // Assumes that the directory exists and is a directory, not a file
    private static File downloadFile(URL url, File directory) {


        HttpURLConnection connection = null;

        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);

            String fileName = getFileNameFromHeader(connection);
            if (fileName == null) {
                fileName = Paths.get(url.getPath()).getFileName().toString();
            }

            File outputFile = new File(directory, fileName);

            InputStream in = connection.getInputStream();
            Files.copy(in, outputFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            return outputFile;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Failed to download file: "+ e, "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private static String getFileNameFromHeader(HttpURLConnection connection) {
        String contentDisposition = connection.getHeaderField("Content-Disposition");
        if (contentDisposition != null && contentDisposition.contains("filename=")) {
            String[] parts = contentDisposition.split(";");
            for (String part : parts) {
                if (part.trim().startsWith("filename=")) {
                    return part.split("=")[1].trim().replace("\"", "");
                }
            }
        }
        return null;
    }
}