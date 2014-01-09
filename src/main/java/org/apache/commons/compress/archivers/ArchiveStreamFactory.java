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

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.utils.IOUtils;

/**
 * Factory to create Archive[In|Out]putStreams from names or the first bytes of
 * the InputStream. In order to add other implementations, you should extend
 * ArchiveStreamFactory and override the appropriate methods (and call their
 * implementation from super of course).
 * 
 * Compressing a ZIP-File:
 * 
 * <pre>
 * final OutputStream out = new FileOutputStream(output); 
 * ArchiveOutputStream os = new ArchiveStreamFactory().createArchiveOutputStream(ArchiveStreamFactory.ZIP, out);
 * 
 * os.putArchiveEntry(new ZipArchiveEntry("testdata/test1.xml"));
 * IOUtils.copy(new FileInputStream(file1), os);
 * os.closeArchiveEntry();
 *
 * os.putArchiveEntry(new ZipArchiveEntry("testdata/test2.xml"));
 * IOUtils.copy(new FileInputStream(file2), os);
 * os.closeArchiveEntry();
 * os.close();
 * </pre>
 * 
 * Decompressing a ZIP-File:
 * 
 * <pre>
 * final InputStream is = new FileInputStream(input); 
 * ArchiveInputStream in = new ArchiveStreamFactory().createArchiveInputStream(ArchiveStreamFactory.ZIP, is);
 * ZipArchiveEntry entry = (ZipArchiveEntry)in.getNextEntry();
 * OutputStream out = new FileOutputStream(new File(dir, entry.getName()));
 * IOUtils.copy(in, out);
 * out.close();
 * in.close();
 * </pre>
 * 
 * @Immutable
 */
public class ArchiveStreamFactory {

    /**
     * Constant used to identify the AR archive format.
     * @since 1.1
     */
    public static final String AR = "ar";
    /**
     * Constant used to identify the ARJ archive format.
     * @since 1.6
     */
    public static final String ARJ = "arj";
    /**
     * Constant used to identify the CPIO archive format.
     * @since 1.1
     */
    public static final String CPIO = "cpio";
    /**
     * Constant used to identify the Unix DUMP archive format.
     * @since 1.3
     */
    public static final String DUMP = "dump";
    /**
     * Constant used to identify the JAR archive format.
     * @since 1.1
     */
    public static final String JAR = "jar";
    /**
     * Constant used to identify the TAR archive format.
     * @since 1.1
     */
    public static final String TAR = "tar";
    /**
     * Constant used to identify the ZIP archive format.
     * @since 1.1
     */
    public static final String ZIP = "zip";

    /**
     * Entry encoding, null for the default.
     */
    private String entryEncoding = null;

    /**
     * Returns the encoding to use for arj, zip, dump, cpio and tar
     * files, or null for the default.
     *
     * @return entry encoding, or null
     * @since 1.5
     */
    public String getEntryEncoding() {
        return entryEncoding;
    }

    /**
     * Sets the encoding to use for arj, zip, dump, cpio and tar files. Use null for the default.
     * 
     * @param entryEncoding the entry encoding, null uses the default.
     * @since 1.5
     */
    public void setEntryEncoding(String entryEncoding) {
        this.entryEncoding = entryEncoding;
    }

    /**
     * Create an archive input stream from an archiver name and an input stream.
     * 
     * @param archiverName the archive name, i.e. "ar", "arj", "zip", "tar", "jar", "dump" or "cpio"
     * @param in the input stream
     * @return the archive input stream
     * @throws ArchiveException if the archiver name is not known
     * @throws IllegalArgumentException if the archiver name or stream is null
     */
    public ArchiveInputStream createArchiveInputStream(
            final String archiverName, final InputStream in)
            throws ArchiveException {

        if (archiverName == null) {
            throw new IllegalArgumentException("Archivername must not be null.");
        }

        if (in == null) {
            throw new IllegalArgumentException("InputStream must not be null.");
        }

        if (ZIP.equalsIgnoreCase(archiverName)) {
            if (entryEncoding != null) {
                return new ZipArchiveInputStream(in, entryEncoding);
            } else {
                return new ZipArchiveInputStream(in);
            }
        }

        throw new ArchiveException("Archiver: " + archiverName + " not found.");
    }

    /**
     * Create an archive input stream from an input stream, autodetecting
     * the archive type from the first few bytes of the stream. The InputStream
     * must support marks, like BufferedInputStream.
     * 
     * @param in the input stream
     * @return the archive input stream
     * @throws ArchiveException if the archiver name is not known
     * @throws IllegalArgumentException if the stream is null or does not support mark
     */
    public ArchiveInputStream createArchiveInputStream(final InputStream in)
            throws ArchiveException {
        if (in == null) {
            throw new IllegalArgumentException("Stream must not be null.");
        }

        if (!in.markSupported()) {
            throw new IllegalArgumentException("Mark is not supported.");
        }

        final byte[] signature = new byte[12];
        in.mark(signature.length);
        try {
            int signatureLength = IOUtils.readFully(in, signature);
            in.reset();
            if (ZipArchiveInputStream.matches(signature, signatureLength)) {
                if (entryEncoding != null) {
                    return new ZipArchiveInputStream(in, entryEncoding);
                } else {
                    return new ZipArchiveInputStream(in);
                }
            }

        } catch (IOException e) {
            throw new ArchiveException("Could not use reset and mark operations.", e);
        }

        throw new ArchiveException("No Archiver found for the stream signature");
    }

}
