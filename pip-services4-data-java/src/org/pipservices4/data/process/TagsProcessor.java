package org.pipservices4.data.process;


import org.pipservices4.commons.reflect.ObjectReader;
import org.pipservices4.commons.reflect.PropertyReflector;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Helper class to extract and process search tags from objects.
 * The search tags can be kept individually or embedded as hash tags inside text
 * like "This text has #hash_tag that can be used for search."
 */
public class TagsProcessor {
    private static final String NORMALIZE_REGEX = "[_#]+";
    private static final String COMPRESS_REGEX = "[ _#]+";
    private static final String SPLIT_REGEX = "[,;]+";
    private static final String HASHTAG_REGEX = "#\\w+";

    /**
     * Normalizes a tag by replacing special symbols like '_' and '#' with spaces.
     * When tags are normalized then can be presented to user in similar shape and form.
     *
     * @param tag the tag to normalize.
     * @return a normalized tag.
     */
    public static String normalizeTag(String tag) {
        return tag != null
                ? tag.replaceAll(NORMALIZE_REGEX, " ").trim()
                : null;
    }

    /**
     * Compress a tag by removing special symbols like spaces, '_' and '#'
     * and converting the tag to lower case.
     * When tags are compressed they can be matched in search queries.
     *
     * @param tag the tag to compress.
     * @return a compressed tag.
     */
    public static String compressTag(String tag) {
        return tag != null
                ? tag.replaceAll(COMPRESS_REGEX, "").toLowerCase(Locale.ROOT)
                : null;
    }

    /**
     * Compares two tags using their compressed form.
     *
     * @param tag1 the first tag.
     * @param tag2 the second tag.
     * @return true if the tags are equal and false otherwise.
     */
    public static Boolean equalTags(String tag1, String tag2) {
        if (tag1 == null && tag2 == null)
            return true;
        if (tag1 == null || tag2 == null)
            return false;
        return Objects.equals(TagsProcessor.compressTag(tag1), TagsProcessor.compressTag(tag2));
    }

    /**
     * Normalizes a list of tags.
     *
     * @param tags the tags to normalize.
     * @return a list with normalized tags.
     */
    public static List<String> normalizeTags(List<String> tags) {
        ArrayList<String> normalizedTags = new ArrayList<>();
        for (String tag : tags)
            normalizedTags.add(TagsProcessor.normalizeTag(tag));

        return normalizedTags;
    }

    /**
     * Normalizes a comma-separated list of tags.
     *
     * @param tagList a comma-separated list of tags to normalize.
     * @return a list with normalized tags.
     */
    public static List<String> normalizeTagList(String tagList) {
        List<String> tags = List.of(tagList.split(SPLIT_REGEX, -1));
        return normalizeTags(tags);
    }


    /**
     * Compresses a list of tags.
     *
     * @param tags the tags to compress.
     * @return a list with normalized tags.
     */
    public static List<String> compressTags(List<String> tags) {
        List<String> compressedTags = new ArrayList<>();

        for (String tag : tags)
            compressedTags.add(TagsProcessor.compressTag(tag));

        return compressedTags;
    }

    /**
     * Compresses a comma-separated list of tags.
     *
     * @param tagList a comma-separated list of tags to compress.
     * @return a list with compressed tags.
     */
    public static List<String> compressTagList(String tagList) {
        List<String> tags = List.of(tagList.split(SPLIT_REGEX, -1));
        return compressTags(tags);
    }

    /**
     * Extracts hash tags from a text.
     *
     * @param text a text that contains hash tags
     * @return a list with extracted and compressed tags.
     */
    public static List<String> extractHashTags(String text) {
        List<String> tags = new ArrayList<>();
        Matcher m = Pattern.compile(HASHTAG_REGEX).matcher(text);
        if (!text.isEmpty()) {
            while (m.find())
                tags.add(m.group());
        }

        tags = compressTags(tags);

        return new ArrayList<>(new HashSet<>(tags));
    }

    private static String extractString(Object field) {
        StringBuilder result = new StringBuilder();

        if (field == null) return "";
        if (field instanceof String) return (String) field;
        if (field instanceof Map) {
            for (Object prop : ((Map<?, ?>) field).keySet())
                result.append(" ").append(extractString(((Map<?, ?>) field).get(prop)));
            return result.toString();
        }
        if (!(field instanceof Iterable)) return "";

        Map<String, Object> properties = PropertyReflector.getProperties(field);

        for (String prop : properties.keySet())
            result.append(" ").append(extractString(properties.get(prop)));

        return result.toString();
    }


    /**
     * Extracts hash tags from selected fields in an object.
     *
     * @param obj          an object which contains hash tags.
     * @param searchFields a list of fields in the objects where to extract tags
     * @return a list of extracted and compressed tags.
     */

    @SuppressWarnings("unchecked")
    public static List<String> extractHashTagsFromValue(Object obj, List<String> searchFields) {
        List<String> value = (List<String>) ObjectReader.getProperty(obj, "tags");

        // Todo: Use recursive
        List<String> tags = TagsProcessor.compressTags(value);


        for (String field : searchFields) {
            String text;
            if (obj instanceof Map)
                text = extractString(((Map<?, ?>) obj).get(field));
            else
                text = extractString(PropertyReflector.getProperty(obj, field));


            if (!text.isEmpty()) {
                ArrayList<String> matchTags = new ArrayList<>();
                Matcher m = Pattern.compile(HASHTAG_REGEX).matcher(text);

                while (m.find())
                    matchTags.add(m.group());

                tags.addAll(compressTags(matchTags));

            }
        }

        return new ArrayList<>(new HashSet<>(tags));
    }

}
