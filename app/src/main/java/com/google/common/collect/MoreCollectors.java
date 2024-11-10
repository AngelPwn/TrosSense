package com.google.common.collect;

import com.google.common.base.Preconditions;
import com.google.common.collect.MoreCollectors;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import javax.annotation.Nullable;
import kotlin.text.Typography;

/* loaded from: classes.dex */
public final class MoreCollectors {
    private static final Collector<Object, ?, Optional<Object>> TO_OPTIONAL = Collector.of(new Supplier() { // from class: com.google.common.collect.MoreCollectors$$ExternalSyntheticLambda0
        @Override // java.util.function.Supplier
        public final Object get() {
            return new MoreCollectors.ToOptionalState();
        }
    }, new BiConsumer() { // from class: com.google.common.collect.MoreCollectors$$ExternalSyntheticLambda1
        @Override // java.util.function.BiConsumer
        public final void accept(Object obj, Object obj2) {
            ((MoreCollectors.ToOptionalState) obj).add(obj2);
        }
    }, new BinaryOperator() { // from class: com.google.common.collect.MoreCollectors$$ExternalSyntheticLambda2
        @Override // java.util.function.BiFunction
        public final Object apply(Object obj, Object obj2) {
            return ((MoreCollectors.ToOptionalState) obj).combine((MoreCollectors.ToOptionalState) obj2);
        }
    }, new Function() { // from class: com.google.common.collect.MoreCollectors$$ExternalSyntheticLambda3
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return ((MoreCollectors.ToOptionalState) obj).getOptional();
        }
    }, Collector.Characteristics.UNORDERED);
    private static final Object NULL_PLACEHOLDER = new Object();
    private static final Collector<Object, ?, Object> ONLY_ELEMENT = Collector.of(new Supplier() { // from class: com.google.common.collect.MoreCollectors$$ExternalSyntheticLambda0
        @Override // java.util.function.Supplier
        public final Object get() {
            return new MoreCollectors.ToOptionalState();
        }
    }, new BiConsumer() { // from class: com.google.common.collect.MoreCollectors$$ExternalSyntheticLambda4
        @Override // java.util.function.BiConsumer
        public final void accept(Object obj, Object obj2) {
            ((MoreCollectors.ToOptionalState) obj).add(o == null ? MoreCollectors.NULL_PLACEHOLDER : obj2);
        }
    }, new BinaryOperator() { // from class: com.google.common.collect.MoreCollectors$$ExternalSyntheticLambda2
        @Override // java.util.function.BiFunction
        public final Object apply(Object obj, Object obj2) {
            return ((MoreCollectors.ToOptionalState) obj).combine((MoreCollectors.ToOptionalState) obj2);
        }
    }, new Function() { // from class: com.google.common.collect.MoreCollectors$$ExternalSyntheticLambda5
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return MoreCollectors.lambda$static$1((MoreCollectors.ToOptionalState) obj);
        }
    }, Collector.Characteristics.UNORDERED);

    public static <T> Collector<T, ?, Optional<T>> toOptional() {
        return (Collector<T, ?, Optional<T>>) TO_OPTIONAL;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ Object lambda$static$1(ToOptionalState state) {
        Object result = state.getElement();
        if (result == NULL_PLACEHOLDER) {
            return null;
        }
        return result;
    }

    public static <T> Collector<T, ?, T> onlyElement() {
        return (Collector<T, ?, T>) ONLY_ELEMENT;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static final class ToOptionalState {
        static final int MAX_EXTRAS = 4;

        @Nullable
        Object element = null;

        @Nullable
        List<Object> extras = null;

        IllegalArgumentException multiples(boolean overflow) {
            StringBuilder sb = new StringBuilder().append("expected one element but was: <").append(this.element);
            for (Object o : this.extras) {
                sb.append(", ").append(o);
            }
            if (overflow) {
                sb.append(", ...");
            }
            sb.append(Typography.greater);
            throw new IllegalArgumentException(sb.toString());
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public void add(Object o) {
            Preconditions.checkNotNull(o);
            if (this.element == null) {
                this.element = o;
                return;
            }
            if (this.extras == null) {
                this.extras = new ArrayList(4);
                this.extras.add(o);
            } else {
                if (this.extras.size() < 4) {
                    this.extras.add(o);
                    return;
                }
                throw multiples(true);
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public ToOptionalState combine(ToOptionalState other) {
            if (this.element == null) {
                return other;
            }
            if (other.element == null) {
                return this;
            }
            if (this.extras == null) {
                this.extras = new ArrayList();
            }
            this.extras.add(other.element);
            if (other.extras != null) {
                this.extras.addAll(other.extras);
            }
            if (this.extras.size() > 4) {
                this.extras.subList(4, this.extras.size()).clear();
                throw multiples(true);
            }
            return this;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public Optional<Object> getOptional() {
            if (this.extras == null) {
                return Optional.ofNullable(this.element);
            }
            throw multiples(false);
        }

        Object getElement() {
            if (this.element == null) {
                throw new NoSuchElementException();
            }
            if (this.extras == null) {
                return this.element;
            }
            throw multiples(false);
        }
    }

    private MoreCollectors() {
    }
}
