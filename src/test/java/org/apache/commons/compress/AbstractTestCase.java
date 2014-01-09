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
package org.apache.commons.compress;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Locale;

import junit.framework.TestCase;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.utils.IOUtils;

public abstract class AbstractTestCase extends TestCase {

    protected File dir;
    protected File resultDir;

    private File archive; // used to delete the archive in tearDown
    protected List<String> archiveList; // Lists the content of the archive as originally created

    protected ArchiveStreamFactory factory = new ArchiveStreamFactory();

    public AbstractTestCase() {
    }

    public AbstractTestCase(String name) {
        super(name);
    }

    @Override
    protected void setUp() throws Exception {
        dir = mkdir("dir");
        resultDir = mkdir("dir-result");
        archive = null;
    }

    public static File mkdir(String name) throws IOException {
        File f = File.createTempFile(name, "");
        f.delete();
        f.mkdir();
        return f;
    }

    public static File getFile(String path) throws IOException {
        URL url = AbstractTestCase.class.getClassLoader().getResource(path);
        if (url == null) {
            throw new FileNotFoundException("couldn't find " + path);
        }
        URI uri = null;
        try {
            uri = url.toURI();
        } catch (java.net.URISyntaxException ex) {
//          throw new IOException(ex); // JDK 1.6+
            IOException ioe = new IOException();
            ioe.initCause(ex);
            throw ioe;
        }
        return new File(uri);
    }

    @Override
    protected void tearDown() throws Exception {
        rmdir(dir);
        rmdir(resultDir);
        dir = resultDir = null;
        if (!tryHardToDelete(archive)) {
            // Note: this exception won't be shown if the test has already failed
            throw new Exception("Could not delete "+archive.getPath());
        }
    }

    public static void rmdir(File f) {
        String[] s = f.list();
        if (s != null) {
            for (String element : s) {
                final File file = new File(f, element);
                if (file.isDirectory()){
                    rmdir(file);
                }
                boolean ok = tryHardToDelete(file);
                if (!ok && file.exists()){
                    System.out.println("Failed to delete "+element+" in "+f.getPath());
                }
            }
        }
        tryHardToDelete(f); // safer to delete and check
        if (f.exists()){
            throw new Error("Failed to delete "+f.getPath());
        }
    }

    private static final boolean ON_WINDOWS =
        System.getProperty("os.name").toLowerCase(Locale.ENGLISH)
        .indexOf("windows") > -1;

    /**
     * Accommodate Windows bug encountered in both Sun and IBM JDKs.
     * Others possible. If the delete does not work, call System.gc(),
     * wait a little and try again.
     *
     * @return whether deletion was successful
     * @since Stolen from FileUtils in Ant 1.8.0
     */
    public static boolean tryHardToDelete(File f) {
        if (f != null && f.exists() && !f.delete()) {
            if (ON_WINDOWS) {
                System.gc();
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
                // Ignore Exception
            }
            return f.delete();
        }
        return true;
    }

    /**
     * Checks if an archive contains all expected files.
     *
     * @param archive
     *            the archive to check
     * @param expected
     *            a list with expected string filenames
     * @throws Exception
     */
    protected void checkArchiveContent(File archive, List<String> expected)
            throws Exception {
        final InputStream is = new FileInputStream(archive);
        try {
            final BufferedInputStream buf = new BufferedInputStream(is);
            final ArchiveInputStream in = factory.createArchiveInputStream(buf);
            this.checkArchiveContent(in, expected);
        } finally {
            is.close();
        }
    }

    /**
     * Checks that an archive input stream can be read, and that the file data matches file sizes.
     *
     * @param in
     * @param expected list of expected entries or {@code null} if no check of names desired
     * @throws Exception
     */
    protected void checkArchiveContent(ArchiveInputStream in, List<String> expected)
            throws Exception {
        checkArchiveContent(in, expected, true);
    }

    /**
     * Checks that an archive input stream can be read, and that the file data matches file sizes.
     *
     * @param in
     * @param expected list of expected entries or {@code null} if no check of names desired
     * @param cleanUp Cleans up resources if true
     * @return returns the created result file if cleanUp = false, or null otherwise
     * @throws Exception
     */
    protected File checkArchiveContent(ArchiveInputStream in, List<String> expected, boolean cleanUp)
            throws Exception {
        File result = mkdir("dir-result");
        result.deleteOnExit();

        try {
            ArchiveEntry entry = null;
            while ((entry = in.getNextEntry()) != null) {
                File outfile = new File(result.getCanonicalPath() + "/result/"
                        + entry.getName());
                long copied=0;
                if (entry.isDirectory()){
                    outfile.mkdirs();
                } else {
                    outfile.getParentFile().mkdirs();
                    OutputStream out = new FileOutputStream(outfile);
                    try {
                        copied=IOUtils.copy(in, out);
                    } finally {
                        out.close();
                    }
                }
                final long size = entry.getSize();
                if (size != ArchiveEntry.SIZE_UNKNOWN) {
                    assertEquals("Entry.size should equal bytes read.",size, copied);
                }

                if (!outfile.exists()) {
                    fail("extraction failed: " + entry.getName());
                }
                if (expected != null && !expected.remove(getExpectedString(entry))) {
                    fail("unexpected entry: " + getExpectedString(entry));
                }
            }
            in.close();
            if (expected != null && expected.size() > 0) {
                for (String name : expected) {
                    fail("Expected entry: " + name);
                }
            }
            if (expected != null) {
                assertEquals(0, expected.size());
            }
        } finally {
            if (cleanUp) {
                rmdir(result);
            }
        }
        return result;
    }

    /**
     * Override this method to change what is to be compared in the List.
     * For example, size + name instead of just name.
     *
     * @param entry
     * @return returns the entry name
     */
    protected String getExpectedString(ArchiveEntry entry) {
        return entry.getName();
    }

}
