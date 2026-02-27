package my.company.projetorotisseriejavafx.DAO;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import my.company.projetorotisseriejavafx.DB.DatabaseConnection;
import my.company.projetorotisseriejavafx.Objects.Bairro;
import my.company.projetorotisseriejavafx.Objects.Cliente;
import my.company.projetorotisseriejavafx.Objects.Pedido;
import my.company.projetorotisseriejavafx.Util.DateUtils;

public class PedidoDAO {

    static public int criar(Pedido pedido) throws SQLException {

        String sql = "INSERT INTO Pedido (id_cliente, id_bairro, nome_cliente, tipo_pagamento, " +
                "tipo_pedido, observacoes, valor_entrega, endereco, valor_itens, valor_total, " +
                "valor_pago, vencimento, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        if (pedido.getTipoPagamento().equalsIgnoreCase("Pagar depois") ||
                pedido.getTipoPagamento().equalsIgnoreCase("A definir")) {
            pedido.setStatus("A PAGAR");
            pedido.setValorPago(0);
        } else {
            pedido.setStatus("PAGO");
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            if (pedido.getCliente() != null) {
                stmt.setInt(1, pedido.getCliente().getId());
            } else {
                stmt.setNull(1, Types.INTEGER);
            }

            if (pedido.getBairro() != null) {
                stmt.setInt(2, pedido.getBairro().getId());
            } else {
                stmt.setNull(2, Types.INTEGER);
            }

            stmt.setString(3, pedido.getNomeCliente());
            stmt.setString(4, pedido.getTipoPagamento());
            stmt.setString(5, pedido.getTipoPedido());
            stmt.setString(6, pedido.getObservacoes());

            if (pedido.getValorEntrega() > 0) {
                stmt.setDouble(7, pedido.getValorEntrega());
            } else {
                stmt.setNull(7, Types.REAL);
            }

            stmt.setString(8, pedido.getEndereco());
            stmt.setDouble(9, pedido.getValorItens());
            stmt.setDouble(10, pedido.getValorTotal());
            stmt.setDouble(11, pedido.getValorPago());

            if (pedido.getVencimento() != null) {
                stmt.setDate(12, Date.valueOf(pedido.getVencimento()));
            } else {
                stmt.setNull(12, Types.DATE);
            }

            stmt.setString(13, pedido.getStatus());

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                throw new SQLException("Falha ao criar pedido, nenhum ID obtido.");
            }
        }
    }

    static public void atualizarStatus(int id, String status) throws SQLException {
        String sql = "UPDATE Pedido SET status = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            stmt.setInt(2, id);

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Pedido com ID " + id + " não encontrado.");
            }
        }
    }

    static public void definirPagamento(int pedido_id, String pagamento) throws SQLException {
        String sql = "UPDATE Pedido \n" +
                "SET status = CASE \n" +
                "    WHEN ? != 'Pagar depois' THEN 'PAGO' \n" +
                "    ELSE status \n" +
                "END,\n" +
                "tipo_pagamento = ?\n" +
                "WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, pagamento);
            stmt.setString(2, pagamento);
            stmt.setInt(3, pedido_id);

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Pedido com ID " + pedido_id + " não encontrado.");
            }
        }
    }

    static public void finalizar(int idPedido) throws SQLException {
        String sql = "UPDATE Pedido SET status = 'PAGO' WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idPedido);

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Pedido com ID " + idPedido + " não encontrado.");
            }
        }
    }

    static public void deletar(int id) throws SQLException {
        String sql = "DELETE FROM Pedido WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Pedido com ID " + id + " não encontrado.");
            }
        }
    }

    static public List<Pedido> listarTodos() throws SQLException {
        String sql = """
                SELECT
                p.* ,
                m.id AS cliente_id,
                m.nome AS cliente_nome,
                m.contato AS cliente_contato,
                b.id AS bairro_id,
                b.nome AS bairro_nome,
                b.valor_entrega AS bairro_valor_entrega
                FROM Pedido p
                LEFT JOIN cliente m ON p.id_cliente = m.id
                LEFT JOIN bairro b ON p.id_bairro = b.id
                ORDER BY date_time DESC;""";

        List<Pedido> pedidos = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                pedidos.add(extrairPedidoDoResultSet(rs));
            }
        }

        return pedidos;
    }

    static public List<Pedido> listarPorCliente(int idCliente) throws SQLException {
        String sql = "SELECT * FROM Pedido WHERE id_cliente = ? ORDER BY date_time DESC";
        List<Pedido> pedidos = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idCliente);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    pedidos.add(extrairPedidoDoResultSet(rs));
                }
            }
        }

        return pedidos;
    }

    static public List<Pedido> listarPorData(LocalDate data) throws SQLException {
        String sql = "SELECT \n" +
                "    p.* ,\n" +
                "    m.id AS cliente_id,\n" +
                "    m.nome AS cliente_nome,\n" +
                "    m.contato AS cliente_contato,\n" +
                "    b.id AS bairro_id,\n" +
                "    b.nome AS bairro_nome,\n" +
                "    b.valor_entrega AS bairro_valor_entrega\n" +
                "FROM Pedido p\n" +
                "LEFT JOIN cliente m ON p.id_cliente = m.id\n" +
                "LEFT JOIN bairro b ON p.id_bairro = b.id\n" +
                "WHERE date(date_time) = ?\n" +
                "ORDER BY date_time DESC;";
        List<Pedido> pedidos = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, data.toString());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    pedidos.add(extrairPedidoDoResultSet(rs));
                }
            }
        }

        return pedidos;
    }

    static public List<Pedido> listarPorPeriodo(String dataInicio, String dataFim) throws SQLException {
        String sql = "SELECT * FROM Pedido WHERE date(date_time) BETWEEN ? AND ? ORDER BY date_time DESC";
        List<Pedido> pedidos = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, dataInicio);
            stmt.setString(2, dataFim);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    pedidos.add(extrairPedidoDoResultSet(rs));
                }
            }
        }

        return pedidos;
    }

    static public Pedido buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM Pedido WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extrairPedidoDoResultSet(rs);
                }
            }
        }

        return null;
    }

    static public double[] calcularDiaria(String data) throws SQLException {
        String sql = "SELECT COUNT(*) AS entregas, SUM(valor_entrega) as valorEntregas " +
                "FROM Pedido WHERE date(date_time) = ? AND tipo_pedido = 'Entrega'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, data);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    double entregas = rs.getInt("entregas");
                    double valorEntregas = rs.getDouble("valorEntregas");
                    return new double[]{entregas, valorEntregas};
                }
            }
        }

        return new double[]{0, 0};
    }

    static public List<Pedido> listarAPagar() throws SQLException {
        String sql = "SELECT * FROM Pedido WHERE status = 'A PAGAR' ORDER BY vencimento ASC";
        List<Pedido> pedidos = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                pedidos.add(extrairPedidoDoResultSet(rs));
            }
        }

        return pedidos;
    }

    static public List<Pedido> listarVencidos() throws SQLException {
        String sql = "SELECT * FROM Pedido WHERE status = 'A PAGAR' AND vencimento < date('now') " +
                "ORDER BY vencimento ASC";
        List<Pedido> pedidos = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                pedidos.add(extrairPedidoDoResultSet(rs));
            }
        }

        return pedidos;
    }

    static public double calcularTotalVendas(String dataInicio, String dataFim) throws SQLException {
        String sql = "SELECT SUM(valor_total) as total FROM Pedido " +
                "WHERE date(date_time) BETWEEN ? AND ? AND status = 'PAGO'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, dataInicio);
            stmt.setString(2, dataFim);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("total");
                }
            }
        }

        return 0.0;
    }

    static private Pedido extrairPedidoDoResultSet(ResultSet rs) throws SQLException {
        Pedido pedido = new Pedido();

        if (rs.getInt("id_cliente") != 0) {
            Cliente cliente = new Cliente();
            cliente.setId(rs.getInt("cliente_id"));
            cliente.setNome(rs.getString("cliente_nome"));
            cliente.setContato(rs.getString("cliente_contato"));
            pedido.setCliente(cliente);
        }

        if (rs.getInt("id_bairro") != 0) {
            Bairro bairro = new Bairro();
            bairro.setId(rs.getInt("bairro_id"));
            bairro.setNome(rs.getString("bairro_nome"));
            bairro.setValorEntrega(rs.getDouble("bairro_valor_entrega"));
            pedido.setBairro(bairro);
        }

        pedido.setId(rs.getInt("id"));
        pedido.setNomeCliente(rs.getString("nome_cliente"));
        pedido.setTipoPagamento(rs.getString("tipo_pagamento"));
        pedido.setTipoPedido(rs.getString("tipo_pedido"));
        pedido.setObservacoes(rs.getString("observacoes"));
        pedido.setValorEntrega(rs.getDouble("valor_entrega"));
        pedido.setEndereco(rs.getString("endereco"));
        pedido.setValorItens(rs.getDouble("valor_itens"));
        pedido.setValorTotal(rs.getDouble("valor_total"));
        pedido.setValorPago(rs.getDouble("valor_pago"));
        pedido.setDateTime(LocalDateTime.parse(rs.getString("date_time"), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        if ((rs.getString("vencimento") != null)) {
            long timestamp = Long.parseLong(rs.getString("vencimento"));
            pedido.setVencimento(DateUtils.timestampToLocalDate(timestamp, "America/Sao_Paulo"));
        }
        pedido.setStatus(rs.getString("status"));

        return pedido;
    }
}
