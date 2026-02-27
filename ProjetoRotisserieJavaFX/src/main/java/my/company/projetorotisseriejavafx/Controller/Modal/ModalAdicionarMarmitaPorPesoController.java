package my.company.projetorotisseriejavafx.Controller.Modal;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import my.company.projetorotisseriejavafx.Objects.MarmitaVendida;
import my.company.projetorotisseriejavafx.Util.CurrencyFieldUtil;

import java.net.URL;
import java.util.ResourceBundle;

public class ModalAdicionarMarmitaPorPesoController implements Initializable {

    MarmitaVendida marmitaVendida = null;

    @FXML
    private Label LInfo;

    @FXML
    private TextArea TAObservacao;

    @FXML
    private TextField TFValor;

    @FXML
    private Button btnAdicionar;

    @FXML
    private Button btnCancelar;

    @FXML
    private AnchorPane root;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initCampos();
    }

    @FXML
    void adicionar(ActionEvent event) {
        MarmitaVendida marmitaVendida = new MarmitaVendida();

        if (!validaCampos()) return;

        marmitaVendida.setIdMarmita(1);
        marmitaVendida.setNome("Peso");
        marmitaVendida.setQuantidade(1);
        marmitaVendida.setSubtotal(CurrencyFieldUtil.getValue(TFValor));
        marmitaVendida.setObservacao(TAObservacao.getText());

        this.marmitaVendida = marmitaVendida;

        fecharModal();
    }

    @FXML
    void cancelar(ActionEvent event) {
        fecharModal();
    }

    private void fecharModal() {
        Stage modal = (Stage) root.getScene().getWindow();
        modal.close();
    }

    public MarmitaVendida getMarmitaPorPeso() {
        return marmitaVendida;
    }

    public boolean validaCampos() {
        if (TFValor.getText().trim().isEmpty()) {
            LInfo.setText("Valor não pode ser vazio");
            return false;
        }

        if (CurrencyFieldUtil.getValue(TFValor) <= 0) {
            LInfo.setText("O valo deve ser maior que 00,00");
            return false;
        }

        return true;
    }

    public void initCampos() {
        CurrencyFieldUtil.configureField(TFValor, false, false, false);
    }
}
