/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.commons.compress.archivers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.compress.AbstractTestCase;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipMethod;
import org.apache.commons.compress.utils.IOUtils;

public final class ZipTestCase extends AbstractTestCase {

    /**
     * Simple unarchive test. Asserts nothing.
     * @throws Exception
     */
    public void testZipUnarchive() throws Exception {
        final File input = getFile("bla.zip");
        final InputStream is = new FileInputStream(input);
        final ArchiveInputStream in = new ZipArchiveInputStream(is);
        final ZipArchiveEntry entry = (ZipArchiveEntry)in.getNextEntry();
        final OutputStream out = new FileOutputStream(new File(dir, entry.getName()));
        IOUtils.copy(in, out);
        out.close();
        in.close();
    }

    /**
     * Test case for 
     * <a href="https://issues.apache.org/jira/browse/COMPRESS-208"
     * >COMPRESS-208</a>.
     */
    public void testSkipsPK00Prefix() throws Exception {
        final File input = getFile("COMPRESS-208.zip");
        InputStream is = new FileInputStream(input);
        ArrayList<String> al = new ArrayList<String>();
        al.add("test1.xml");
        al.add("test2.xml");
        try {
            checkArchiveContent(new ZipArchiveInputStream(is), al);
        } finally {
            is.close();
        }
    }

    /**
     * Test case for being able to skip an entry in an 
     * {@link ZipArchiveInputStream} even if the compression method of that
     * entry is unsupported.
     *
     * @see <a href="https://issues.apache.org/jira/browse/COMPRESS-93"
     *        >COMPRESS-93</a>
     */
    public void testSkipEntryWithUnsupportedCompressionMethod()
            throws IOException {
        ZipArchiveInputStream zip =
            new ZipArchiveInputStream(new FileInputStream(getFile("moby.zip")));
        try {
            ZipArchiveEntry entry = zip.getNextZipEntry();
            assertEquals("method", ZipMethod.TOKENIZATION.getCode(), entry.getMethod());
            assertEquals("README", entry.getName());
            assertFalse(zip.canReadEntryData(entry));
            try {
                assertNull(zip.getNextZipEntry());
            } catch (IOException e) {
                e.printStackTrace();
                fail("COMPRESS-93: Unable to skip an unsupported zip entry");
            }
        } finally {
            zip.close();
        }
    }

    /**
     * Checks if all entries from a nested archive can be read.
     * The archive: OSX_ArchiveWithNestedArchive.zip contains:
     * NestedArchiv.zip and test.xml3.
     * 
     * The nested archive:  NestedArchive.zip contains test1.xml and test2.xml
     * 
     * @throws Exception
     */
    public void testListAllFilesWithNestedArchive() throws Exception {
        final File input = getFile("OSX_ArchiveWithNestedArchive.zip");

        List<String> results = new ArrayList<String>();

        final InputStream is = new FileInputStream(input);
        ArchiveInputStream in = null;
        try {
            in = new ZipArchiveInputStream(is);

            ZipArchiveEntry entry = null;
            while((entry = (ZipArchiveEntry)in.getNextEntry()) != null) {
                results.add(entry.getName());

                ArchiveInputStream nestedIn = new ZipArchiveInputStream(in);
                ZipArchiveEntry nestedEntry = null;
                while((nestedEntry = (ZipArchiveEntry)nestedIn.getNextEntry()) != null) {
                    results.add(nestedEntry.getName());
                }
               // nested stream must not be closed here
            }
        } finally {
            if (in != null) {
                in.close();
            }
        }
        is.close();

        results.contains("NestedArchiv.zip");
        results.contains("test1.xml");
        results.contains("test2.xml");
        results.contains("test3.xml");
    }

}
