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

  private static ConnectionPool of(DataSource dataSource, Queue<PooledConnection> connectionQueue) {
    return new ConnectionPool(dataSource, connectionQueue);
  }

  public static ConnectionPool withDataSource(DataSource dataSource) {
    return of(dataSource, new LinkedBlockingQueue<>());
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
