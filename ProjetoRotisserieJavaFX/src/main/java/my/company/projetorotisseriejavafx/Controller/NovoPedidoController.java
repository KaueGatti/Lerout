package my.company.projetorotisseriejavafx.Controller;

import java.io.IOException;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;

import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.stage.*;
import my.company.projetorotisseriejavafx.Controller.Modal.*;
import my.company.projetorotisseriejavafx.Controller.Pane.PaneMarmitaController;
import my.company.projetorotisseriejavafx.Controller.Pane.PaneProdutoController;
import my.company.projetorotisseriejavafx.DAO.*;
import my.company.projetorotisseriejavafx.Objects.*;
import my.company.projetorotisseriejavafx.Util.CssHelper;
import my.company.projetorotisseriejavafx.Util.DatabaseExceptionHandler;
import my.company.projetorotisseriejavafx.Util.IconHelper;
import my.company.projetorotisseriejavafx.Util.Printer;

import javax.swing.*;

public class NovoPedidoController {

    private double valorEntrega = 0;
    private double valorItens = 0;
    private double valorTotal = 0;
    private ObservableList<DescontoAdicional> descontosEAdicionais = FXCollections.observableArrayList();

    private String pagamento;
    private ObservableList<Pagamento> pagamentos = FXCollections.observableArrayList();
    private LocalDate vencimento;
    private boolean print;

    @FXML
    private ToggleGroup tipo;
    @FXML
    private Button btnFinalizar;
    @FXML
    private Button btnCancelar;
    @FXML
    private Button btnDescontosEAdicionais;

    @FXML
    private Button btnMinimizar;
    @FXML
    private Button btnMaximizar;
    @FXML
    private Button btnNext1;

    @FXML
    private Pane panePrincipal;

    @FXML
    private TableView<MarmitaVendida> tableMarmita;
    @FXML
    private TableColumn<MarmitaVendida, String> colNomeMarmita;
    @FXML
    private TableColumn<MarmitaVendida, Integer> colQuantidadeMarmita;
    @FXML
    private TableColumn<MarmitaVendida, Double> colSubtotalMarmita;
    @FXML
    private TableColumn<MarmitaVendida, Void> colDelMarmita;

    @FXML
    private TableView<ProdutoVendido> tableProduto;
    @FXML
    private TableColumn<ProdutoVendido, String> colNomeProduto;
    @FXML
    private TableColumn<ProdutoVendido, Integer> colQuantidadeProduto;
    @FXML
    private TableColumn<ProdutoVendido, Double> colSubtotalProduto;
    @FXML
    private TableColumn<ProdutoVendido, Void> colDelProduto;

    @FXML
    private ToggleButton tabButtonLeft;
    @FXML
    private ToggleButton tabButtonRight;
    @FXML
    private ComboBox<Bairro> comboBoxBairro;
    @FXML
    private ComboBox<Cliente> comboBoxCliente;
    @FXML
    private CheckBox checkBoxCliente;
    @FXML
    private RadioButton RBEntrega;
    @FXML
    private RadioButton RBBalcao;
    @FXML
    private Label labelCliente;
    @FXML
    private TextField TFNomeCliente;
    @FXML
    private TextArea TAEndereco;
    @FXML
    private TextArea TAObservacoes;
    @FXML
    private Label labelValorEntrega;
    @FXML
    private Label labelValorTotal;
    @FXML
    private Label LInfoPedido;
    @FXML
    private AnchorPane APMarmitaProduto;
    @FXML
    private Pane paneEndereco;

    public NovoPedidoController() {
    }

    public void initialize() {
        initDescontosEAdicionais();
        initTableMarmita();
        initTableProduto();
        loadCliente();
        loadBairro();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Pane/paneMarmita.fxml"));
            Pane marmitaPane = loader.load();
            PaneMarmitaController marmitaController = loader.getController();
            marmitaController.setController(this);
            APMarmitaProduto.getChildren().clear();
            APMarmitaProduto.getChildren().add(marmitaPane);
        } catch (IOException e) {
            System.out.println("Erro ao carregar PaneMarmita: " + e);
            e.printStackTrace();
        }
    }

    @FXML
    private void finalizar() {
        finalizarPedido();
    }

    @FXML
    private void cancelar() {
        close();
    }

    @FXML
    private void marmitaClicked(ActionEvent event) {
        if (tabButtonLeft.isSelected()) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Pane/paneMarmita.fxml"));
                Pane marmitaPane = loader.load();

                IconHelper.applyIconsTo(marmitaPane);

                PaneMarmitaController marmitaController = loader.getController();
                marmitaController.setController(this);
                APMarmitaProduto.getChildren().clear();
                APMarmitaProduto.getChildren().add(marmitaPane);
                tabButtonRight.setSelected(false);
            } catch (Exception e) {
                System.out.println("Erro marmitaClicked: " + e);
                e.printStackTrace();
            }
        } else {
            tabButtonLeft.setSelected(true);
        }
    }

    @FXML
    private void produtoClicked(ActionEvent event) {

        if (!validaProdutos()) {
            tabButtonRight.setSelected(false);
            return;
        }

        if (tabButtonRight.isSelected()) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Pane/paneProduto.fxml"));
                Pane produtoPane = loader.load();

                IconHelper.applyIconsTo(produtoPane);

                PaneProdutoController produtoController = loader.getController();
                produtoController.setController(this);
                APMarmitaProduto.getChildren().clear();
                APMarmitaProduto.getChildren().add(produtoPane);
                tabButtonLeft.setSelected(false);
            } catch (Exception e) {
                System.out.println("Erro produtoClicked: " + e);
                e.printStackTrace();
            }
        } else {
            tabButtonRight.setSelected(true);
        }
    }

    @FXML
    private void RBGTipo(ActionEvent event) {
        if (RBEntrega.isSelected()) {

            if (!validaBairro()) {
                RBBalcao.setSelected(true);
                return;
            }

            paneEndereco.setDisable(false);
            if (comboBoxBairro.getValue() != null) {
                valorEntrega = comboBoxBairro.getValue().getValorEntrega();
                atualizaValor();
            }
        } else {
            valorEntrega = 0;
            atualizaValor();
            paneEndereco.setDisable(true);
        }
    }

    @FXML
    private void minimizar() {
        abrirModalNovoPedidoTelaReduzida();
    }

    private void loadBairro() {
        comboBoxBairro.getItems().clear();

        try {
            List<Bairro> bairros = BairroDAO.listarAtivos();

            if (!bairros.isEmpty()) {
                comboBoxBairro.getItems().addAll(bairros);

                comboBoxBairro.setOnAction(e -> {
                    if (RBEntrega.isSelected()) {
                        valorEntrega = comboBoxBairro.getSelectionModel().getSelectedItem().getValorEntrega();
                        atualizaValor();
                    }
                });
            }

        } catch (SQLException e) {
            DatabaseExceptionHandler.handleException(e, "bairro");
        }


    }

    private void loadCliente() {
        comboBoxCliente.getItems().clear();

        try {
            List<Cliente> clientes = ClienteDAO.listarAtivos();
            if (!clientes.isEmpty()) {
                comboBoxCliente.getItems().addAll(clientes);
                comboBoxCliente.getSelectionModel().selectFirst();

                comboBoxCliente.setOnAction(e -> {
                    comboBoxBairro.getSelectionModel().select(comboBoxCliente.getValue().getBairro());
                    TAEndereco.setText(comboBoxCliente.getValue().getEndereco());
                });
            }
        } catch (SQLException e) {
            DatabaseExceptionHandler.handleException(e, "cliente");
        }
    }

    public void adicionarMarmita(MarmitaVendida marmitaVendida) {
        tableMarmita.getItems().add(marmitaVendida);
        valorItens += marmitaVendida.getSubtotal();
        atualizaValor();
    }

    public void adicionarProduto(ProdutoVendido produtoVendido) {
        tableProduto.getItems().add(produtoVendido);
        valorItens += produtoVendido.getSubtotal();
        atualizaValor();
    }

    private void atualizaValor() {
        NumberFormat formatoMoeda = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        String valorEntregaFormatado = formatoMoeda.format(valorEntrega);
        double valorDesconto = descontosEAdicionais.stream()
                .filter(desconto -> desconto.getTipo().equals("Desconto"))
                .mapToDouble(DescontoAdicional::getValor).sum();

        double valorAdicional = descontosEAdicionais.stream()
                .filter(adicional -> adicional.getTipo().equals("Adicional"))
                .mapToDouble(DescontoAdicional::getValor).sum();

        valorTotal = (valorEntrega + valorItens + valorAdicional) - valorDesconto;
        String valorTotalFormatado = formatoMoeda.format(valorTotal);
        labelValorEntrega.setText("Entrega " + valorEntregaFormatado);
        labelValorTotal.setText("Total " + valorTotalFormatado);
    }

    private boolean validaPedido(Pedido pedido) {
        if (tableMarmita.getItems().isEmpty() && tableProduto.getItems().isEmpty()) {
            LInfoPedido.setText("Adicione pelo menos uma marmita/produto");
            return false;
        }

        if (pedido.getTipoPedido().equals("Entrega")) {
            if (pedido.getEndereco().trim().isEmpty()) {
                LInfoPedido.setText("Endereço não pode estar vazio");
                return false;
            }
        }

        LInfoPedido.setText("");
        return true;
    }

    @FXML
    private void checkBoxCliente(ActionEvent event) {
        if (checkBoxCliente.isSelected()) {

            if (!validaCliente()) {
                checkBoxCliente.setSelected(false);
                return;
            }

            comboBoxCliente.setDisable(false);
            labelCliente.setDisable(true);
            TFNomeCliente.setDisable(true);

            comboBoxBairro.getSelectionModel().select(comboBoxCliente.getValue().getBairro());
            TAEndereco.setText(comboBoxCliente.getValue().getEndereco());
        } else {
            comboBoxCliente.setDisable(true);
            labelCliente.setDisable(false);
            TFNomeCliente.setDisable(false);
        }
    }

    @FXML
    private void descontosEAdicionais() {
        abrirModalDescontosEAdicionais();
    }

    private void close() {
        ((AnchorPane) panePrincipal.getParent()).getChildren().clear();
    }

    public ObservableList<DescontoAdicional> getDescontosEAdicionais() {
        return descontosEAdicionais;
    }

    public void initDescontosEAdicionais() {
        descontosEAdicionais.addListener((ListChangeListener<DescontoAdicional>) change -> {
            while (change.next()) {
                atualizaValor();
            }
        });
    }

    public void setDescontosEAdicionais(ObservableList<DescontoAdicional> descontosEAdicionais) {
        this.descontosEAdicionais = descontosEAdicionais;
    }

    public void finalizarPedido() {
        Pedido pedido = new Pedido();

        if (checkBoxCliente.isSelected()) {
            pedido.setCliente(comboBoxCliente.getValue());
        } else {
            pedido.setNomeCliente(TFNomeCliente.getText());
        }

        if (RBEntrega.isSelected()) {
            if (comboBoxBairro.getValue() == null) {
                LInfoPedido.setText("Selecione um bairro");
                return;
            }
            pedido.setTipoPedido("Entrega");
            pedido.setEndereco(TAEndereco.getText());
            pedido.setBairro(comboBoxBairro.getValue());
        } else {
            pedido.setTipoPedido("Balcao");
        }

        pedido.setObservacoes(TAObservacoes.getText());
        pedido.setValorEntrega(valorEntrega);
        pedido.setValorTotal(valorTotal);

        if (validaPedido(pedido)) {

            if (!abrirModalPagamento(valorTotal)) return;

            // Soma todos os pagamentos e define o valor pago
            double valorPago = pagamentos.stream().mapToDouble(Pagamento::getValor).sum();
            pedido.setValorPago(valorPago);

            pedido.setTipoPagamento(pagamento);
            pedido.setVencimento(vencimento);

            List<MarmitaVendida> marmitas = tableMarmita.getItems();
            List<ProdutoVendido> produtos = tableProduto.getItems();

            if (print) {
                Printer.printOrder(pedido, marmitas, produtos);
            }

            try {
                int idPedido = PedidoDAO.criar(pedido);
                pedido.setId(idPedido);

                if (!marmitas.isEmpty()) {
                    MarmitaVendidaDAO.criar(tableMarmita.getItems(), pedido.getId());
                }

                if (!produtos.isEmpty()) {
                    ProdutoVendidoDAO.criar(tableProduto.getItems(), pedido.getId());
                }

                if (!descontosEAdicionais.isEmpty()) {
                    DescontoAdicionalDAO.criar(descontosEAdicionais, pedido.getId());
                }

                PagamentoDAO.criar(pagamentos, pedido.getId());
            } catch (SQLException e) {
                DatabaseExceptionHandler.handleException(e, "Pedido");
            }

            close();
        }
    }

    public void abrirModalDescontosEAdicionais() {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Modal/modalDescontosEAdicionais.fxml"));
            Parent root = loader.load();

            Stage modal = new Stage();
            Scene scene = new Scene(root);

            CssHelper.loadCss(scene);

            modal.setScene(scene);

            ModalDescontosEAdicionaisController controller = loader.getController();

            controller.initialize(this, descontosEAdicionais);

            modal.setResizable(false);
            modal.initStyle(StageStyle.UTILITY);
            modal.initModality(Modality.APPLICATION_MODAL);
            modal.showAndWait();

            atualizaValor();

        } catch (IOException e) {
            System.out.println("Erro ao abrir modal descontos e adicionais" + e);
            e.printStackTrace();
        }

    }

    public boolean abrirModalPagamento(Double valorTotal) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Modal/modalPagamento.fxml"));
            Parent root = loader.load();

            Stage modal = new Stage();
            Scene scene = new Scene(root);

            CssHelper.loadCss(scene);

            IconHelper.applyIconsTo(root);

            modal.setScene(scene);

            ModalPagamentoController controller = loader.getController();

            controller.initialize(valorTotal, pagamentos);

            modal.setResizable(false);
            modal.initModality(Modality.APPLICATION_MODAL);
            modal.initStyle(StageStyle.UTILITY);
            modal.showAndWait();

            if (!controller.getFinalizado()) {
                return false;
            }

            pagamento = controller.getPagamento();
            vencimento = controller.getVencimento();
            print = controller.getPrint();

            return true;

        } catch (IOException e) {
            System.out.println("Erro ao abrir modal pagamento" + e);
            e.printStackTrace();
            return false;
        }
    }

    public boolean validaProdutos() {
        try {
            if (ProdutoDAO.listarAtivos().isEmpty()) {
                String msg = "Você ainda não tem nenhum produto ativo ou cadastrado!";
                abrirModalAvisoNovoPedido(msg, new Pedido());
                return false;
            }
            return true;
        } catch (SQLException e) {
            DatabaseExceptionHandler.handleException(e, "produto");
        }
        return false;
    }

    public boolean validaBairro() {
        try {
            if (BairroDAO.listarAtivos().isEmpty()) {
                String msg = "Você ainda não tem nenhum bairro ativo ou cadastrado!";
                abrirModalAvisoNovoPedido(msg, new Pedido());
                return false;
            }
            return true;
        } catch (SQLException e) {
            DatabaseExceptionHandler.handleException(e, "bairro");
        }
        return false;
    }

    public boolean validaCliente() {
        try {
            if (ClienteDAO.listarAtivos().isEmpty()) {
                String msg = "Você ainda não tem nenhum cliente ativo ou cadastrado!";
                abrirModalAvisoNovoPedido(msg, new Cliente());
                return false;
            }
            return true;
        } catch (SQLException e) {
            DatabaseExceptionHandler.handleException(e, "cliente");
        }
        return false;
    }

    public boolean abrirModalAvisoNovoPedido(String msg, Object tipo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Modal/modalAvisoNovoPedido.fxml"));
            Parent root = loader.load();

            Stage modal = new Stage();
            Scene scene = new Scene(root);

            CssHelper.loadCss(scene);

            modal.setScene(scene);

            ModalAvisoNovoPedidoController controller = loader.getController();

            controller.initialize(msg, tipo);

            modal.initStyle(StageStyle.UTILITY);
            modal.initModality(Modality.APPLICATION_MODAL);
            modal.setResizable(false);
            modal.showAndWait();

            return controller.isCadastrar();

        } catch (IOException e) {
            System.out.println("Erro ao abrir aviso novo pedido");
            e.printStackTrace();
        }

        return false;
    }

    public boolean abrirModalNovoPedidoTelaReduzida() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Modal/ModalNovoPedidoTelaReduzida.fxml"));
            Parent root = loader.load();

            Stage modal = new Stage();
            Scene scene = new Scene(root);

            CssHelper.loadCss(scene);
            IconHelper.applyIconsTo(root);

            modal.setScene(scene);

            ModalNovoPedidoTelaReduzidaController controller = loader.getController();

            controller.initialize();

            modal.initStyle(StageStyle.UTILITY);
            modal.initModality(Modality.APPLICATION_MODAL);
            modal.setResizable(false);
            minimizarApp();
            modal.showAndWait();

        } catch (IOException e) {
            System.out.println("Erro ao abrir modal novo pedido tela reduzida");
            e.printStackTrace();
        }

        return false;
    }

    private void initTableMarmita() {
        colNomeMarmita.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colQuantidadeMarmita.setCellValueFactory(new PropertyValueFactory<>("quantidade"));
        colSubtotalMarmita.setCellValueFactory(new PropertyValueFactory<>("formattedSubtotal"));
        colDelMarmita.setCellFactory(param -> new TableCell<>() {


            private final Button btnExcluir = new Button("");

            {
                btnExcluir.setMaxWidth(Double.MAX_VALUE);
                btnExcluir.getStyleClass().add("BExcluir");
                btnExcluir.getStyleClass().add("icon-delete");

                btnExcluir.setOnAction(event -> {
                    MarmitaVendida marmita = getTableView().getItems().get(getIndex());
                    valorItens -= marmita.getSubtotal();
                    getTableView().getItems().remove(marmita);
                    atualizaValor();
                });

                HBox.setHgrow(btnExcluir, Priority.ALWAYS);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                } else {
                    HBox wrapper = new HBox(btnExcluir);
                    wrapper.setSpacing(0);
                    wrapper.setPadding(new Insets(0));
                    wrapper.setFillHeight(true);

                    wrapper.setMaxWidth(Double.MAX_VALUE);

                    IconHelper.applyIcon(btnExcluir);

                    setGraphic(wrapper);
                }
            }
        });

        tableMarmita.setOnMouseClicked(event -> {
                    if (event.getClickCount() == 2) {
                        if (tableMarmita.getSelectionModel().getSelectedItem() != null) {
                            try {

                                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Modal/modalDetalhesMarmita.fxml"));
                                Parent root = loader.load();

                                Stage modal = new Stage();
                                Scene scene = new Scene(root);

                                CssHelper.loadCss(scene);

                                modal.setScene(scene);

                                ModalDetalhesMarmitaController controller = loader.getController();

                                controller.load(tableMarmita.getSelectionModel().getSelectedItem());

                                modal.setOnCloseRequest(eventClose -> {
                                    event.consume();
                                });
                                modal.setResizable(false);
                                modal.initStyle(StageStyle.UTILITY);
                                modal.showAndWait();

                            } catch (IOException e) {
                                System.out.println("Erro Modal Detalhes Marmita:");
                                e.printStackTrace();
                            }
                        }
                    }
                }
        );

    }

    private void initTableProduto() {
        colNomeProduto.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colQuantidadeProduto.setCellValueFactory(new PropertyValueFactory<>("quantidade"));
        colSubtotalProduto.setCellValueFactory(new PropertyValueFactory<>("formattedSubtotal"));
        colDelProduto.setCellFactory(param -> new TableCell<>() {
            private final Button btnExcluir = new Button("");

            {
                btnExcluir.setMaxWidth(Double.MAX_VALUE);
                btnExcluir.getStyleClass().add("BExcluir");
                btnExcluir.getStyleClass().add("icon-delete");

                btnExcluir.setOnAction(event -> {
                    ProdutoVendido produtoVendido = getTableRow().getItem();
                    valorItens -= produtoVendido.getSubtotal();
                    getTableView().getItems().remove(produtoVendido);
                    atualizaValor();
                });

                HBox.setHgrow(btnExcluir, Priority.ALWAYS);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                } else {
                    HBox wrapper = new HBox(btnExcluir);
                    wrapper.setSpacing(0);
                    wrapper.setPadding(new Insets(0));
                    wrapper.setFillHeight(true);

                    wrapper.setMaxWidth(Double.MAX_VALUE);

                    IconHelper.applyIcon(btnExcluir);

                    setGraphic(wrapper);
                }
            }
        });
    }

    private void minimizarApp() {
        Stage window = (Stage) panePrincipal.getParent().getScene().getWindow();
        window.setIconified(true);
    }
}
