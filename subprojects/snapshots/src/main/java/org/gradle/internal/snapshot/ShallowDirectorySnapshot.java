/*
 * Copyright 2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.internal.snapshot;

import com.google.common.collect.ImmutableList;
import org.gradle.internal.file.FileType;

import java.util.List;
import java.util.Optional;

/**
 * An incomplete snapshot of an existing directory.
 *
 * We know all the metadata of its children, but the child snapshots are not necessarily complete, nor have we calculated a Merkle hash for the directory.
 */
public class ShallowDirectorySnapshot extends AbstractIncompleteSnapshotWithChildren implements MetadataSnapshot {

    public ShallowDirectorySnapshot(String pathToParent, List<? extends FileSystemNode> children) {
        super(pathToParent, children);
    }

    @Override
    protected Optional<MetadataSnapshot> getThisSnapshot() {
        return Optional.of(this);
    }

    @Override
    public Optional<MetadataSnapshot> getSnapshot(String absolutePath, int offset) {
        return Optional.of(super.getSnapshot(absolutePath, offset)
            .orElseGet(() -> AbstractCompleteFileSystemLocationSnapshot.missingSnapshotForAbsolutePath(absolutePath)));
    }

    @Override
    protected FileSystemNode createCopy(String prefix, List<? extends FileSystemNode> newChildren) {
        return new PartialDirectorySnapshot(prefix, newChildren);
    }

    @Override
    protected Optional<FileSystemNode> withNoChildren() {
        return Optional.of(new PartialDirectorySnapshot(getPathToParent(), ImmutableList.of()));
    }

    @Override
    protected FileSystemNode withUnkownChildInvalidated() {
        return new PartialDirectorySnapshot(getPathToParent(), children);
    }

    @Override
    public FileType getType() {
        return FileType.Directory;
    }
}