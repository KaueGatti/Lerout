package my.company.projetorotisseriejavafx.Controller;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.Pane;
import my.company.projetorotisseriejavafx.DAO.MarmitaVendidaDAO;
import my.company.projetorotisseriejavafx.DAO.PedidoDAO;
import my.company.projetorotisseriejavafx.Objects.MarmitasVendidas;
import my.company.projetorotisseriejavafx.Objects.Relatorio;
import my.company.projetorotisseriejavafx.Util.DatabaseExceptionHandler;

import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class RelatorioController implements Initializable {

    @FXML
    private DatePicker DPData;

    @FXML
    private Label LFaturamento;

    @FXML
    private Label LPorcentoCartao;

    @FXML
    private Label LPorcentoDinheiro;

    @FXML
    private Label LPorcentoPB;

    @FXML
    private Label LPorcentoPE;

    @FXML
    private Label LPorcentoPix;

    @FXML
    private Label LPorcentoTP;

    @FXML
    private Label LTotalCartao;

    @FXML
    private Label LTotalDinheiro;

    @FXML
    private Label LTotalPB;

    @FXML
    private Label LTotalPE;

    @FXML
    private Label LTotalPagamentos;

    @FXML
    private Label LTotalPedidos;

    @FXML
    private Label LTotalPedidosRG;

    @FXML
    private Label LTotalPix;

    @FXML
    private Label LValorCartao;

    @FXML
    private Label LValorDinheiro;

    @FXML
    private Label LValorPB;

    @FXML
    private Label LValorPE;

    @FXML
    private Label LValorPix;

    @FXML
    private Label LValorTP;

    @FXML
    private Label LValorTotalPagamentos;

    @FXML
    private Pane PPagamentos;

    @FXML
    private Pane PMarmitasVendidas;

    @FXML
    private Pane PPedidos;

    @FXML
    private Pane PResumoGeral;

    @FXML
    private Pane panePrincipal;

    @FXML
    private TableView<MarmitasVendidas> TMarmitas;

    @FXML
    private TableColumn<MarmitasVendidas, String> colNomeMV;

    @FXML
    private TableColumn<MarmitasVendidas, Integer> colQuantidadeMV;

    @FXML
    private TableColumn<MarmitasVendidas, Double> colSubtotalMV;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        DPData.setValue(LocalDate.now());
        loadRelatorio();
        initDPData();
        initTableMV();
    }

    private void initDPData() {
        DPData.setEditable(false);

        DPData.valueProperty().addListener((observable, oldValue, newValue) -> {
            loadRelatorio();
            loadTableMV();
        });
    }

    private void loadRelatorio() {
        Relatorio relatorio = null;
        try {
            relatorio = new Relatorio(PedidoDAO.listarPorData(DPData.getValue()));
        } catch (SQLException e) {
            DatabaseExceptionHandler.handleException(e, "Erro ao ler relatorio");
            return;
        }

        LTotalPedidosRG.setText(String.valueOf(relatorio.getTotalPedidos()));
        LFaturamento.setText(String.format("R$ %.2f", relatorio.getValorFaturamento()));

        LTotalPE.setText(String.valueOf(relatorio.getTotalEntrega()));
        LValorPE.setText(String.format("R$ %.2f", relatorio.getValorTotalEntrega()));
        LPorcentoPE.setText(relatorio.getPorcentoEntrega() + "%");

        LTotalPB.setText(String.valueOf(relatorio.getTotalBalcao()));
        LValorPB.setText(String.format("R$ %.2f", relatorio.getValorTotalBalcao()));
        LPorcentoPB.setText(relatorio.getPorcentoBalcao() + "%");

        LTotalPedidos.setText(String.valueOf(relatorio.getTotalPedidos()));
        LValorTP.setText(String.format("R$ %.2f", relatorio.getValorTotalPedidos()));

        LTotalDinheiro.setText(String.valueOf(relatorio.getTotalDinheiro()));
        LValorDinheiro.setText(String.format("R$ %.2f", relatorio.getValorTotalDinheiro()));
        LPorcentoDinheiro.setText(relatorio.getPorcentoDinheiro() + "%");

        LTotalCartao.setText(String.valueOf(relatorio.getTotalCartao()));
        LValorCartao.setText(String.format("R$ %.2f", relatorio.getValorTotalCartao()));
        LPorcentoCartao.setText(relatorio.getPorcentoCartao() + "%");

        LTotalPix.setText(String.valueOf(relatorio.getTotalPix()));
        LValorPix.setText(String.format("R$ %.2f", relatorio.getValorTotalPix()));
        LPorcentoPix.setText(relatorio.getPorcentoPix() + "%");

        LTotalPagamentos.setText(String.valueOf(relatorio.getTotalPagamentos()));
        LValorTotalPagamentos.setText(String.format("R$ %.2f", relatorio.getValorTotalPagamentos()));
    }

    private void initTableMV() {
        colNomeMV.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().nome()));

        colQuantidadeMV.setCellValueFactory(data ->
                new SimpleObjectProperty<>(data.getValue().quantidade()));

        colSubtotalMV.setCellValueFactory(data ->
                new SimpleObjectProperty<>(data.getValue().subtotal()));

        try {
            List<MarmitasVendidas> list = MarmitaVendidaDAO.listarMarmitasVendidas(DPData.getValue());
            ObservableList<MarmitasVendidas> observableList = FXCollections.observableList(list);
            TMarmitas.setItems(observableList);
        } catch (SQLException e) {
            DatabaseExceptionHandler.handleException(e, "Erro ao gerar relatorio");
        }
    }

    private void loadTableMV() {
        try {
            List<MarmitasVendidas> list = MarmitaVendidaDAO.listarMarmitasVendidas(DPData.getValue());
            ObservableList<MarmitasVendidas> observableList = FXCollections.observableList(list);
            TMarmitas.setItems(observableList);
        } catch (SQLException e) {
            DatabaseExceptionHandler.handleException(e, "Erro ao gerar relatorio");
        }
    }


}
