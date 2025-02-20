package org.example.charitydonationsystem.utils;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class CSVUtil {

    /**
     * Writes data to a CSV file.
     *
     * @param filePath the destination path
     * @param headers  column headers
     * @param rows     list of string arrays (one per row)
     */
    public static void writeCSV(String filePath, String[] headers, List<String[]> rows) {
        try (FileWriter writer = new FileWriter(filePath)) {
            // Write headers
            writer.write(String.join(",", headers) + "\n");

            // Write data
            for (String[] row : rows) {
                writer.write(String.join(",", row) + "\n");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

