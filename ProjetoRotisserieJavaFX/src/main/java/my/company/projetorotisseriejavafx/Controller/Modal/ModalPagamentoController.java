package my.company.projetorotisseriejavafx.Controller.Modal;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;

import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import my.company.projetorotisseriejavafx.Objects.Pagamento;
import my.company.projetorotisseriejavafx.Util.CssHelper;
import my.company.projetorotisseriejavafx.Util.CurrencyFieldUtil;
import my.company.projetorotisseriejavafx.Util.IconHelper;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

public class ModalPagamentoController {

    private double valorPedido;

    private LocalDate vencimento = null;

    private boolean print = false;

    private boolean finalizado = false;

    private ObservableList<Pagamento> pagamentos = FXCollections.observableArrayList();

    @FXML
    private AnchorPane root;
    @FXML
    private ComboBox<String> CBPagamento;
    @FXML
    private DatePicker DPVencimento;
    @FXML
    private Label LInfo;
    @FXML
    private TextField TFValorPago;
    @FXML
    private Button btnFinalizar;
    @FXML
    private Button btnAddPagamento;
    @FXML
    private Label LValorAPagar;
    @FXML
    private Label LValorPago;
    @FXML
    private TableColumn<Pagamento, Void> colDel;
    @FXML
    private TableColumn<Pagamento, String> colFormaDePagamento;
    @FXML
    private TableColumn<Pagamento, Double> colValor;
    @FXML
    private TableView<Pagamento> pagamentoTable;

    public void initialize(Double valorPedido) {
        this.valorPedido = valorPedido;
        initCampos();
        loadCBPagamento();
        initLValorAPagar();
        initPagamentoTable();
        initPagamentoObservableList();
    }

    @FXML
    void finalizarEImprimir(ActionEvent event) {
        print = true;
        finalizar(event);
    }

    @FXML
    void finalizar(ActionEvent event) {

        if (!validaValorPago()) return;

        double valorPago = pagamentos.stream().mapToDouble(Pagamento::getValor).sum();

        if (CBPagamento.getValue().equals("Pagar depois")) {
            if (!validaVencimento()) return;

            vencimento = DPVencimento.getValue();
        }

        finalizado = true;

        if (!CBPagamento.getValue().equals("Pagar depois") && !CBPagamento.getValue().equals("A definir")) {
            if (valorPago > valorPedido) {
                double valorTroco = valorPago - valorPedido;
                abrirModalTroco(valorTroco);
            }
        }

        fecharModal();
    }

    @FXML
    void adicionarPagamento(ActionEvent event) {

        if (!validaTFValorPago()) return;

        double valorPago = pagamentos.stream().mapToDouble(Pagamento::getValor).sum();
        double valor = CurrencyFieldUtil.getValue(TFValorPago);

        if (!CBPagamento.getValue().equals("Dinheiro")) {
            if ((valorPago + valor) > valorPedido) {
                String valorAPagar = String.format("R$ %.2f", valorPedido - valorPago);
                LInfo.setText("Valor inválido! Restam apenas " + valorAPagar);
                return;
            }
        }

        Pagamento pagamento = new Pagamento();

        pagamento.setTipoPagamento(CBPagamento.getValue());
        pagamento.setValor(CurrencyFieldUtil.getValue(TFValorPago));

        pagamentos.add(pagamento);

        TFValorPago.setText("");
    }

    private void fecharModal() {
        Stage modal = (Stage) root.getScene().getWindow();
        modal.close();
    }

    public String getPagamento() {

        if (CBPagamento.getValue().equals("Pagar depois")) {
            return "Pagar depois";
        }

        if (CBPagamento.getValue().equals("A definir")) {
            return "A definir";
        }

        List<Pagamento> semRepetidos = pagamentos.stream()
                .filter(distinctByKey(Pagamento::getTipoPagamento))
                .toList();

        StringBuilder pagamento = new StringBuilder();
        for (Pagamento p : semRepetidos) {
            pagamento.append(p.getTipoPagamento());
            if (pagamentos.indexOf(p) < semRepetidos.size() - 1) {
                pagamento.append(", ");
            }
        }

        return pagamento.toString();
    }

    public List<Pagamento> getPagamentos() {
        return pagamentos;
    }

    public LocalDate getVencimento() {
        return vencimento;
    }

    public boolean getPrint() {
        return print;
    }

    public boolean getFinalizado() {
        return finalizado;
    }

    public void loadCBPagamento() {
        CBPagamento.getItems().clear();
        CBPagamento.getItems().setAll("Dinheiro", "Cartão", "Pix", "Pagar depois", "A definir");
        CBPagamento.getSelectionModel().selectFirst();

        CBPagamento.setOnAction(event -> {

            if (CBPagamento.getValue().equals("Pagar depois")) {
                btnAddPagamento.setDisable(true);
                pagamentoTable.setDisable(true);
                TFValorPago.setDisable(true);
                TFValorPago.setText("");
                LValorPago.setText("R$ 0,00");
                LInfo.setText("");
                DPVencimento.setDisable(false);
                return;
            }

            if (CBPagamento.getValue().equals("A definir")) {
                btnAddPagamento.setDisable(true);
                pagamentoTable.setDisable(true);
                TFValorPago.setDisable(true);
                TFValorPago.setText("");
                LValorPago.setText("R$ 0,00");
                LInfo.setText("");
                DPVencimento.setDisable(true);
                return;
            }

            String valorPago = String.format("R$ %.2f", pagamentos.stream().mapToDouble(Pagamento::getValor).sum());
            LValorPago.setText(valorPago);
            btnAddPagamento.setDisable(false);
            pagamentoTable.setDisable(false);
            TFValorPago.setText("");
            LInfo.setText("");
            TFValorPago.setDisable(false);
            DPVencimento.setDisable(true);
        });
    }

    public void initCampos() {
        CurrencyFieldUtil.configureField(TFValorPago, false, false, false);

        TFValorPago.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                validaValorPago();
            }
        });
        DPVencimento.setEditable(false);
    }

    public boolean validaValorPago() {
        double valorPago = pagamentos.stream().mapToDouble(Pagamento::getValor).sum();

        if (!CBPagamento.getValue().equals("Pagar depois") && !CBPagamento.getValue().equals("A definir")) {
            if (valorPago < valorPedido) {
                String valorRestante = String.format("R$ %.2f", valorPedido - valorPago);
                LInfo.setText("Valor insuficiente! restam " + valorRestante);
                return false;
            }
        }

        LInfo.setText("");
        return true;
    }

    private boolean validaTFValorPago() {
        if (TFValorPago.getText().trim().isEmpty()) {
            LInfo.setText("Valor pago não pode ser vázio");
            return false;
        }

        double valorPago = CurrencyFieldUtil.getValue(TFValorPago);

        if (valorPago <= 0) {
            LInfo.setText("O valor deve ser maior que 0,00");
            return false;
        }

        LInfo.setText("");
        return true;
    }

    public boolean validaVencimento() {
        if (DPVencimento.getValue() == null) {
            LInfo.setText("Vencimento não pode ser vázio");
            return false;
        }

        if (DPVencimento.getValue().isBefore(LocalDate.now())) {
            LInfo.setText("A data de vencimento não pode ser anterior a data atual");
            return false;
        }

        return true;
    }

    public void abrirModalTroco(double valorTroco) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Modal/modalTroco.fxml"));

            Parent root = loader.load();

            Stage modal = new Stage();
            Scene scene = new Scene(root);

            CssHelper.loadCss(scene);

            modal.setScene(scene);

            ModalTrocoController controller = loader.getController();

            controller.initialize(valorTroco);

            modal.setResizable(false);
            modal.initModality(Modality.APPLICATION_MODAL);
            modal.initStyle(StageStyle.UTILITY);
            modal.show();

        } catch (IOException e) {
            System.out.println("Erro ao abrir modal troco" + e);
            e.printStackTrace();
        }
    }

    private void initPagamentoTable() {
        colFormaDePagamento.setCellValueFactory(new PropertyValueFactory<>("tipoPagamento"));
        colValor.setCellValueFactory(new PropertyValueFactory<>("formattedValor"));
        colDel.setCellFactory(param -> new TableCell<>() {
            private final Button btnDel = new Button("");

            {
                btnDel.setMaxWidth(Double.MAX_VALUE);
                btnDel.getStyleClass().add("BExcluir");
                btnDel.getStyleClass().add("icon-delete");

                btnDel.setOnAction(event -> {
                    Pagamento pagamento = getTableView().getItems().get(getIndex());

                    pagamentos.remove(pagamento);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btnDel);
                    HBox wrapper = new HBox(btnDel);
                    wrapper.setSpacing(0);
                    wrapper.setPadding(new Insets(0));
                    wrapper.setFillHeight(true);

                    wrapper.setMaxWidth(Double.MAX_VALUE);

                    IconHelper.applyIcon(btnDel);

                    setGraphic(wrapper);
                }
            }
        });

        pagamentoTable.setItems(pagamentos);
    }

    public void initPagamentoObservableList() {
        pagamentos.addListener((ListChangeListener<Pagamento>) change -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    String valorPago = String.format("R$ %.2f", pagamentos.stream().mapToDouble(Pagamento::getValor)
                            .sum());

                    LValorPago.setText(valorPago);
                }

                if (change.wasRemoved()) {
                    String valorPago = String.format("R$ %.2f", pagamentos.stream().mapToDouble(Pagamento::getValor)
                            .sum());

                    LValorPago.setText(valorPago);
                }
            }
        });
    }

    public void initLValorAPagar() {
        LValorAPagar.setText(String.format("R$ %.2f", valorPedido));
    }

    private <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }
}
