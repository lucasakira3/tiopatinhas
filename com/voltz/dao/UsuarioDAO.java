package com.voltz.dao;

import com.voltz.factory.ConnectionFactory;
import com.voltz.model.Empresa;
import com.voltz.model.Permissao;
import com.voltz.model.Pessoa;
import com.voltz.model.Usuario;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO da tabela USUARIO. Os SELECTs usam JOIN com PESSOA, EMPRESA e PERMISSAO
 * para devolver o Usuario já completo (id_pessoa e id_empresa são opcionais: LEFT JOIN).
 */
public class UsuarioDAO implements CrudDAO<Usuario> {

    private static final String SELECT_COMPLETO =
            "SELECT u.id_usuario, u.login, u.senha, u.status, "
          + "       p.id_pessoa, p.nome AS pessoa_nome, p.cpf, p.email, p.data_nascimento, "
          + "       e.id_empresa, e.razao_social, e.cnpj, e.email_contato, "
          + "       pm.id_permissao, pm.nome AS permissao_nome, pm.descricao "
          + "FROM usuario u "
          + "LEFT JOIN pessoa p ON u.id_pessoa = p.id_pessoa "
          + "LEFT JOIN empresa e ON u.id_empresa = e.id_empresa "
          + "INNER JOIN permissao pm ON u.id_permissao = pm.id_permissao ";

    @Override
    public Usuario inserir(Usuario u) throws SQLException {
        try (Connection c = ConnectionFactory.getConnection()) {
            return inserir(c, u);
        }
    }

    /** INSERT dentro de uma conexão/transação já aberta. O id é gerado pelo banco (IDENTITY). */
    public Usuario inserir(Connection c, Usuario u) throws SQLException {
        String sql = "INSERT INTO usuario (login, senha, id_pessoa, id_empresa, id_permissao, status) VALUES (?, ?, ?, ?, ?, ?)";
        // Informar a coluna de PK faz o Oracle devolver o id gerado (e não o ROWID).
        try (PreparedStatement stmt = c.prepareStatement(sql, new String[]{"ID_USUARIO"})) {
            stmt.setString(1, u.getLogin());
            stmt.setString(2, u.getSenha());
            setFks(stmt, 3, u);
            stmt.setString(6, u.getStatus());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) u.setIdUsuario(rs.getInt(1));
            }
        }
        return u;
    }

    @Override
    public boolean atualizar(Usuario u) throws SQLException {
        String sql = "UPDATE usuario SET login = ?, senha = ?, id_pessoa = ?, id_empresa = ?, "
                   + "id_permissao = ?, status = ? WHERE id_usuario = ?";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement stmt = c.prepareStatement(sql)) {
            stmt.setString(1, u.getLogin());
            stmt.setString(2, u.getSenha());
            // setFks ocupa 3 posições (pessoa, empresa, permissão) a partir do índice 3
            setFks(stmt, 3, u);
            stmt.setString(6, u.getStatus());
            stmt.setInt(7, u.getIdUsuario());
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean excluir(int id) throws SQLException {
        String sql = "DELETE FROM usuario WHERE id_usuario = ?";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement stmt = c.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public Usuario buscarPorId(int id) throws SQLException {
        String sql = SELECT_COMPLETO + "WHERE u.id_usuario = ?";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement stmt = c.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }

    /** SELECT com JOIN filtrando pelo login (UNIQUE). */
    public Usuario buscarPorLogin(String login) throws SQLException {
        String sql = SELECT_COMPLETO + "WHERE u.login = ?";
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement stmt = c.prepareStatement(sql)) {
            stmt.setString(1, login);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }

    @Override
    public List<Usuario> listarTodos() throws SQLException {
        String sql = SELECT_COMPLETO + "ORDER BY u.id_usuario";
        List<Usuario> lista = new ArrayList<>();
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement stmt = c.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    /** Preenche 3 parâmetros consecutivos: id_pessoa, id_empresa, id_permissao. */
    private void setFks(PreparedStatement stmt, int indiceInicial, Usuario u) throws SQLException {
        if (u.temPessoa()) stmt.setInt(indiceInicial, u.getIdPessoa());
        else stmt.setNull(indiceInicial, Types.NUMERIC);

        if (u.getEmpresa() != null && u.getEmpresa().getIdEmpresa() > 0) {
            stmt.setInt(indiceInicial + 1, u.getEmpresa().getIdEmpresa());
        } else {
            stmt.setNull(indiceInicial + 1, Types.NUMERIC);
        }

        if (u.getPermissao() == null) {
            throw new SQLException("Usuário exige uma Permissão (id_permissao é NOT NULL).");
        }
        stmt.setInt(indiceInicial + 2, u.getPermissao().getIdPermissao());
    }

    private Usuario mapear(ResultSet rs) throws SQLException {
        Pessoa pessoa = null;
        int idPessoa = rs.getInt("id_pessoa");
        if (!rs.wasNull()) {
            Date nasc = rs.getDate("data_nascimento");
            pessoa = new Pessoa(idPessoa, rs.getString("pessoa_nome"), rs.getString("cpf"),
                    rs.getString("email"), nasc != null ? nasc.toLocalDate() : null);
        }

        Empresa empresa = null;
        int idEmpresa = rs.getInt("id_empresa");
        if (!rs.wasNull()) {
            empresa = new Empresa(idEmpresa, rs.getString("razao_social"),
                    rs.getString("cnpj"), rs.getString("email_contato"));
        }

        Permissao permissao = new Permissao(rs.getInt("id_permissao"),
                rs.getString("permissao_nome"), rs.getString("descricao"));

        return new Usuario(rs.getInt("id_usuario"), rs.getString("login"), rs.getString("senha"),
                rs.getString("status"), permissao, empresa, pessoa);
    }
}
