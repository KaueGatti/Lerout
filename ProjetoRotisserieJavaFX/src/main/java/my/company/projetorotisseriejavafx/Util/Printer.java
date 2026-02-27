package my.company.projetorotisseriejavafx.Util;

import my.company.projetorotisseriejavafx.Objects.MarmitaVendida;
import my.company.projetorotisseriejavafx.Objects.Pedido;
import my.company.projetorotisseriejavafx.Objects.ProdutoVendido;

import javax.print.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
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
            marmitasSB.append(marmitas.get(i).getQuantidade()).append(" - ");
            marmitasSB.append(marmitas.get(i).getNome()).append("\n");
            marmitasSB.append("[/bold]");

            marmitasSB.append(marmitas.get(i).getDetalhes());

            if (marmitas.get(i).getObservacao() != null) {
                if (!marmitas.get(i).getObservacao().isEmpty()) {
                    marmitasSB.append(marmitas.get(i).getObservacao() != null ? "Observacao: " + marmitas.get(i).getObservacao() : "");
                }
            }

            if (i != marmitas.size() - 1) {
                marmitasSB.append("\n\n");
            }
        }

        marmitasSB.append("\n");

        String marmitasText = marmitasSB.toString();

        StringBuilder produtosText = new StringBuilder();

        for (ProdutoVendido produto : produtos) {
            produtosText.append(produto.getNome()).append(" ");
            produtosText.append("x").append(produto.getQuantidade()).append("\n");
        }

        dadosPedidos.put("MARMITAS", marmitas.isEmpty() ? "" : "MARMITAS\n\n" + marmitasText);
        dadosPedidos.put("PRODUTOS", produtos.isEmpty() ? "" : "PRODUTOS\n\n" + produtosText);

        if (pedido.getTipoPedido().equals("Entrega")) {
            dadosPedidos.put("Endereco:", "Endereco: " + pedido.getEndereco());
            dadosPedidos.put("Bairro:", "Bairro: " + pedido.getBairro().getNome());
            dadosPedidos.put("entrega", pedido.getFormattedValorEntrega());
        } else {
            dadosPedidos.put("Endereco:", "${rl}");
            dadosPedidos.put("Bairro:", "[wide]Balcão/Retirada[/wide]");
            dadosPedidos.put("entrega", "${rl}");
        }

        dadosPedidos.put("pagamento", pedido.getTipoPagamento());

        if (pedido.getTipoPagamento().contains("Dinheiro") && pedido.getValorPago() > pedido.getValorTotal()) {
            double valorTroco = pedido.getValorTotal() - pedido.getValorPago();
            dadosPedidos.put("troco", String.valueOf(valorTroco));
        }

        dadosPedidos.put("total", pedido.getFormattedValorTotal());

        if (pedido.getObservacoes() != null && !pedido.getObservacoes().isEmpty()) {
            dadosPedidos.put("PedidoObservacao", "[wide][bold]Observacoes: " + pedido.getObservacoes() + "[/bold][/wide]");
        } else {
            dadosPedidos.put("PedidoObservacao", "${rl}");
        }

        try {

            InputStream is = Printer.class.getResourceAsStream("/modelo_pedido.txt");
            if (is == null) {
                throw new IOException("Template modelo_pedido.txt não encontrado no JAR");
            }
            byte[] templateBytes = is.readAllBytes();
            is.close();

            String model_order = new String(templateBytes, StandardCharsets.UTF_8);

            for (Map.Entry<String, String> entry : dadosPedidos.entrySet()) {
                String variavel = "${" + entry.getKey() + "}";
                String valor = entry.getValue() != null ? entry.getValue() : "?";
                model_order = model_order.replace(variavel, valor);
            }

            if (marmitas.isEmpty()) {
                model_order = model_order.replaceAll("(?m)^.*MARMITAS.*\\R?", "");
            }

            if (produtos.isEmpty()) {
                model_order = model_order.replaceAll("(?m)^.*PRODUTOS.*\\R?", "");
            }

            model_order = model_order.replaceAll("(?m)^.*\\$\\{rl}.*\\R?", "");

            model_order = Normalize.normalize(model_order);

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
                .replaceAll("á", "a")
                .replaceAll("à", "a")
                .replaceAll("ã", "a")
                .replaceAll("â", "a")
                .replaceAll("Á", "A")
                .replaceAll("é", "e")
                .replaceAll("ê", "e")
                .replaceAll("É", "E")
                .replaceAll("í", "i")
                .replaceAll("Í", "I")
                .replaceAll("ó", "o")
                .replaceAll("ô", "o")
                .replaceAll("õ", "o")
                .replaceAll("Ó", "O")
                .replaceAll("ú", "u")
                .replaceAll("Ú", "U")
                .replaceAll("ç", "c")
                .replaceAll("Ç", "C");
    }
}
