// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package io.github.lmos.arc.client.gemini;

import com.google.cloud.vertexai.api.Part;
import com.google.cloud.vertexai.generativeai.PartMaker;

/**
 * Workaround for the mapping kotlin byte arrays to java byte arrays.
 */
public class ByteMapper {

    public static Part map(String mimeType, byte[] data) {
        return PartMaker.fromMimeTypeAndData(mimeType, data);
    }
}
