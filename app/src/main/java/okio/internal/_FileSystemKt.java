package okio.internal;

import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import kotlin.ExceptionsKt;
import kotlin.Metadata;
import kotlin.collections.ArrayDeque;
import kotlin.jvm.internal.Intrinsics;
import kotlin.sequences.Sequence;
import kotlin.sequences.SequencesKt;
import okio.BufferedSink;
import okio.FileMetadata;
import okio.FileSystem;
import okio.Okio;
import okio.Path;
import okio.Source;
import org.jose4j.jwe.KeyManagementAlgorithmIdentifiers;

/* compiled from: -FileSystem.kt */
@Metadata(d1 = {"\u00004\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\r\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u001aI\u0010\u0000\u001a\u00020\u0001*\b\u0012\u0004\u0012\u00020\u00030\u00022\u0006\u0010\u0004\u001a\u00020\u00052\f\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00030\u00072\u0006\u0010\b\u001a\u00020\u00032\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\nH\u0080@ø\u0001\u0000¢\u0006\u0002\u0010\f\u001a\u001c\u0010\r\u001a\u00020\u0001*\u00020\u00052\u0006\u0010\u000e\u001a\u00020\u00032\u0006\u0010\u000f\u001a\u00020\u0003H\u0000\u001a\u001c\u0010\u0010\u001a\u00020\u0001*\u00020\u00052\u0006\u0010\u0011\u001a\u00020\u00032\u0006\u0010\u0012\u001a\u00020\nH\u0000\u001a\u001c\u0010\u0013\u001a\u00020\u0001*\u00020\u00052\u0006\u0010\u0014\u001a\u00020\u00032\u0006\u0010\u0015\u001a\u00020\nH\u0000\u001a\u0014\u0010\u0016\u001a\u00020\n*\u00020\u00052\u0006\u0010\b\u001a\u00020\u0003H\u0000\u001a\"\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\u00030\u0018*\u00020\u00052\u0006\u0010\u0011\u001a\u00020\u00032\u0006\u0010\t\u001a\u00020\nH\u0000\u001a\u0014\u0010\u0019\u001a\u00020\u001a*\u00020\u00052\u0006\u0010\b\u001a\u00020\u0003H\u0000\u001a\u0016\u0010\u001b\u001a\u0004\u0018\u00010\u0003*\u00020\u00052\u0006\u0010\b\u001a\u00020\u0003H\u0000\u0082\u0002\u0004\n\u0002\b\u0019¨\u0006\u001c"}, d2 = {"collectRecursively", "", "Lkotlin/sequences/SequenceScope;", "Lokio/Path;", "fileSystem", "Lokio/FileSystem;", "stack", "Lkotlin/collections/ArrayDeque;", "path", "followSymlinks", "", "postorder", "(Lkotlin/sequences/SequenceScope;Lokio/FileSystem;Lkotlin/collections/ArrayDeque;Lokio/Path;ZZLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "commonCopy", "source", "target", "commonCreateDirectories", KeyManagementAlgorithmIdentifiers.DIRECT, "mustCreate", "commonDeleteRecursively", "fileOrDirectory", "mustExist", "commonExists", "commonListRecursively", "Lkotlin/sequences/Sequence;", "commonMetadata", "Lokio/FileMetadata;", "symlinkTarget", "okio"}, k = 2, mv = {1, 6, 0}, xi = 48)
/* loaded from: classes5.dex */
public final class _FileSystemKt {
    public static final FileMetadata commonMetadata(FileSystem $this$commonMetadata, Path path) throws IOException {
        Intrinsics.checkNotNullParameter($this$commonMetadata, "<this>");
        Intrinsics.checkNotNullParameter(path, "path");
        FileMetadata metadataOrNull = $this$commonMetadata.metadataOrNull(path);
        if (metadataOrNull != null) {
            return metadataOrNull;
        }
        throw new FileNotFoundException("no such file: " + path);
    }

    public static final boolean commonExists(FileSystem $this$commonExists, Path path) throws IOException {
        Intrinsics.checkNotNullParameter($this$commonExists, "<this>");
        Intrinsics.checkNotNullParameter(path, "path");
        return $this$commonExists.metadataOrNull(path) != null;
    }

    public static final void commonCreateDirectories(FileSystem $this$commonCreateDirectories, Path dir, boolean mustCreate) throws IOException {
        Intrinsics.checkNotNullParameter($this$commonCreateDirectories, "<this>");
        Intrinsics.checkNotNullParameter(dir, "dir");
        ArrayDeque directories = new ArrayDeque();
        for (Path path = dir; path != null && !$this$commonCreateDirectories.exists(path); path = path.parent()) {
            directories.addFirst(path);
        }
        if (mustCreate && directories.isEmpty()) {
            throw new IOException(dir + " already exist.");
        }
        Iterator it2 = directories.iterator();
        while (it2.hasNext()) {
            Path toCreate = (Path) it2.next();
            $this$commonCreateDirectories.createDirectory(toCreate);
        }
    }

    public static final void commonCopy(FileSystem $this$commonCopy, Path source, Path target) throws IOException {
        Object result$iv;
        Throwable thrown$iv;
        Intrinsics.checkNotNullParameter($this$commonCopy, "<this>");
        Intrinsics.checkNotNullParameter(source, "source");
        Intrinsics.checkNotNullParameter(target, "target");
        Closeable $this$use$iv = $this$commonCopy.source(source);
        Object result$iv2 = null;
        Throwable thrown$iv2 = null;
        try {
            Source bytesIn = (Source) $this$use$iv;
            Closeable $this$use$iv2 = Okio.buffer($this$commonCopy.sink(target));
            result$iv = null;
            thrown$iv = null;
            try {
                BufferedSink bytesOut = (BufferedSink) $this$use$iv2;
                result$iv = Long.valueOf(bytesOut.writeAll(bytesIn));
            } catch (Throwable t$iv) {
                thrown$iv = t$iv;
            }
            if ($this$use$iv2 != null) {
                try {
                    $this$use$iv2.close();
                } catch (Throwable t$iv2) {
                    if (thrown$iv == null) {
                        thrown$iv = t$iv2;
                    } else {
                        ExceptionsKt.addSuppressed(thrown$iv, t$iv2);
                    }
                }
            }
        } catch (Throwable t$iv3) {
            thrown$iv2 = t$iv3;
        }
        if (thrown$iv != null) {
            throw thrown$iv;
        }
        Intrinsics.checkNotNull(result$iv);
        result$iv2 = Long.valueOf(((Number) result$iv).longValue());
        if ($this$use$iv != null) {
            try {
                $this$use$iv.close();
            } catch (Throwable t$iv4) {
                if (thrown$iv2 == null) {
                    thrown$iv2 = t$iv4;
                } else {
                    ExceptionsKt.addSuppressed(thrown$iv2, t$iv4);
                }
            }
        }
        if (thrown$iv2 != null) {
            throw thrown$iv2;
        }
        Intrinsics.checkNotNull(result$iv2);
    }

    public static final void commonDeleteRecursively(FileSystem $this$commonDeleteRecursively, Path fileOrDirectory, boolean mustExist) throws IOException {
        Intrinsics.checkNotNullParameter($this$commonDeleteRecursively, "<this>");
        Intrinsics.checkNotNullParameter(fileOrDirectory, "fileOrDirectory");
        Sequence sequence = SequencesKt.sequence(new _FileSystemKt$commonDeleteRecursively$sequence$1($this$commonDeleteRecursively, fileOrDirectory, null));
        Iterator iterator = sequence.iterator();
        while (iterator.hasNext()) {
            Path toDelete = (Path) iterator.next();
            $this$commonDeleteRecursively.delete(toDelete, mustExist && !iterator.hasNext());
        }
    }

    public static final Sequence<Path> commonListRecursively(FileSystem $this$commonListRecursively, Path dir, boolean followSymlinks) throws IOException {
        Intrinsics.checkNotNullParameter($this$commonListRecursively, "<this>");
        Intrinsics.checkNotNullParameter(dir, "dir");
        return SequencesKt.sequence(new _FileSystemKt$commonListRecursively$1(dir, $this$commonListRecursively, followSymlinks, null));
    }

    /* JADX WARN: Failed to find 'out' block for switch in B:7:0x0025. Please report as an issue. */
    /* JADX WARN: Removed duplicated region for block: B:11:0x0030  */
    /* JADX WARN: Removed duplicated region for block: B:14:0x0035  */
    /* JADX WARN: Removed duplicated region for block: B:34:0x012f  */
    /* JADX WARN: Removed duplicated region for block: B:41:0x0055  */
    /* JADX WARN: Removed duplicated region for block: B:44:0x0098  */
    /* JADX WARN: Removed duplicated region for block: B:47:0x00a6  */
    /* JADX WARN: Removed duplicated region for block: B:67:0x006d  */
    /* JADX WARN: Removed duplicated region for block: B:8:0x0028  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static final java.lang.Object collectRecursively(kotlin.sequences.SequenceScope<? super okio.Path> r14, okio.FileSystem r15, kotlin.collections.ArrayDeque<okio.Path> r16, okio.Path r17, boolean r18, boolean r19, kotlin.coroutines.Continuation<? super kotlin.Unit> r20) {
        /*
            Method dump skipped, instructions count: 340
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: okio.internal._FileSystemKt.collectRecursively(kotlin.sequences.SequenceScope, okio.FileSystem, kotlin.collections.ArrayDeque, okio.Path, boolean, boolean, kotlin.coroutines.Continuation):java.lang.Object");
    }

    public static final Path symlinkTarget(FileSystem $this$symlinkTarget, Path path) throws IOException {
        Intrinsics.checkNotNullParameter($this$symlinkTarget, "<this>");
        Intrinsics.checkNotNullParameter(path, "path");
        Path target = $this$symlinkTarget.metadata(path).getSymlinkTarget();
        if (target == null) {
            return null;
        }
        Path parent = path.parent();
        Intrinsics.checkNotNull(parent);
        return parent.resolve(target);
    }
}
