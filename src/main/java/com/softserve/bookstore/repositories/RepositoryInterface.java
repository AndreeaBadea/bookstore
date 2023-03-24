package com.softserve.bookstore.repositories;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public interface RepositoryInterface<T> {

    List<T> findAllFromFile(String fileName) throws IOException;

    List<T> findAll() throws SQLException;

    void add(List<T> list) throws SQLException;




}
