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

/**
 * Various constants used throughout the package.
 *
 * @since 1.3
 */
final class ZipConstants {
    private ZipConstants() { }

    /** Masks last eight bits */
    static final int BYTE_MASK = 0xFF;

    /** length of a ZipShort in bytes */
    static final int SHORT = 2;

    /** length of a ZipLong in bytes */
    static final int WORD = 4;

    /** length of a ZipEightByteInteger in bytes */
    static final int DWORD = 8;

    /** Initial ZIP specification version */
    static final int INITIAL_VERSION = 10;

    /** ZIP specification version that introduced data descriptor method */
    static final int DATA_DESCRIPTOR_MIN_VERSION = 20;

    /** Local file header signature */
    static final byte[] LFH_SIG = ZipLong.LFH_SIG.getBytes();
    
    /** Data descriptor signature */
    static final byte[] DD_SIG = ZipLong.DD_SIG.getBytes();
    
    /** Central file header signature */
    static final byte[] CFH_SIG = ZipLong.CFH_SIG.getBytes();
    
    /** End of central dir signature */
    static final byte[] EOCD_SIG = ZipLong.getBytes(0X06054B50L);
    
    /**
     * Length of the "End of central directory record" - which is
     * supposed to be the last structure of the archive - without file
     * comment.
     */
    static final int MIN_EOCD_SIZE =
        /* end of central dir signature    */ WORD
        /* number of this disk             */ + SHORT
        /* number of the disk with the     */
        /* start of the central directory  */ + SHORT
        /* total number of entries in      */
        /* the central dir on this disk    */ + SHORT
        /* total number of entries in      */
        /* the central dir                 */ + SHORT
        /* size of the central directory   */ + WORD
        /* offset of start of central      */
        /* directory with respect to       */
        /* the starting disk number        */ + WORD
        /* zipfile comment length          */ + SHORT;

    /**
     * Offset of the field that holds the location of the first
     * central directory entry inside the "End of central directory
     * record" relative to the start of the "End of central directory
     * record".
     */
    static final int CFD_LOCATOR_OFFSET =
        /* end of central dir signature    */ WORD
        /* number of this disk             */ + SHORT
        /* number of the disk with the     */
        /* start of the central directory  */ + SHORT
        /* total number of entries in      */
        /* the central dir on this disk    */ + SHORT
        /* total number of entries in      */
        /* the central dir                 */ + SHORT
        /* size of the central directory   */ + WORD;

    static final int NIBLET_MASK = 0x0f;
    static final int BYTE_SHIFT = 8;
}
