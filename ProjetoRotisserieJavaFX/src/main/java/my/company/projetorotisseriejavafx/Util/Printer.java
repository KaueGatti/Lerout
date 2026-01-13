package my.company.projetorotisseriejavafx.Util;

import my.company.projetorotisseriejavafx.Objects.MarmitaVendida;
import my.company.projetorotisseriejavafx.Objects.Pedido;
import my.company.projetorotisseriejavafx.Objects.ProdutoVendido;

import javax.print.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Printer {

    public static void printOrder(Pedido pedido, List<MarmitaVendida> marmitas, List<ProdutoVendido> produtos) {

        Map<String, String> dadosPedidos = new HashMap<>();

        dadosPedidos.put("nomeCliente", pedido.getNomeCliente() != null ? pedido.getNomeCliente().toUpperCase() : "");
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        dadosPedidos.put("data", LocalDate.now().format(dtf));

        StringBuilder marmitasSB = new StringBuilder();

        for (int i = 0; i < marmitas.size(); i++) {
            marmitasSB.append("[bold]");
            marmitasSB.append(marmitas.get(i).getNome()).append("\n");
            marmitasSB.append("[/bold]");
            marmitasSB.append(marmitas.get(i).getDetalhes()).append("\n");
            marmitasSB.append("Observacao: ");
            marmitasSB.append(marmitas.get(i).getObservacao() != null ? marmitas.get(i).getObservacao() : "");

            if (i != marmitas.size() - 1) {
                marmitasSB.append("\n\n");
            }
        }

        String marmitasText = marmitasSB.toString();
        marmitasText = marmitasText.replace("Principais:", "[bold]Principais:[/bold]")
                .replace("Misturas:", "[bold]\nMisturas:[/bold]")
                .replace("Guarnicoes:", "[bold]\nGuarnicoes:[/bold]")
                .replace("Saladas:", "[bold]\nSaladas:[/bold]")
                .replace("Observacoes:", "[bold]\nObservacoes:[/bold]");

        StringBuilder produtosText = new StringBuilder();

        for (int i = 0; i < produtos.size(); i++) {
            produtosText.append(produtos.get(i).getNome()).append(" ");
            produtosText.append("x").append(produtos.get(i).getQuantidade()).append("\n");
        }

        dadosPedidos.put("MARMITAS", marmitas.isEmpty() ? "" : "MARMITAS");
        dadosPedidos.put("marmitas", marmitasText);
        dadosPedidos.put("PRODUTOS", produtos.isEmpty() ? "" : "PRODUTOS");
        dadosPedidos.put("produtos", produtosText.toString());
        dadosPedidos.put("endereco", pedido.getEndereco() != null ? pedido.getEndereco() : "");

        dadosPedidos.put("bairro", pedido.getBairro() != null ? pedido.getBairro().getNome() : "");
        dadosPedidos.put("pagamento", pedido.getTipoPagamento());
        dadosPedidos.put("total", pedido.getFormattedValorTotal());
        dadosPedidos.put("observacao", pedido.getObservacoes() != null ? pedido.getObservacoes() : "");
        dadosPedidos.put("entrega", pedido.getFormattedValorEntrega() != null ? pedido.getFormattedValorEntrega() : "R$ 0,00");
        try {
            String model_order = new String(Files.readAllBytes(Paths.get("src/main/resources/modelo_pedido.txt")));

            for (Map.Entry<String, String> entry : dadosPedidos.entrySet()) {
                String variavel = "${" + entry.getKey() + "}";
                String valor = entry.getValue() != null ? entry.getValue() : "?";
                model_order = model_order.replace(variavel, valor);
            }

            model_order = normalizar(model_order);

            byte[] bytes = PosFormatter.process(model_order);

            PrintService service = PrintServiceLookup.lookupDefaultPrintService();
            DocPrintJob job = service.createPrintJob();

            DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;
            Doc doc = new SimpleDoc(bytes, flavor, null);

            job.print(doc, null);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static String normalizar(String texto) {
        return texto
                .replace("á", "a")
                .replace("à", "a")
                .replace("ã", "a")
                .replace("â", "a")
                .replace("Á", "A")
                .replace("é", "e")
                .replace("ê", "e")
                .replace("É", "E")
                .replace("í", "i")
                .replace("Í", "I")
                .replace("ó", "o")
                .replace("ô", "o")
                .replace("õ", "o")
                .replace("Ó", "O")
                .replace("ú", "u")
                .replace("Ú", "U")
                .replace("ç", "c")
                .replace("Ç", "C");
    }
}
