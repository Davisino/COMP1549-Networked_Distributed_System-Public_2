package org.chatapp.utils;

import java.io.InputStream;
import java.util.Scanner;

public class RealScannerWrapper implements ScannerWrapper {
    private Scanner scanner;

    public RealScannerWrapper(InputStream in) {
        this.scanner = new Scanner(in);
    }


    public boolean hasNextLine() {
        return scanner.hasNextLine();
    }

    public String nextLine() {
        return scanner.nextLine();
    }

    // Add other necessary methods...
}
