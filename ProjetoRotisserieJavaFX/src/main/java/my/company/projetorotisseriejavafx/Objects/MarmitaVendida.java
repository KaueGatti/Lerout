package my.company.projetorotisseriejavafx.Objects;

import java.util.ArrayList;
import java.util.List;

public class MarmitaVendida {

    private int id;
    private int idMarmita;
    private String nome;
    private String detalhes;
    private List<String> principais;
    private List<String> misturas;
    private List<String> guarnicoes;
    private List<String> saladas;
    private String observacao;
    private double subtotal;

    public int getIdMarmita() {
        return idMarmita;
    }

    public void setIdMarmita(int idMarmita) {
        this.idMarmita = idMarmita;
    }


    public void setDetalhes(String detalhes) {
        this.detalhes = detalhes;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public List<String> getSalada() {
        return saladas;
    }

    public void setSalada(List<String> saladas) {
        this.saladas = saladas;
    }

    public List<String> getPrincipais() {
        return principais;
    }

    public void setPrincipais(List<String> principais) {
        this.principais = principais;
    }

    public List<String> getMisturas() {
        return misturas;
    }

    public void setMisturas(List<String> misturas) {
        this.misturas = misturas;
    }

    public List<String> getGuarnicoes() {
        return guarnicoes;
    }

    public void setGuarnicoes(List<String> guarnicoes) {
        this.guarnicoes = guarnicoes;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public String getFormattedSubtotal() {
        return "R$ " + String.valueOf(subtotal).replace(".", ",");
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDetalhes() {
        if (detalhes != null) {
            return detalhes;
        }

        StringBuilder detalhesSB = new StringBuilder();

        List<String> detalhesList = new ArrayList<>(principais);

        for (int i = 0; i < detalhesList.size(); i++) {
            detalhesSB.append(detalhesList.get(i));

            if (i != detalhesList.size() - 1) {
                detalhesSB.append(", ");
            }
        }

        detalhesSB.append("\n\n");
        detalhesList = misturas;


        for (int i = 0; i < detalhesList.size(); i++) {
            detalhesSB.append(detalhesList.get(i));

            if (i != detalhesList.size() - 1) {
                detalhesSB.append(", ");
            }
        }

        detalhesSB.append("\n\n");
        detalhesList = guarnicoes;

        for (int i = 0; i < detalhesList.size(); i++) {
            detalhesSB.append(detalhesList.get(i));

            if (i != detalhesList.size() - 1) {
                detalhesSB.append(", ");
            }
        }

        detalhesSB.append("\n\n");
        detalhesList = saladas;

        for (int i = 0; i < detalhesList.size(); i++) {
            detalhesSB.append(detalhesList.get(i));

            if (i != detalhesList.size() - 1) {
                detalhesSB.append(", ");
            }
        }

        detalhesSB.append("\n");

        this.detalhes = detalhesSB.toString();

        return this.detalhes;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }
}
