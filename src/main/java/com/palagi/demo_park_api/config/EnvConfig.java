package com.palagi.demo_park_api.config;

import io.github.cdimascio.dotenv.Dotenv;

public class EnvConfig {

    private static final Dotenv dotenv = Dotenv.load();

    private EnvConfig() {
    }

    public static String getString(String key) {
        String value = dotenv.get(key);

        if (value == null || value.isBlank()) {
            throw new IllegalStateException("A variável de ambiente '" + key + "' não foi configurada corretamente no arquivo .env");
        }
        return value;
    }

    public static long getLong(String key) {
        String value = dotenv.get(key);
        if (value != null && !value.isBlank()) {
            try {
                return Long.parseLong(value);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("A variável '" + key + "' não pode ser convertida para long: " + value);
            }
        }
        throw new IllegalStateException("A variável '" + key + "' não foi encontrada ou está vazia.");
    }
}
