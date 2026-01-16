package my.company.projetorotisseriejavafx.DB;

import java.io.File;
import java.sql.*;

public class DatabaseConnection {

    private static final String DB_NAME = "rotisserie.db";
    private static Connection connection;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            String url = "jdbc:sqlite:" + getDatabasePath();
            connection = DriverManager.getConnection(url);

            try (Statement stmt = connection.createStatement()) {
                stmt.execute("PRAGMA foreign_keys = ON");
            }
        }
        return connection;
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Conexão fechada com sucesso!");
            }
        } catch (SQLException e) {
            System.err.println("Erro ao fechar conexão: " + e.getMessage());
        }
    }

    private static String getDatabasePath() {
        String appName = "ProjetoRotisserieJavaFX";

        String appData = System.getenv("APPDATA");

        // Fallback caso não exista (Linux / Mac)
        if (appData == null) {
            appData = System.getProperty("user.home");
        }

        File appDir = new File(appData, appName);

        if (!appDir.exists()) {
            appDir.mkdirs(); // cria pasta se não existir
        }

        return new File(appDir, DB_NAME).getAbsolutePath();
    }

    public static void initializeDatabase() {

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            // ==================== CRIAR TABELAS ====================

            // Tabela Marmita
            stmt.execute("""
                        CREATE TABLE IF NOT EXISTS Marmita (
                            id INTEGER PRIMARY KEY AUTOINCREMENT,
                            nome TEXT NOT NULL,
                            max_mistura INTEGER NOT NULL,
                            max_guarnicao INTEGER NOT NULL,
                            valor REAL NOT NULL,
                            status TEXT NOT NULL DEFAULT 'ATIVO',
                            UNIQUE (nome)
                        )
                    """);

            stmt.execute("""
                        INSERT OR IGNORE INTO Marmita (nome, max_mistura, max_guarnicao, valor)
                        VALUES ('Peso', 0, 0, 0)
                    """);

            // Tabela Produto
            stmt.execute("""
                        CREATE TABLE IF NOT EXISTS Produto (
                            id INTEGER PRIMARY KEY AUTOINCREMENT,
                            nome TEXT NOT NULL,
                            valor REAL NOT NULL,
                            status TEXT NOT NULL DEFAULT 'ATIVO',
                            UNIQUE (nome)
                        )
                    """);

            // Tabela Bairro
            stmt.execute("""
                        CREATE TABLE IF NOT EXISTS Bairro (
                            id INTEGER PRIMARY KEY AUTOINCREMENT,
                            nome TEXT NOT NULL,
                            valor_entrega REAL NOT NULL,
                            status TEXT NOT NULL DEFAULT 'ATIVO',
                            UNIQUE (nome)
                        )
                    """);

            // Tabela Motoboy
            stmt.execute("""
                        CREATE TABLE IF NOT EXISTS Motoboy (
                            id INTEGER PRIMARY KEY AUTOINCREMENT,
                            nome TEXT NOT NULL,
                            valor_diaria REAL NOT NULL,
                            status TEXT NOT NULL DEFAULT 'ATIVO',
                            UNIQUE (nome)
                        )
                    """);

            // Tabela Cliente
            stmt.execute("""
                        CREATE TABLE IF NOT EXISTS Cliente (
                            id INTEGER PRIMARY KEY AUTOINCREMENT,
                            nome TEXT NOT NULL,
                            contato TEXT NOT NULL,
                            bairro_id INTEGER,
                            endereco TEXT,
                            conta REAL NOT NULL DEFAULT 0,
                            status TEXT NOT NULL DEFAULT 'ATIVO',
                            UNIQUE (nome)
                        )
                    """);

            // Tabela Pedido
            stmt.execute("""
                        CREATE TABLE IF NOT EXISTS Pedido (
                            id INTEGER PRIMARY KEY AUTOINCREMENT,
                            id_cliente INTEGER,
                            id_bairro INTEGER,
                            nome_cliente TEXT,
                            tipo_pagamento TEXT NOT NULL,
                            tipo_pedido TEXT NOT NULL,
                            observacoes TEXT,
                            valor_entrega REAL,
                            endereco TEXT,
                            valor_itens REAL NOT NULL,
                            valor_total REAL NOT NULL,
                            valor_pago REAL NOT NULL DEFAULT 0,
                            date_time TEXT NOT NULL DEFAULT (datetime('now', 'localtime')),
                            vencimento TEXT,
                            status TEXT NOT NULL DEFAULT 'PAGO',
                            FOREIGN KEY (id_cliente) REFERENCES Cliente (id),
                            FOREIGN KEY (id_bairro) REFERENCES Bairro (id)
                        )
                    """);

            // Tabela Marmita_Vendida
            stmt.execute("""
                        CREATE TABLE IF NOT EXISTS Marmita_Vendida (
                            id INTEGER PRIMARY KEY AUTOINCREMENT,
                            id_pedido INTEGER NOT NULL,
                            id_marmita INTEGER NOT NULL,
                            detalhes TEXT NOT NULL,
                            valor_peso REAL,
                            subtotal REAL NOT NULL,
                            observacao TEXT,
                            FOREIGN KEY (id_pedido) REFERENCES Pedido (id),
                            FOREIGN KEY (id_marmita) REFERENCES Marmita (id)
                        )
                    """);

            // Tabela Produto_Vendido
            stmt.execute("""
                        CREATE TABLE IF NOT EXISTS Produto_Vendido (
                            id INTEGER PRIMARY KEY AUTOINCREMENT,
                            id_pedido INTEGER NOT NULL,
                            id_produto INTEGER NOT NULL,
                            quantidade INTEGER NOT NULL,
                            subtotal REAL NOT NULL,
                            FOREIGN KEY (id_pedido) REFERENCES Pedido (id),
                            FOREIGN KEY (id_produto) REFERENCES Produto (id)
                        )
                    """);

            // Tabela Desconto_Adicional
            stmt.execute("""
                        CREATE TABLE IF NOT EXISTS Desconto_Adicional (
                            id INTEGER PRIMARY KEY AUTOINCREMENT,
                            id_pedido INTEGER NOT NULL,
                            tipo TEXT NOT NULL,
                            valor REAL NOT NULL,
                            observacao TEXT,
                            FOREIGN KEY (id_pedido) REFERENCES Pedido (id)
                        )
                    """);

            // Tabela Item_Cardapio
            stmt.execute("""
                        CREATE TABLE IF NOT EXISTS Item_Cardapio (
                            id INTEGER PRIMARY KEY AUTOINCREMENT,
                            nome TEXT NOT NULL,
                            categoria TEXT NOT NULL,
                            UNIQUE (nome, categoria)
                        )
                    """);

            // Tabela Cardapio
            stmt.execute("""
                        CREATE TABLE IF NOT EXISTS Cardapio (
                            id INTEGER PRIMARY KEY AUTOINCREMENT,
                            principal_1 TEXT,
                            principal_2 TEXT,
                            principal_3 TEXT,
                            mistura_1 TEXT,
                            mistura_2 TEXT,
                            mistura_3 TEXT,
                            mistura_4 TEXT,
                            guarnicao_1 TEXT,
                            guarnicao_2 TEXT,
                            guarnicao_3 TEXT,
                            guarnicao_4 TEXT,
                            salada_1 TEXT,
                            salada_2 TEXT,
                            data_hora TEXT
                        )
                    """);

            // Tabela Pagamento
            stmt.execute("""
                        CREATE TABLE IF NOT EXISTS Pagamento (
                            id INTEGER PRIMARY KEY AUTOINCREMENT,
                            id_pedido INTEGER NOT NULL,
                            tipo_pagamento TEXT NOT NULL,
                            valor REAL NOT NULL,
                            observacao TEXT,
                            data TEXT NOT NULL DEFAULT (date('now', 'localtime')),
                            FOREIGN KEY (id_pedido) REFERENCES Pedido (id)
                        )
                    """);

            // ==================== CRIAR TRIGGERS ====================

            // Trigger: Soma conta do cliente ao criar pedido
            stmt.execute("""
                        CREATE TRIGGER IF NOT EXISTS SOMA_CONTA_CLIENTE
                        AFTER INSERT ON Pedido
                        FOR EACH ROW
                        WHEN NEW.id_cliente IS NOT NULL AND NEW.status = 'A PAGAR'
                        BEGIN
                            UPDATE Cliente
                            SET conta = conta + NEW.valor_total
                            WHERE id = NEW.id_cliente;
                        END
                    """);

            // Trigger: Subtrai conta do cliente ao pagar pedido
            stmt.execute("""
                        CREATE TRIGGER IF NOT EXISTS SUBTRAI_CONTA_CLIENTE
                        AFTER UPDATE ON Pedido
                        FOR EACH ROW
                        WHEN OLD.status != 'PAGO' AND NEW.status = 'PAGO'
                        BEGIN
                            UPDATE Cliente
                            SET conta = conta - NEW.valor_total
                            WHERE id = NEW.id_cliente;
                        END
                    """);

            // Trigger: Adiciona valor ao valor_pago ao criar pagamento
            stmt.execute("""
                        CREATE TRIGGER IF NOT EXISTS ADICIONA_PAGAMENTO
                        AFTER INSERT ON Pagamento
                        FOR EACH ROW
                        BEGIN
                            UPDATE Pedido
                            SET valor_pago = valor_pago + NEW.valor
                            WHERE id = NEW.id_pedido;
                        END
                    """);

            // Trigger: Remove valor do valor_pago ao deletar pagamento
            stmt.execute("""
                        CREATE TRIGGER IF NOT EXISTS REMOVE_PAGAMENTO
                        AFTER DELETE ON Pagamento
                        FOR EACH ROW
                        BEGIN
                            UPDATE Pedido
                            SET valor_pago = valor_pago - OLD.valor
                            WHERE id = OLD.id_pedido;
                        END
                    """);

        } catch (SQLException e) {
            System.err.println("Erro ao inicializar banco de dados:");
            e.printStackTrace();
        }
    }

    public static boolean databaseExists() {
        File dbFile = new File(getDatabasePath());
        return dbFile.exists();
    }

    public static void showDatabaseInfo() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            // Listar todas as tabelas
            ResultSet rs = stmt.executeQuery(
                    "SELECT name FROM sqlite_master WHERE type='table' ORDER BY name"
            );

            System.out.println("\n=== TABELAS NO BANCO ===");
            while (rs.next()) {
                String tableName = rs.getString("name");
                System.out.println("- " + tableName);

                // Contar registros em cada tabela
                ResultSet count = stmt.executeQuery(
                        "SELECT COUNT(*) as total FROM " + tableName
                );
                if (count.next()) {
                    System.out.println("  Registros: " + count.getInt("total"));
                }
            }

        } catch (SQLException e) {
            System.err.println("Erro ao exibir informações: " + e.getMessage());
        }
    }
}
