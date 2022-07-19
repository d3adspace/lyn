package de.d3adspace.lyn;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ConnectionPoolTest {
  @Mock
  private DataSource dataSource;

  @Test
  void testGetConnectionSingle() throws SQLException {
    // given
    var connectionPool = ConnectionPool.of(dataSource, 10, 0);
    var connection = mock(Connection.class);

    // when
    when(dataSource.getConnection()).thenReturn(connection);

    // then
    var poolConnection = connectionPool.getConnection();
    assertEquals(connection, poolConnection.getConnection());
  }

  @Test
  void testGetConnectionSingleReturned() throws SQLException {
    // given
    var connectionPool = ConnectionPool.of(dataSource, 10, 0);
    var connection = mock(Connection.class);

    // when
    when(dataSource.getConnection()).thenReturn(connection);

    // then
    var poolConnection = connectionPool.getConnection();
    poolConnection.close();
    poolConnection = connectionPool.getConnection();
    assertEquals(connection, poolConnection.getConnection());
  }

  @Test
  void testGetConnectionMultipleOverMaxCapacity() throws SQLException {
    // given
    var connectionPool = ConnectionPool.of(dataSource, 1, 0);
    var connection = mock(Connection.class);

    // when
    when(dataSource.getConnection()).thenReturn(connection);

    // then
    var poolConnection = connectionPool.getConnection();
    var poolConnection2 = connectionPool.getConnection();
    assertEquals(connection, poolConnection.getConnection());
    assertEquals(connection, poolConnection2.getConnection());
  }

  @Test
  void testGetConnectionClosedIfCapacityReached() throws SQLException {
    // given
    var connectionPool = ConnectionPool.of(dataSource, 1, 0);
    var connection = mock(Connection.class);
    var connection2 = mock(Connection.class);

    // when
    when(dataSource.getConnection()).thenReturn(connection, connection2);

    // then
    var poolConnection = connectionPool.getConnection();
    var poolConnection2 = connectionPool.getConnection();

    poolConnection.close();
    poolConnection2.close();

    verify(connection2).close();
  }

  @Test
  void testGetConnectionWithInitialConnections() throws SQLException {
    // given
    var connection = mock(Connection.class);

    // when
    when(dataSource.getConnection()).thenReturn(connection);

    // then
    var connectionPool = ConnectionPool.of(dataSource, 1, 1);

    verifyNoMoreInteractions(dataSource);

    var poolConnection = connectionPool.getConnection();
    assertEquals(connection, poolConnection.getConnection());
  }

  @Test
  void testConnectionWithSpecificInitialConnectionAmount() throws SQLException {
    // given
    var connection = mock(Connection.class);

    // when
    when(dataSource.getConnection()).thenReturn(connection);

    // then
    var connectionPool = ConnectionPool.of(dataSource, 10, 10);

    verify(dataSource, times(10)).getConnection();
  }

  @Test
  void testHasInitialConnections() throws SQLException {
    // given
    var connection = mock(Connection.class);

    // when
    when(dataSource.getConnection()).thenReturn(connection);

    // then
    var connectionPool = ConnectionPool.of(dataSource);

    verify(dataSource, atLeastOnce()).getConnection();
  }
}
