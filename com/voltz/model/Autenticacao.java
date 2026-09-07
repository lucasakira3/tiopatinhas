package com.voltz.model;

public class Autenticacao {

    public boolean autenticar(Usuario usuario, String email, String senha) {
        if (usuario == null) {
            System.out.println("Usuário inválido");
            return false;
        }

        boolean autenticado = usuario.login(email, senha);

        if (autenticado) {
            System.out.println("Login realizado com sucesso!");
        } else {
            System.out.println("Email ou senha incorretos.");
        }

        return autenticado;
    }
}