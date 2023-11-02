package org.pipservices4.expressions.io;

import org.junit.Before;
import org.junit.Test;

public class StringScannerTest {

    private String content;
    private StringScanner scanner;
    private ScannerFixture fixture;

    @Before
    public void setup() {
        content = "Test String\nLine2\rLine3\r\n\r\nLine5";
        scanner = new StringScanner(content);
        fixture = new ScannerFixture(scanner, content);
    }

    @Test
    public void testRead() {
        fixture.testRead();
    }

    @Test
    public void testUnread() {
        fixture.testUnread();
    }

    @Test
    public void testLineColumn() {
        fixture.testLineColumn(3, 's', 1, 3);
        fixture.testLineColumn(12, '\n', 2, 0);
        fixture.testLineColumn(15, 'n', 2, 3);
        fixture.testLineColumn(21, 'n', 3, 3);
        fixture.testLineColumn(26, '\r', 4, 0);
        fixture.testLineColumn(30, 'n', 5, 3);
    }
}
