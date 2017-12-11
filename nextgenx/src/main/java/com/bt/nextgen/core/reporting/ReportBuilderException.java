package com.bt.nextgen.core.reporting;

public class ReportBuilderException extends RuntimeException {
    public ReportBuilderException() {
        super();
    }

    public ReportBuilderException(String error) {
        super(error);
    }

    public ReportBuilderException(String error, Throwable throwable) {
        super(error, throwable);
    }

    public ReportBuilderException(Throwable throwable) {
        super(throwable);
    }
}
