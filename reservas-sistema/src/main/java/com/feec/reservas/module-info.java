module com.feec.reservas {
    // Módulos que seu projeto precisa
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires javafx.base; // <-- ADICIONADO: Necessário para propriedades, coleções e bindings.

    // Pacotes que precisam ser "abertos" para o JavaFX FXML
    opens com.feec.reservas.controller to javafx.fxml;
    opens com.feec.reservas.model to javafx.fxml; // <-- ADICIONADO: Boa prática para permitir que o FXML acesse o modelo.

    // Pacotes que você quer exportar (tornar públicos para outros módulos)
    exports com.feec.reservas; // Mantenha se a classe MainApplication estiver aqui
    exports com.feec.reservas.controller;
    exports com.feec.reservas.model;
    exports com.feec.reservas.service;
    exports com.feec.reservas.exception;
}