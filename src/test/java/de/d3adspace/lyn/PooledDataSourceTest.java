package de.d3adspace.lyn;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PooledDataSourceTest {
  @Mock private DataSource dataSource;

  @Test
  void testGetConnectionFromPoolPassThrough() throws SQLException {
    // given
    var connection = mock(Connection.class);

    // when
    when(connection.getCatalog()).thenReturn("test");
    when(dataSource.getConnection()).thenReturn(connection);

    // then
    var pooledDataSource = PooledDataSource.withDataSource(dataSource);
    var poolConnection = pooledDataSource.getConnection();

    assertEquals("test", connection.getCatalog());
  }
}
