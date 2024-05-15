package com.pfe.personnel.config;

public class AuthenticationResponse {
    private String token;
    private String name;
    private String role;

    // Constructor, getters, and setters

    public static AuthenticationResponseBuilder builder() {
        return new AuthenticationResponseBuilder();
    }

    public static class AuthenticationResponseBuilder {
        private String token;
        private String nom;
        private String role;

        public AuthenticationResponseBuilder token(String token) {
            this.token = token;
            return this;
        }

        public AuthenticationResponseBuilder name(String name) {
            this.nom= name;
            return this;
        }

        public AuthenticationResponseBuilder role(String role) {
            this.role = role;
            return this;
        }

        public AuthenticationResponse build() {
            AuthenticationResponse response = new AuthenticationResponse();
            response.setToken(token);
            response.setNom(nom);
            response.setRole(role);
            return response;
        }
    }

    private void setRole(String role) {
    }

    private void setNom(String nom) {
    }

    private void setToken(String token) {
    }
}

