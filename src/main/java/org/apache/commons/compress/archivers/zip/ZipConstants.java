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

    /** ZIP specification version that introduced ZIP64 */
    static final int ZIP64_MIN_VERSION = 45;

    /**
     * Value stored in two-byte size and similar fields if ZIP64
     * extensions are used.
     */
    static final int ZIP64_MAGIC_SHORT = 0xFFFF;

    /**
     * Value stored in four-byte size and similar fields if ZIP64
     * extensions are used.
     */
    static final long ZIP64_MAGIC = 0xFFFFFFFFL;

    /** Local file header signature */
    static final byte[] LFH_SIG = ZipLong.LFH_SIG.getBytes();
    
    /** Data descriptor signature */
    static final byte[] DD_SIG = ZipLong.DD_SIG.getBytes();
    
    /** Central file header signature */
    static final byte[] CFH_SIG = ZipLong.CFH_SIG.getBytes();
    
    /** End of central dir signature */
    static final byte[] EOCD_SIG = ZipLong.getBytes(0X06054B50L);
    
    /** ZIP64 end of central dir signature */
    static final byte[] ZIP64_EOCD_SIG = ZipLong.getBytes(0X06064B50L);
    
    /** ZIP64 end of central dir locator signature */
    static final byte[] ZIP64_EOCD_LOC_SIG = ZipLong.getBytes(0X07064B50L);
    

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
     * Maximum length of the "End of central directory record" with a
     * file comment.
     */
    static final int MAX_EOCD_SIZE = MIN_EOCD_SIZE
        /* maximum length of zipfile comment */ + ZIP64_MAGIC_SHORT;

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

    /**
     * Length of the "Zip64 end of central directory locator" - which
     * should be right in front of the "end of central directory
     * record" if one is present at all.
     */
    static final int ZIP64_EOCDL_LENGTH =
        /* zip64 end of central dir locator sig */ WORD
        /* number of the disk with the start    */
        /* start of the zip64 end of            */
        /* central directory                    */ + WORD
        /* relative offset of the zip64         */
        /* end of central directory record      */ + DWORD
        /* total number of disks                */ + WORD;

    /**
     * Offset of the field that holds the location of the "Zip64 end
     * of central directory record" inside the "Zip64 end of central
     * directory locator" relative to the start of the "Zip64 end of
     * central directory locator".
     */
    static final int ZIP64_EOCDL_LOCATOR_OFFSET =
        /* zip64 end of central dir locator sig */ WORD
        /* number of the disk with the start    */
        /* start of the zip64 end of            */
        /* central directory                    */ + WORD;

    /**
     * Offset of the field that holds the location of the first
     * central directory entry inside the "Zip64 end of central
     * directory record" relative to the start of the "Zip64 end of
     * central directory record".
     */
    static final int ZIP64_EOCD_CFD_LOCATOR_OFFSET =
        /* zip64 end of central dir        */
        /* signature                       */ WORD
        /* size of zip64 end of central    */
        /* directory record                */ + DWORD
        /* version made by                 */ + SHORT
        /* version needed to extract       */ + SHORT
        /* number of this disk             */ + WORD
        /* number of the disk with the     */
        /* start of the central directory  */ + WORD
        /* total number of entries in the  */
        /* central directory on this disk  */ + DWORD
        /* total number of entries in the  */
        /* central directory               */ + DWORD
        /* size of the central directory   */ + DWORD;

    static final int NIBLET_MASK = 0x0f;
    static final int BYTE_SHIFT = 8;
}
