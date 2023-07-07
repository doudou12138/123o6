package org.fffd.l23o6.util.strategy.payment;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PaymentLogger {
    public static final PaymentLogger INSTANCE= new PaymentLogger();
    private static final String LOG_FILE_PATH = "payment.log";

    public void logPayment( double amount) {
        String logMessage = getLogMessage(amount);
        writeLogToFile(logMessage);
    }

    public void logPaymentFailure(double amount, String errorMessage) {
        String logMessage = getLogMessage(amount)+" error "+errorMessage;
        writeLogToFile(logMessage);
    }

    private String getLogMessage( double amount) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String timestamp = now.format(formatter);
        return timestamp + " - Payment logged - "+ " Amount: " + amount + " RMB";
    }


    private void writeLogToFile(String logMessage) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_FILE_PATH, true))) {
            writer.write(logMessage);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Failed to write payment log to file: " + e.getMessage());
        }
    }

}
