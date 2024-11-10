package kotlin.io;

import java.io.Closeable;
import kotlin.Metadata;
import kotlin.internal.PlatformImplementationsKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.InlineMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jose4j.jwk.JsonWebKey;

/* compiled from: Closeable.kt */
@Metadata(d1 = {"\u0000\u001c\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0003\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\u001a\u0018\u0010\u0000\u001a\u00020\u0001*\u0004\u0018\u00010\u00022\b\u0010\u0003\u001a\u0004\u0018\u00010\u0004H\u0001\u001aH\u0010\u0005\u001a\u0002H\u0006\"\n\b\u0000\u0010\u0007*\u0004\u0018\u00010\u0002\"\u0004\b\u0001\u0010\u0006*\u0002H\u00072\u0012\u0010\b\u001a\u000e\u0012\u0004\u0012\u0002H\u0007\u0012\u0004\u0012\u0002H\u00060\tH\u0087\bø\u0001\u0000\u0082\u0002\n\n\b\b\u0001\u0012\u0002\u0010\u0001 \u0001¢\u0006\u0002\u0010\n\u0082\u0002\u0007\n\u0005\b\u009920\u0001¨\u0006\u000b"}, d2 = {"closeFinally", "", "Ljava/io/Closeable;", "cause", "", JsonWebKey.USE_PARAMETER, "R", "T", "block", "Lkotlin/Function1;", "(Ljava/io/Closeable;Lkotlin/jvm/functions/Function1;)Ljava/lang/Object;", "kotlin-stdlib"}, k = 2, mv = {1, 8, 0}, xi = 48)
/* loaded from: classes.dex */
public final class CloseableKt {
    private static final <T extends Closeable, R> R use(T t, Function1<? super T, ? extends R> block) {
        Intrinsics.checkNotNullParameter(block, "block");
        try {
            R invoke = block.invoke(t);
            InlineMarker.finallyStart(1);
            if (PlatformImplementationsKt.apiVersionIsAtLeast(1, 1, 0)) {
                closeFinally(t, null);
            } else if (t != null) {
                t.close();
            }
            InlineMarker.finallyEnd(1);
            return invoke;
        } catch (Throwable e) {
            try {
                throw e;
            } catch (Throwable e2) {
                InlineMarker.finallyStart(1);
                if (PlatformImplementationsKt.apiVersionIsAtLeast(1, 1, 0)) {
                    closeFinally(t, e);
                } else if (t != null) {
                    try {
                        t.close();
                    } catch (Throwable th) {
                    }
                }
                InlineMarker.finallyEnd(1);
                throw e2;
            }
        }
    }

    public static final void closeFinally(Closeable $this$closeFinally, Throwable cause) {
        if ($this$closeFinally != null) {
            if (cause != null) {
                try {
                    $this$closeFinally.close();
                    return;
                } catch (Throwable closeException) {
                    kotlin.ExceptionsKt.addSuppressed(cause, closeException);
                    return;
                }
            }
            $this$closeFinally.close();
        }
    }
}
