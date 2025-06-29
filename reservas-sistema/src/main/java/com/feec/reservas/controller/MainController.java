package com.feec.reservas.controller;

import com.feec.reservas.model.Usuario;
import com.feec.reservas.model.Aluno;
import com.feec.reservas.model.Secretaria;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

/**
 * Controller principal que gerencia a janela principal e a troca de telas.
 */
public class MainController {

    @FXML
    private BorderPane mainPane;

    @FXML
    private MenuItem menuLogout;

    @FXML
    public void initialize() {
        // Carrega a tela de login como a primeira tela a ser exibida.
        carregarTelaLogin();
    }

    /**
     * Carrega a tela de login no centro da janela principal.
     */
    public void carregarTelaLogin() {
        menuLogout.setVisible(false); // Esconde o botão de logout
        carregarTela("/com/feec/reservas/fxml/login-view.fxml", null);
    }

    /**
     * Carrega a tela apropriada para o usuário que fez login.
     * @param usuario O usuário que foi autenticado.
     */
    public void carregarTelaUsuario(Usuario usuario) {
        menuLogout.setVisible(true); // Mostra o botão de logout

        if (usuario.getTipoUsuario().equalsIgnoreCase("ALUNO")) {
            carregarTela("/com/feec/reservas/fxml/tela-aluno-view.fxml", usuario);
        } else if (usuario.getTipoUsuario().equalsIgnoreCase("SECRETARIA")) {
            carregarTela("/com/feec/reservas/fxml/tela-secretaria-view.fxml", usuario);
        }
    }

    /**
     * Método genérico para carregar um arquivo FXML no centro do BorderPane.
     * Também passa o controle principal e o usuário logado para os sub-controllers.
     * @param fxmlPath O caminho para o arquivo .fxml.
     * @param usuario O usuário a ser passado para o controller da tela carregada (pode ser null).
     */
    private void carregarTela(String fxmlPath, Usuario usuario) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();

            // Passa o controle deste MainController e do usuário para a tela carregada
            Object controller = loader.getController();
            if (controller instanceof LoginController) {
                ((LoginController) controller).setMainController(this);
            } else if (controller instanceof TelaAlunoController && usuario != null) {
                ((TelaAlunoController) controller).setAluno((Aluno) usuario);
            } else if (controller instanceof TelaSecretariaController && usuario != null) {
                ((TelaSecretariaController) controller).setSecretaria((Secretaria) usuario);
            }

            mainPane.setCenter(view);

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Erro Crítico", "Não foi possível carregar a tela: " + fxmlPath);
        }
    }

    @FXML
    private void handleLogout() {
        carregarTelaLogin();
    }
    
    @FXML
    private void handleSair() {
        Platform.exit();
    }

    @FXML
    private void handleSobre() {
        mostrarAlerta("Sobre", "Sistema de Reserva de Salas e Equipamentos v1.0");
    }

    private void mostrarAlerta(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}