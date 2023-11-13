package org.pipservices4.persistence.util;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Helpers {
    public static String getFileExtension(String filename) {
        ArrayList<String> matchExt = new ArrayList<>();
        final String fileExtensionRegex = "/(?:\\.([^.]+))?$/";
        Matcher m = Pattern.compile(fileExtensionRegex).matcher(filename);
        while (m.find())
            matchExt.add(m.group());
        return !matchExt.isEmpty() ? matchExt.get(0) : null;
    }

    public static long getLinesUpToIndex(String file, Integer index) {
        if (index == null) {
            return 0;
        }

        final String fileUpToIndex = file.substring(0, index);
        return fileUpToIndex.split("\n").length - 1;
    }

    /**
     * Given a file and a string, find the line number of the string in the file.
     * 
     * @param file:          The file that we're searching in.
     * @param searchingText: The text to search for.
     * @param position:      The position in the file to start searching from.
     * @return A LineRange object.
     */
    public static LineRange getLineRange(String file, String searchingText, Integer position) {
        final int charAtStart = file.indexOf(searchingText, position);
        final String fileUpToStart = file.substring(0, charAtStart);

        final String fileUpToEnd = file.substring(0, charAtStart + searchingText.length());
        return new LineRange(fileUpToStart.split("\n").length - 1,
                fileUpToEnd.split("\n").length - 1);
    }
}