/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package org.apache.commons.compress.archivers.zip;

import junit.framework.TestCase;

/**
 * JUnit 3 testcases for org.apache.commons.compress.archivers.zip.ZipEntry.
 *
 */
public class ZipArchiveEntryTest extends TestCase {

    public ZipArchiveEntryTest(String name) {
        super(name);
    }

    public void testUnixMode() {
        ZipArchiveEntry ze = new ZipArchiveEntry("foo");
        assertEquals(0, ze.getPlatform());
        ze.setUnixMode(0755);
        assertEquals(3, ze.getPlatform());
        assertEquals(0755,
                     (ze.getExternalAttributes() >> 16) & 0xFFFF);
        assertEquals(0, ze.getExternalAttributes()  & 0xFFFF);

        ze.setUnixMode(0444);
        assertEquals(3, ze.getPlatform());
        assertEquals(0444,
                     (ze.getExternalAttributes() >> 16) & 0xFFFF);
        assertEquals(1, ze.getExternalAttributes()  & 0xFFFF);

        ze = new ZipArchiveEntry("foo/");
        assertEquals(0, ze.getPlatform());
        ze.setUnixMode(0777);
        assertEquals(3, ze.getPlatform());
        assertEquals(0777,
                     (ze.getExternalAttributes() >> 16) & 0xFFFF);
        assertEquals(0x10, ze.getExternalAttributes()  & 0xFFFF);

        ze.setUnixMode(0577);
        assertEquals(3, ze.getPlatform());
        assertEquals(0577,
                     (ze.getExternalAttributes() >> 16) & 0xFFFF);
        assertEquals(0x11, ze.getExternalAttributes()  & 0xFFFF);
    }

    /**
     * Test case for
     * <a href="https://issues.apache.org/jira/browse/COMPRESS-94"
     * >COMPRESS-94</a>.
     */
    public void testNotEquals() {
        ZipArchiveEntry entry1 = new ZipArchiveEntry("foo");
        ZipArchiveEntry entry2 = new ZipArchiveEntry("bar");
        assertFalse(entry1.equals(entry2));
    }

    /**
     * Tests comment's influence on equals comparisons.
     * @see "https://issues.apache.org/jira/browse/COMPRESS-187"
     */
    public void testNullCommentEqualsEmptyComment() {
        ZipArchiveEntry entry1 = new ZipArchiveEntry("foo");
        ZipArchiveEntry entry2 = new ZipArchiveEntry("foo");
        ZipArchiveEntry entry3 = new ZipArchiveEntry("foo");
        entry1.setComment(null);
        entry2.setComment("");
        entry3.setComment("bar");
        assertEquals(entry1, entry2);
        assertFalse(entry1.equals(entry3));
        assertFalse(entry2.equals(entry3));
    }
}
