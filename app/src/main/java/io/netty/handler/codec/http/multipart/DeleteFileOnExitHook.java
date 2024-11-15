package io.netty.handler.codec.http.multipart;

import java.io.File;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/* loaded from: classes4.dex */
final class DeleteFileOnExitHook {
    private static final Set<String> FILES = Collections.newSetFromMap(new ConcurrentHashMap());

    static {
        Runtime.getRuntime().addShutdownHook(new Thread() { // from class: io.netty.handler.codec.http.multipart.DeleteFileOnExitHook.1
            @Override // java.lang.Thread, java.lang.Runnable
            public void run() {
                DeleteFileOnExitHook.runHook();
            }
        });
    }

    private DeleteFileOnExitHook() {
    }

    public static void remove(String file) {
        FILES.remove(file);
    }

    public static void add(String file) {
        FILES.add(file);
    }

    public static boolean checkFileExist(String file) {
        return FILES.contains(file);
    }

    static void runHook() {
        for (String filename : FILES) {
            new File(filename).delete();
        }
    }
}
