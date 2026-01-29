package com.fintech.wallet.application.port.in;

import com.fintech.wallet.domain.valueobject.AccountStatement;
import com.fintech.wallet.domain.valueobject.MonthlySummary;

import java.time.LocalDate;
import java.time.YearMonth;

/**
 * Input port for generating reports and analytics.
 */
public interface ReportingUseCase {

    /**
     * Generates an account statement for a wallet within a date range.
     *
     * @param walletId the wallet ID
     * @param startDate the start date (inclusive)
     * @param endDate the end date (inclusive)
     * @return the account statement
     */
    AccountStatement generateAccountStatement(String walletId, LocalDate startDate, LocalDate endDate);

    /**
     * Generates a monthly spending summary for a wallet.
     *
     * @param walletId the wallet ID
     * @param month the month to summarize
     * @return the monthly summary
     */
    MonthlySummary generateMonthlySummary(String walletId, YearMonth month);

    /**
     * Exports an account statement to PDF format.
     *
     * @param statement the account statement to export
     * @return PDF file as byte array
     */
    byte[] exportStatementToPdf(AccountStatement statement);

    /**
     * Exports an account statement to CSV format.
     *
     * @param statement the account statement to export
     * @return CSV file as byte array
     */
    byte[] exportStatementToCsv(AccountStatement statement);
}
