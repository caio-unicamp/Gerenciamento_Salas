package com.feec.reservas.controller;

import com.feec.reservas.dao.ItemReservavelDAO;
import com.feec.reservas.dao.ReservaDAO;
import com.feec.reservas.dao.UsuarioDAO;
import com.feec.reservas.model.Usuario;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.sql.SQLException;

public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField senhaField;
    @FXML private Button loginButton;

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private MainController mainController; // Referência ao controller principal

    // Método para que o MainController possa se "apresentar"
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @FXML
    public void initialize() {
        try {
            new UsuarioDAO().criarDadosIniciaisSeNecessario();
            new ItemReservavelDAO().criarDadosIniciaisSeNecessario();
            new ReservaDAO().criarDadosIniciaisSeNecessario();
        } catch (SQLException e) {
            System.err.println("Erro ao popular banco de dados: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLoginButtonAction() {
        String email = emailField.getText();
        String senha = senhaField.getText();

        if (email.isEmpty() || senha.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Campos Vazios", "Por favor, preencha o email e a senha.");
            return;
        }

        try {
            Usuario usuario = usuarioDAO.autenticar(email, senha);

            if (usuario != null) {
                // Em vez de abrir uma nova janela, pede ao MainController para trocar a tela
                if (mainController != null) {
                    mainController.carregarTelaUsuario(usuario);
                }
            } else {
                showAlert(Alert.AlertType.ERROR, "Falha no Login", "Email ou senha incorretos.");
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erro de Banco de Dados", "Ocorreu um erro ao acessar o banco de dados.");
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}