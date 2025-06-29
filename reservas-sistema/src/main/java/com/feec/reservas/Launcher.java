package com.feec.reservas;

/**
 * Classe "lançadora" para contornar o problema de empacotamento modular do JavaFX.
 * Esta classe NÃO estende Application e seu único propósito é chamar o main()
 * da classe principal real da aplicação.
 */
public class Launcher {
    public static void main(String[] args) {
        MainApplication.main(args);
    }
}