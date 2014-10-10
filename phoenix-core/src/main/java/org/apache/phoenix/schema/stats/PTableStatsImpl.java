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
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.phoenix.schema.stats;

import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.hadoop.hbase.util.Bytes;
import org.apache.phoenix.util.SizedUtil;

import com.sun.istack.NotNull;
 
 /**
 * Implementation for PTableStats.
 */
public class PTableStatsImpl implements PTableStats {
    private final SortedMap<byte[], List<byte[]>> guidePosts;
    private final int estimatedSize;

    public PTableStatsImpl() {
        this(new TreeMap<byte[], List<byte[]>>(Bytes.BYTES_COMPARATOR));
    }

    public PTableStatsImpl(@NotNull SortedMap<byte[], List<byte[]>> guidePosts) {
        this.guidePosts = guidePosts;
        int estimatedSize = SizedUtil.OBJECT_SIZE + SizedUtil.INT_SIZE + SizedUtil.sizeOfTreeMap(guidePosts.size());
        for (Map.Entry<byte[], List<byte[]>> entry : guidePosts.entrySet()) {
            byte[] cf = entry.getKey();
            estimatedSize += SizedUtil.ARRAY_SIZE + cf.length;
            List<byte[]> keys = entry.getValue();
            estimatedSize += SizedUtil.sizeOfArrayList(keys.size());
            for (byte[] key : keys) {
                estimatedSize += SizedUtil.ARRAY_SIZE + key.length;
            }
        }
        this.estimatedSize = estimatedSize;
    }

    @Override
    public SortedMap<byte[], List<byte[]>> getGuidePosts() {
        return guidePosts;
    }
    
    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("PTableStats [");
        for (Map.Entry<byte[], List<byte[]>> entry : guidePosts.entrySet()) {
            buf.append(Bytes.toStringBinary(entry.getKey()));
            buf.append(":(");
            List<byte[]> keys = entry.getValue();
            if (!keys.isEmpty()) {
                for (byte[] key : keys) {
                    buf.append(Bytes.toStringBinary(key));
                    buf.append(",");
                }
                buf.setLength(buf.length()-1);
            }
            buf.append(")");
        }
        buf.append("]");
        return buf.toString();
    }

    @Override
    public int getEstimatedSize() {
        return estimatedSize;
    }
    
}
