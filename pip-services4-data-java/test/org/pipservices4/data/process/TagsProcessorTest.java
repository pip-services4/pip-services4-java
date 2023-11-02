package org.pipservices4.data.process;

import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TagsProcessorTest {
    public class TestTags {
        public TestTags(List<String> tags, Object name, Object description) {
            this.tags = tags;
            this.name = name;
            this.description = description;
        }

        public List<String> tags;
        public Object name;
        public Object description;
    }

    @Test
    public void testNormalizeTags() {
        String tag = TagsProcessor.normalizeTag("  A_b#c ");
        assertEquals("A b c", tag);

        List<String> tags = TagsProcessor.normalizeTags(List.of("  A_b#c ", "d__E f"));
        assertEquals(tags.size(), 2);
        assertEquals("A b c", tags.get(0));
        assertEquals("d E f", tags.get(1));

        tags = TagsProcessor.normalizeTagList("  A_b#c ,d__E f;;");
        assertEquals(tags.size(), 3);
        assertEquals("A b c", tags.get(0));
        assertEquals("d E f", tags.get(1));
    }

    @Test
    public void testCompressTags() {
        String tag = TagsProcessor.compressTag("  A_b#c ");
        assertEquals("abc", tag);

        List<String> tags = TagsProcessor.compressTags(List.of("  A_b#c ", "d__E f"));
        assertEquals(tags.size(), 2);
        assertEquals("abc", tags.get(0));
        assertEquals("def", tags.get(1));

        tags = TagsProcessor.compressTagList("  A_b#c ,d__E f;;");
        assertEquals(tags.size(), 3);
        assertEquals("abc", tags.get(0));
        assertEquals("def", tags.get(1));
    }

    @Test
    public void testExtractTags() {
        List<String> tags = TagsProcessor.extractHashTags("  #Tag_1  #TAG2#tag3 ");
        assertEquals(tags.size(), 3);
        assertEquals("tag1", tags.get(0));
        assertEquals("tag2", tags.get(1));
        assertEquals("tag3", tags.get(2));

        // Test with Map
        tags = TagsProcessor.extractHashTagsFromValue(
                Map.of(
                        "tags", List.of("Tag 1", "tag_2", "TAG3"),
                        "name", "Text with tag1 #Tag1",
                        "description", "Text with #tag_2,#tag3-#tag4 and #TAG__5"
                ),
                List.of("name", "description")
        );

        assertTrue(tags.containsAll(List.of("tag1", "tag2", "tag3", "tag4", "tag5")));

        // Test with Object
        tags = TagsProcessor.extractHashTagsFromValue(
                new TestTags(
                        List.of("Tag 1", "tag_2", "TAG3"),
                        "Text with tag1 #Tag1",
                        "Text with #tag_2,#tag3-#tag4 and #TAG__5"),
                List.of("name", "description")
        );

        assertTrue(tags.containsAll(List.of("tag1", "tag2", "tag3", "tag4", "tag5")));
    }

    @Test
    public void testExtractTagsFromObj() {
        // Test with Map
        List<String> tags = TagsProcessor.extractHashTagsFromValue(
                Map.of(
                        "tags", List.of("Tag 1", "tag_2", "TAG3"),
                        "name", Map.of("short", "Just a name", "full", "Text with tag1 #Tag1"),
                        "description", Map.of("en", "Text with #tag_2,#tag4 and #TAG__5",
                                "ru", "Текст с #tag_2,#tag3 и #TAG__5"
                        )
                ),
                List.of("name", "description")
        );

        assertTrue(tags.containsAll(List.of("tag1", "tag2", "tag3", "tag4", "tag5")));

        // Test with Object
        tags = TagsProcessor.extractHashTagsFromValue(
                new TestTags(
                        List.of("Tag 1", "tag_2", "TAG3"),
                        Map.of("short", "Just a name", "full", "Text with tag1 #Tag1"),
                        Map.of(
                                "en", "Text with #tag_2,#tag4 and #TAG__5",
                                "ru", "Текст с #tag_2,#tag3 и #TAG__5"
                        )
                ),
                List.of("name", "description")
        );

        assertTrue(tags.containsAll(List.of("tag1", "tag2", "tag3", "tag4", "tag5")));
    }
}
