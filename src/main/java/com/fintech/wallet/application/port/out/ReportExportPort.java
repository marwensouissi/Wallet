package com.fintech.wallet.application.port.out;

import com.fintech.wallet.domain.valueobject.AccountStatement;

/**
 * Output port for exporting reports to various formats.
 */
public interface ReportExportPort {

    /**
     * Exports an account statement to PDF format.
     *
     * @param statement the account statement
     * @return PDF as byte array
     */
    byte[] exportToPdf(AccountStatement statement);

    /**
     * Exports an account statement to CSV format.
     *
     * @param statement the account statement
     * @return CSV as byte array
     */
    byte[] exportToCsv(AccountStatement statement);
}
