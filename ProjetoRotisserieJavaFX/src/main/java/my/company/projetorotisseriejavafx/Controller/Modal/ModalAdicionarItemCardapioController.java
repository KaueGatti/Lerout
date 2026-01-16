package my.company.projetorotisseriejavafx.Controller.Modal;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import my.company.projetorotisseriejavafx.Objects.ItemCardapio;

public class ModalAdicionarItemCardapioController {

    private ObservableList<ItemCardapio> lista;
    private String categoria;

    @FXML
    private Label LInfo;

    @FXML
    private Label LTitulo;

    @FXML
    private TextField TFNome;

    @FXML
    private Button btnAdicionar;

    @FXML
    private AnchorPane root;

    public void initialize(String categoria, ObservableList<ItemCardapio> lista){
        this.categoria = categoria;
        this.lista = lista;
        loadLTitulo();
        TFNome.requestFocus();
    }

    @FXML
    void adicionar(ActionEvent event) {
        if (!validaCampos()) return;

        ItemCardapio itemCardapio = new ItemCardapio();

        itemCardapio.setNome(TFNome.getText());
        itemCardapio.setCategoria(categoria);

        lista.add(itemCardapio);

        fecharModal();
    }

    public void loadLTitulo() {
        LTitulo.setText("Adicionar " +  categoria);
    }

    public boolean validaCampos() {
        if (TFNome.getText().trim().isEmpty()) {
            LInfo.setText("Nome não pode estar vázio");
            return false;
        }

        return true;
    }

    public void fecharModal() {
        Stage modal =  (Stage) root.getScene().getWindow();
        modal.close();
    }

}
