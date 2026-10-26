package com.school.violationsystem.util;

import java.io.Console;
import java.util.Arrays;

public class PasswordHashGenerator {

    private PasswordHashGenerator() {
    }

    public static void main(String[] args) {
        Console console = System.console();
        if (console == null) {
            throw new IllegalStateException("Run this class from a terminal to enter the password securely.");
        }

        char[] password = console.readPassword("Password to hash: ");
        try {
            if (password.length == 0) {
                System.err.println("Password must not be empty.");
                return;
            }
            System.out.println(PasswordUtil.hash(password));
        } finally {
            Arrays.fill(password, '\0');
        }
    }
}
