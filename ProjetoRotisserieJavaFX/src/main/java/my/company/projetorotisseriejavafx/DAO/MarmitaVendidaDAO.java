package my.company.projetorotisseriejavafx.DAO;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javafx.collections.ObservableList;
import my.company.projetorotisseriejavafx.DB.DatabaseConnection;
import my.company.projetorotisseriejavafx.Objects.MarmitaVendida;
import my.company.projetorotisseriejavafx.Objects.MarmitasVendidas;

public class MarmitaVendidaDAO {

    static public int criar(ObservableList<MarmitaVendida> mvs, int idPedido) throws SQLException {
        String sql = """
                        INSERT INTO Marmita_Vendida (id_pedido, id_marmita, quantidade, subtotal, detalhes, observacao)
                        VALUES (?, ?, ?, ?, ?, ?)
                     """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            for (MarmitaVendida mv : mvs) {

                stmt.setInt(1, idPedido);
                stmt.setInt(2, mv.getIdMarmita());
                stmt.setInt(3, mv.getQuantidade());
                stmt.setDouble(4, mv.getSubtotal());
                stmt.setString(5, mv.getDetalhes());
                stmt.setString(6, mv.getObservacao());

                stmt.addBatch();
            }

            stmt.executeBatch();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    static public void deletar(int id) throws SQLException {
        String sql = "DELETE FROM Marmita_Vendida WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    static public List<MarmitaVendida> listarPorPedido(int idPedido) throws SQLException {
        String sql = "SELECT MV.*, M.nome as nome_marmita FROM Marmita_Vendida AS MV " +
                "JOIN Marmita AS M ON M.id = MV.id_marmita WHERE MV.id_pedido = ?";
        List<MarmitaVendida> lista = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idPedido);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    MarmitaVendida mv = new MarmitaVendida();
                    mv.setId(rs.getInt("id"));
                    mv.setIdMarmita(rs.getInt("id_marmita"));
                    mv.setNome(rs.getString("nome_marmita"));
                    mv.setQuantidade(rs.getInt("quantidade"));
                    mv.setDetalhes(rs.getString("detalhes"));
                    mv.setSubtotal(rs.getDouble("subtotal"));
                    mv.setObservacao(rs.getString("observacao"));
                    lista.add(mv);
                }
            }
        }
        return lista;
    }

    static public List<MarmitaVendida> listarPorData(Date data) throws SQLException {
        String sql = "SELECT MV.*, M.nome AS nome_marmita, P.date_time AS data FROM Marmita_Vendida AS MV " +
                "JOIN Marmita AS M ON M.id = MV.id_marmita " +
                "JOIN Pedido AS P ON P.id = MV.id_pedido " +
                "WHERE DATE(P.date_time) = DATE(?)";

        List<MarmitaVendida> lista = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, data);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    MarmitaVendida mv = new MarmitaVendida();
                    mv.setId(rs.getInt("id"));
                    mv.setIdMarmita(rs.getInt("id_marmita"));
                    mv.setNome(rs.getString("nome_marmita"));
                    mv.setQuantidade(rs.getInt("quantidade"));
                    mv.setDetalhes(rs.getString("detalhes"));
                    mv.setSubtotal(rs.getDouble("subtotal"));
                    mv.setObservacao(rs.getString("observacao"));
                    lista.add(mv);
                }
            }
        }
        return lista;
    }

    static public List<MarmitasVendidas> listarMarmitasVendidas(LocalDate data) throws SQLException {
        String sql = """
                SELECT
                    M.nome,
                    SUM(MV.quantidade) AS qtd,
                    SUM(MV.subtotal) AS subtotal
                FROM Marmita_Vendida AS MV
                JOIN Marmita AS M ON M.id = MV.id_marmita
                JOIN Pedido AS P ON P.id = MV.id_pedido
                WHERE
                    strftime('%Y-%m-%d', P.date_time) = ?
                GROUP BY
                    M.nome
                """;

        List<MarmitasVendidas> lista = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {


            stmt.setString(1, data.toString());

            try (ResultSet rs = stmt.executeQuery()) {
                System.out.println(rs.getFetchSize());
                while (rs.next()) {
                    String nome = rs.getString("nome");
                    Integer qtd = rs.getInt("qtd");
                    double subtotal = rs.getDouble("subtotal");
                    MarmitasVendidas mv = new MarmitasVendidas(nome, qtd, subtotal);
                    lista.add(mv);
                }
            }
        }

        System.out.println(lista.size());
        return lista;
    }
}
