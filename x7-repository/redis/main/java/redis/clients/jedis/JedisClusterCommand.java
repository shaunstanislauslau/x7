package redis.clients.jedis;

import redis.clients.jedis.exceptions.*;
import redis.clients.util.JedisClusterCRC16;

public abstract class JedisClusterCommand<T> {

  private JedisClusterConnectionHandler connectionHandler;
  private int redirections;
  private ThreadLocal<Jedis> askConnection = new ThreadLocal<Jedis>();

  public JedisClusterCommand(JedisClusterConnectionHandler connectionHandler, int maxRedirections) {
    this.connectionHandler = connectionHandler;
    this.redirections = maxRedirections;
  }

  public abstract T execute(Jedis connection);

  public T run(String key) {
    if (key == null) {
      throw new JedisClusterException("No way to dispatch this command to Redis Cluster.");
    }

    return runWithRetries(key, this.redirections, false, false);
  }

  private T runWithRetries(String key, int redirections, boolean tryRandomNode, boolean asking) {
    if (redirections <= 0) {
      throw new JedisClusterMaxRedirectionsException("Too many Cluster redirections?");
    }

    Jedis connection = null;
    try {

      if (asking) {
        // TODO: Pipeline asking with the original command to make it
        // faster....
        connection = askConnection.get();
        connection.asking();

        // if asking success, reset asking flag
        asking = false;
      } else {
        if (tryRandomNode) {
          connection = connectionHandler.getConnection();
        } else {
          connection = connectionHandler.getConnectionFromSlot(JedisClusterCRC16.getSlot(key));
        }
      }

      return execute(connection);
    } catch (JedisConnectionException jce) {
      if (tryRandomNode) {
        // maybe all connection is down
        throw jce;
      }

      // release current connection before recursion
      releaseConnection(connection);
      connection = null;

      // retry with random connection
      return runWithRetries(key, redirections - 1, true, asking);
    } catch (JedisRedirectionException jre) {
      // release current connection before recursion or renewing
      releaseConnection(connection);
      connection = null;

      if (jre instanceof JedisAskDataException) {
        asking = true;
        askConnection.set(this.connectionHandler.getConnectionFromNode(jre.getTargetNode()));
      } else if (jre instanceof JedisMovedDataException) {
        // it rebuilds cluster's slot cache
        // recommended by Redis cluster specification
        this.connectionHandler.renewSlotCache();
      } else {
        throw new JedisClusterException(jre);
      }

      return runWithRetries(key, redirections - 1, false, asking);
    } finally {
      releaseConnection(connection);
    }

  }

  private void releaseConnection(Jedis connection) {
    if (connection != null) {
      connection.close();
    }
  }

}
