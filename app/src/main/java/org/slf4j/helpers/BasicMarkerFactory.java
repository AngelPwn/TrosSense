package org.slf4j.helpers;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.slf4j.IMarkerFactory;
import org.slf4j.Marker;

/* loaded from: classes5.dex */
public class BasicMarkerFactory implements IMarkerFactory {
    private final ConcurrentMap<String, Marker> markerMap = new ConcurrentHashMap();

    @Override // org.slf4j.IMarkerFactory
    public Marker getMarker(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Marker name cannot be null");
        }
        Marker marker = this.markerMap.get(name);
        if (marker == null) {
            Marker marker2 = new BasicMarker(name);
            Marker oldMarker = this.markerMap.putIfAbsent(name, marker2);
            return oldMarker != null ? oldMarker : marker2;
        }
        return marker;
    }

    @Override // org.slf4j.IMarkerFactory
    public boolean exists(String name) {
        if (name == null) {
            return false;
        }
        return this.markerMap.containsKey(name);
    }

    @Override // org.slf4j.IMarkerFactory
    public boolean detachMarker(String name) {
        return (name == null || this.markerMap.remove(name) == null) ? false : true;
    }

    @Override // org.slf4j.IMarkerFactory
    public Marker getDetachedMarker(String name) {
        return new BasicMarker(name);
    }
}
