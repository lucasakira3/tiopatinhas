package com.voltz.dao;

import java.sql.SQLException;
import java.util.List;

/** Contrato comum de todos os DAOs: INSERT, UPDATE, DELETE e SELECT. */
public interface CrudDAO<T> {

    /** INSERT — grava o objeto e devolve-o com o id preenchido. */
    T inserir(T objeto) throws SQLException;

    /** UPDATE — retorna true se alguma linha foi alterada. */
    boolean atualizar(T objeto) throws SQLException;

    /** DELETE — retorna true se alguma linha foi removida. */
    boolean excluir(int id) throws SQLException;

    /** SELECT por chave primária — retorna null se não existir. */
    T buscarPorId(int id) throws SQLException;

    /** SELECT de todos os registros. */
    List<T> listarTodos() throws SQLException;
}
