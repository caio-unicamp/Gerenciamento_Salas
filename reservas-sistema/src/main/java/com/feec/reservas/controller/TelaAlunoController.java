package com.feec.reservas.controller;

import com.feec.reservas.dao.ItemReservavelDAO;
import com.feec.reservas.exception.ReservaConflitanteException;
import com.feec.reservas.model.Aluno;
import com.feec.reservas.model.ItemReservavel;
import com.feec.reservas.model.Reserva;
import com.feec.reservas.service.GerenciadorDeReservas;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class TelaAlunoController {

    @FXML private Label labelBoasVindas;
    @FXML private ComboBox<ItemReservavel> comboItens;
    @FXML private DatePicker datePicker;
    @FXML private Button btnReservar;
    @FXML private TableView<Reserva> tabelaReservas;
    @FXML private TableColumn<Reserva, String> colunaItem;
    @FXML private TableColumn<Reserva, String> colunaData;
    @FXML private TableColumn<Reserva, String> colunaStatus;

    private Aluno alunoLogado;
    private final ItemReservavelDAO itemDAO = new ItemReservavelDAO();
    private final GerenciadorDeReservas gerenciador = new GerenciadorDeReservas();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public void setAluno(Aluno aluno) {
        this.alunoLogado = aluno;
        labelBoasVindas.setText("Bem-vindo, " + aluno.getNome() + "!");
        carregarDados();
    }

    @FXML
    public void initialize() {
        // Configura a exibição dos itens no ComboBox
        configurarComboBox();

        // Configura as colunas da tabela
        colunaItem.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getItem().getDescricaoDetalhada()));
        colunaData.setCellValueFactory(cellData -> {
            String dataFormatada = cellData.getValue().getDataHoraInicio().format(formatter);
            return new SimpleStringProperty(dataFormatada);
        });
        colunaStatus.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus().toString()));
        
        datePicker.setValue(LocalDate.now());
    }

    private void carregarDados() {
        try {
            // Carrega os itens disponíveis no ComboBox
            comboItens.setItems(FXCollections.observableArrayList(itemDAO.listarTodos()));
            // Carrega as reservas do aluno na tabela
            tabelaReservas.setItems(FXCollections.observableArrayList(gerenciador.listarReservasPorAluno(alunoLogado.getId())));
        } catch (SQLException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro de Banco de Dados", "Não foi possível carregar os dados iniciais.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleReservarAction() {
        ItemReservavel itemSelecionado = comboItens.getValue();
        LocalDate dataSelecionada = datePicker.getValue();

        if (itemSelecionado == null || dataSelecionada == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Dados incompletos", "Por favor, selecione um item e uma data para a reserva.");
            return;
        }

        // Para simplificar, vamos assumir que a reserva é sempre às 14:00.
        // Em um projeto real, você adicionaria campos para o horário.
        LocalDateTime dataHoraInicio = LocalDateTime.of(dataSelecionada, LocalTime.of(14, 0));

        try {
            gerenciador.solicitarReserva(alunoLogado, itemSelecionado, dataHoraInicio);
            mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Sua solicitação de reserva foi enviada e está pendente de aprovação.");
            carregarDados(); // Atualiza a tabela com a nova reserva
        } catch (ReservaConflitanteException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Conflito de Reserva", e.getMessage());
        } catch (SQLException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro de Banco de Dados", "Ocorreu um erro ao processar sua solicitação.");
            e.printStackTrace();
        }
    }

    private void configurarComboBox() {
        // Define como o texto do item será exibido dentro do ComboBox
        comboItens.setConverter(new StringConverter<>() {
            @Override
            public String toString(ItemReservavel item) {
                return item == null ? null : item.getDescricaoDetalhada();
            }

            @Override
            public ItemReservavel fromString(String string) {
                return null; // Não necessário para este caso
            }
        });
    }
    
    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensagem) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}