package org.slf4j.helpers;

/* loaded from: classes5.dex */
public class NOPLogger extends MarkerIgnoringBase {
    public static final NOPLogger NOP_LOGGER = new NOPLogger();
    private static final long serialVersionUID = -517220405410904473L;

    protected NOPLogger() {
    }

    @Override // org.slf4j.helpers.MarkerIgnoringBase, org.slf4j.helpers.NamedLoggerBase, org.slf4j.Logger
    public String getName() {
        return "NOP";
    }

    @Override // org.slf4j.Logger
    public final boolean isTraceEnabled() {
        return false;
    }

    @Override // org.slf4j.Logger
    public final void trace(String msg) {
    }

    @Override // org.slf4j.Logger
    public final void trace(String format, Object arg) {
    }

    @Override // org.slf4j.Logger
    public final void trace(String format, Object arg1, Object arg2) {
    }

    @Override // org.slf4j.Logger
    public final void trace(String format, Object... argArray) {
    }

    @Override // org.slf4j.Logger
    public final void trace(String msg, Throwable t) {
    }

    @Override // org.slf4j.Logger
    public final boolean isDebugEnabled() {
        return false;
    }

    @Override // org.slf4j.Logger
    public final void debug(String msg) {
    }

    @Override // org.slf4j.Logger
    public final void debug(String format, Object arg) {
    }

    @Override // org.slf4j.Logger
    public final void debug(String format, Object arg1, Object arg2) {
    }

    @Override // org.slf4j.Logger
    public final void debug(String format, Object... argArray) {
    }

    @Override // org.slf4j.Logger
    public final void debug(String msg, Throwable t) {
    }

    @Override // org.slf4j.Logger
    public final boolean isInfoEnabled() {
        return false;
    }

    @Override // org.slf4j.Logger
    public final void info(String msg) {
    }

    @Override // org.slf4j.Logger
    public final void info(String format, Object arg1) {
    }

    @Override // org.slf4j.Logger
    public final void info(String format, Object arg1, Object arg2) {
    }

    @Override // org.slf4j.Logger
    public final void info(String format, Object... argArray) {
    }

    @Override // org.slf4j.Logger
    public final void info(String msg, Throwable t) {
    }

    @Override // org.slf4j.Logger
    public final boolean isWarnEnabled() {
        return false;
    }

    @Override // org.slf4j.Logger
    public final void warn(String msg) {
    }

    @Override // org.slf4j.Logger
    public final void warn(String format, Object arg1) {
    }

    @Override // org.slf4j.Logger
    public final void warn(String format, Object arg1, Object arg2) {
    }

    @Override // org.slf4j.Logger
    public final void warn(String format, Object... argArray) {
    }

    @Override // org.slf4j.Logger
    public final void warn(String msg, Throwable t) {
    }

    @Override // org.slf4j.Logger
    public final boolean isErrorEnabled() {
        return false;
    }

    @Override // org.slf4j.Logger
    public final void error(String msg) {
    }

    @Override // org.slf4j.Logger
    public final void error(String format, Object arg1) {
    }

    @Override // org.slf4j.Logger
    public final void error(String format, Object arg1, Object arg2) {
    }

    @Override // org.slf4j.Logger
    public final void error(String format, Object... argArray) {
    }

    @Override // org.slf4j.Logger
    public final void error(String msg, Throwable t) {
    }
}
