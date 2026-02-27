package my.company.projetorotisseriejavafx.Controller.Modal;

import java.io.IOException;
import java.util.List;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import my.company.projetorotisseriejavafx.Objects.MarmitaVendida;
import my.company.projetorotisseriejavafx.Objects.ProdutoVendido;
import my.company.projetorotisseriejavafx.Util.CssHelper;

public class ModalMarmitasEProdutosController {

    @FXML
    private TableView<MarmitaVendida> tableMarmita;
    @FXML
    private TableColumn<MarmitaVendida, String> colNomeMarmita;
    @FXML
    private TableColumn<MarmitaVendida, Integer> colQuantidadeMarmita;
    @FXML
    private TableColumn<MarmitaVendida, Double> colSubtotalMarmita;
    @FXML
    private TableView<ProdutoVendido> tableProduto;
    @FXML
    private TableColumn<ProdutoVendido, String> colNomeProduto;
    @FXML
    private TableColumn<ProdutoVendido, Integer> colQuantidadeProduto;
    @FXML
    private TableColumn<ProdutoVendido, Double> colSubtotalProduto;
    @FXML
    private AnchorPane root;

    public void initialize(List<MarmitaVendida> marmitas, List<ProdutoVendido> produtos) {
        initTableMarmita();
        initTableProduto();
        loadMarmitasEProdutos(marmitas, produtos);
    }

    public void loadMarmitasEProdutos(List<MarmitaVendida> marmitas, List<ProdutoVendido> produtos) {
        if (!marmitas.isEmpty()) {
            tableMarmita.getItems().addAll(marmitas);
        }

        if (!produtos.isEmpty()) {
            tableProduto.getItems().addAll(produtos);
        }
    }

    private void initTableMarmita() {
        colNomeMarmita.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colQuantidadeMarmita.setCellValueFactory(new PropertyValueFactory<>("quantidade"));
        colSubtotalMarmita.setCellValueFactory(new PropertyValueFactory<>("formattedSubtotal"));

        tableMarmita.setOnMouseClicked(event -> {
                    if (event.getClickCount() == 2) {
                        if (tableMarmita.getSelectionModel().getSelectedItem() != null) {
                            abrirModalDetalhesMarmita(tableMarmita.getSelectionModel().getSelectedItem());
                        }
                    }
                }
        );
    }

    private void initTableProduto() {
        colNomeProduto.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colQuantidadeProduto.setCellValueFactory(new PropertyValueFactory<>("quantidade"));
        colSubtotalProduto.setCellValueFactory(new PropertyValueFactory<>("formattedSubtotal"));
    }

    public void abrirModalDetalhesMarmita(MarmitaVendida marmita) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Modal/modalDetalhesMarmita.fxml"));
            Parent root = loader.load();

            Stage modal = new Stage();
            Scene scene = new Scene(root);

            CssHelper.loadCss(scene);

            modal.setScene(scene);

            ModalDetalhesMarmitaController controller = loader.getController();

            controller.load(marmita);

            modal.setResizable(false);
            modal.initStyle(StageStyle.UTILITY);
            modal.initModality(Modality.APPLICATION_MODAL);
            modal.showAndWait();

        } catch (IOException e) {
            System.out.println("Erro Modal Detalhes Marmita:");
            e.printStackTrace();
        }
    }
}
