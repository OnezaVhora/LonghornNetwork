import java.io.*;
import java.util.*;

/**
 * DataParser class to read and
 * parse student data from a file.
 * It creates a list of UniversityStudent objects.
 */
public class DataParser {
    public static List<UniversityStudent> parseStudents(String filename) throws IOException {
        List<UniversityStudent> students = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String line;

        // Use a map to accumulate key-value pairs for each student
        Map<String, String> studentData = new HashMap<>();

        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty()) {
                // Skip empty lines
                continue;
            }

            if (line.equals("Student:")) {
                // If there is a previous student block, create UniversityStudent and add to list
                if (!studentData.isEmpty()) {
                    UniversityStudent student = UniversityStudent.fromMap(studentData);
                    students.add(student);
                    studentData.clear(); // Clear the map for the next student
                }
                continue; // Skip to the next line
            }

            // Expect lines in "Field: Value" format
            int colonIndex = line.indexOf(":");
            if (colonIndex == -1) {
                //throw new IllegalArgumentException("Invalid format in line: '" + line + "'. Expected 'Field: Value' format.");
                continue;
            }

            String key = line.substring(0, colonIndex).trim();
            String value = line.substring(colonIndex + 1).trim();
            studentData.put(key, value); // Add key-value pair to the map
        }

        // Process the last student entry if it exists
        if (!studentData.isEmpty()) {
            UniversityStudent student = UniversityStudent.fromMap(studentData);
            students.add(student);
        }

        reader.close(); // Close the reader
        return students; // Return the list of students
    }
}