package api.base.setup;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import java.io.ByteArrayOutputStream;

/**
 * Initiates Remote SSH Connection.
 * For Test Server, Windows Machine, etc.,
 */
public class RemoteSshConnection {

  /**
   * JSCh Connection (SSL).
   *
   * @param serverIpAddr serverip
   * @param serverRootUser rootuser
   * @param serverRootPass rootpassword
   * @param cmd command to execute
   * @return response
   */
  public String jschConnection(String serverIpAddr, String serverRootUser,
      String serverRootPass, String cmd) {

    Session session = null;
    ChannelExec channel = null;
    String resp = null;
    try {
      session = new JSch().getSession(serverRootUser, serverIpAddr, 22);
      session.setPassword(serverRootPass);
      session.setConfig("StrictHostKeyChecking", "no");
      session.connect();
      channel = (ChannelExec) session.openChannel("exec");
      channel.setCommand(cmd);
      ByteArrayOutputStream resStream = new ByteArrayOutputStream();
      channel.setOutputStream(resStream);
      try {
        channel.connect();
        // log.info("SSH connection channel is established & Active Thread Count >> "+Thread.activeCount());
      } catch (JSchException e) {
        e.printStackTrace();
      }
      while (channel.isConnected()) {
        try {
          Thread.sleep(100);
        } catch (InterruptedException e) {

          e.printStackTrace();
        }
      }
      resp = new String(resStream.toByteArray());
    } catch (JSchException e) {

      e.printStackTrace();
    }
    if (session != null) {
      session.disconnect();

    }
    if (channel != null) {
      channel.disconnect();
    }
    return resp;
  }

}
