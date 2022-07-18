package de.d3adspace.lyn;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;
import javax.sql.DataSource;

public final class PooledDataSource implements DataSource {
  private final DataSource dataSource;
  private final ConnectionPool connectionPool;

  private PooledDataSource(DataSource dataSource, ConnectionPool connectionPool) {
    this.dataSource = dataSource;
    this.connectionPool = connectionPool;
  }

  public static PooledDataSource withDataSource(DataSource dataSource) {
    var pool = ConnectionPool.withDataSource(dataSource);
    return new PooledDataSource(dataSource, pool);
  }

  @Override
  public Connection getConnection() throws SQLException {
    return connectionPool.getConnection();
  }

  @Override
  public Connection getConnection(String username, String password) throws SQLException {
    return connectionPool.getConnection();
  }

  @Override
  public PrintWriter getLogWriter() throws SQLException {
    return dataSource.getLogWriter();
  }

  @Override
  public void setLogWriter(PrintWriter out) throws SQLException {
    dataSource.setLogWriter(out);
  }

  @Override
  public void setLoginTimeout(int seconds) throws SQLException {
    dataSource.setLoginTimeout(seconds);
  }

  @Override
  public int getLoginTimeout() throws SQLException {
    return dataSource.getLoginTimeout();
  }

  @Override
  public Logger getParentLogger() throws SQLFeatureNotSupportedException {
    return dataSource.getParentLogger();
  }

  @Override
  public <T> T unwrap(Class<T> iface) throws SQLException {
    return dataSource.unwrap(iface);
  }

  @Override
  public boolean isWrapperFor(Class<?> iface) throws SQLException {
    return dataSource.isWrapperFor(iface);
  }
}
