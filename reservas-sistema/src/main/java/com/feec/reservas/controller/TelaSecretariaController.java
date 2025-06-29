package com.feec.reservas.controller;

import com.feec.reservas.model.Reserva;
import com.feec.reservas.model.Secretaria;
import com.feec.reservas.service.GerenciadorDeReservas;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.sql.SQLException;
import java.time.format.DateTimeFormatter;

public class TelaSecretariaController {

    @FXML private Label labelBoasVindas;
    @FXML private TableView<Reserva> tabelaTodasReservas;
    @FXML private TableColumn<Reserva, String> colunaSolicitante;
    @FXML private TableColumn<Reserva, String> colunaItem;
    @FXML private TableColumn<Reserva, String> colunaData;
    @FXML private TableColumn<Reserva, String> colunaStatus;
    @FXML private Button btnAprovar;
    @FXML private Button btnNegar;

    private Secretaria secretariaLogada;
    private final GerenciadorDeReservas gerenciador = new GerenciadorDeReservas();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public void setSecretaria(Secretaria secretaria) {
        this.secretariaLogada = secretaria;
        labelBoasVindas.setText("Painel de Gerenciamento - Usuária: " + secretaria.getNome());
        carregarDados();
    }

    @FXML
    public void initialize() {
        // Configura como cada coluna irá obter os dados do objeto Reserva
        colunaSolicitante.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSolicitante().getNome()));
        colunaItem.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getItem().getNome()));
        colunaData.setCellValueFactory(cellData -> {
            String dataFormatada = cellData.getValue().getDataHoraInicio().format(formatter);
            return new SimpleStringProperty(dataFormatada);
        });
        colunaStatus.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus().toString()));
    }

    private void carregarDados() {
        try {
            tabelaTodasReservas.setItems(FXCollections.observableArrayList(gerenciador.listarTodasAsReservas()));
        } catch (SQLException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro de Banco de Dados", "Não foi possível carregar as reservas.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAprovarAction() {
        Reserva selecionada = tabelaTodasReservas.getSelectionModel().getSelectedItem();
        if (selecionada != null) {
            try {
                gerenciador.aprovarReserva(selecionada);
                mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Reserva aprovada com sucesso!");
                carregarDados(); // Recarrega a tabela para mostrar o novo status
            } catch (SQLException e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Não foi possível aprovar a reserva.");
                e.printStackTrace();
            }
        } else {
            mostrarAlerta(Alert.AlertType.WARNING, "Nenhuma seleção", "Por favor, selecione uma reserva para aprovar.");
        }
    }

    @FXML
    private void handleNegarAction() {
        Reserva selecionada = tabelaTodasReservas.getSelectionModel().getSelectedItem();
        if (selecionada != null) {
            try {
                gerenciador.negarReserva(selecionada);
                mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Reserva negada com sucesso.");
                carregarDados(); // Recarrega a tabela
            } catch (SQLException e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Não foi possível negar a reserva.");
                e.printStackTrace();
            }
        } else {
            mostrarAlerta(Alert.AlertType.WARNING, "Nenhuma seleção", "Por favor, selecione uma reserva para negar.");
        }
    }
    
    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensagem) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}