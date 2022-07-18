package de.d3adspace.lyn;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import javax.sql.DataSource;

public final class ConnectionPool {
  private final DataSource dataSource;
  private final Queue<PooledConnection> connectionQueue;

  private ConnectionPool(DataSource dataSource, Queue<PooledConnection> connectionQueue) {
    this.dataSource = dataSource;
    this.connectionQueue = connectionQueue;
  }

  private static final int DEFAULT_MAX_CONNECTIONS = 50;

  public static ConnectionPool of(DataSource dataSource) throws SQLException {
    return of(dataSource, DEFAULT_MAX_CONNECTIONS);
  }

  private static final int DEFAULT_INITIAL_CONNECTIONS = 10;

  public static ConnectionPool of(DataSource dataSource, int maxConnections) throws SQLException {
    return of(dataSource, maxConnections, DEFAULT_INITIAL_CONNECTIONS);
  }

  public static ConnectionPool of(DataSource dataSource, int maxConnections, int initialConnections)
      throws SQLException {
    var connectionPool = new ConnectionPool(dataSource, new LinkedBlockingQueue<>(maxConnections));
    connectionPool.fill(initialConnections);
    return connectionPool;
  }

  private void fill(int initialConnections) throws SQLException {
    for (int i = 0; i < initialConnections; i++) {
      var originalConnection = dataSource.getConnection();
      var connection = new PooledConnection(this, originalConnection);
      connectionQueue.add(connection);
    }
  }

  public Connection getConnection() throws SQLException {
    PooledConnection connection = connectionQueue.poll();
    if (connection == null) {
      var newConnection = dataSource.getConnection();
      connection = new PooledConnection(this, newConnection);
      connectionQueue.add(connection);
      return connection;
    }
    return connection;
  }

  void returnConnection(PooledConnection pooledConnection) {
    connectionQueue.add(pooledConnection);
  }
}
