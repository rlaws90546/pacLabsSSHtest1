package ssh.demo.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.lang.IllegalStateException;

import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.config.keys.ClientIdentityLoader;
import org.apache.sshd.client.session.ClientSession;
import org.eclipse.jgit.api.TransportConfigCallback;
import org.eclipse.jgit.transport.SshTransport;
import org.eclipse.jgit.transport.Transport;
import org.eclipse.jgit.transport.sshd.SshdSessionFactory;
import org.eclipse.jgit.transport.sshd.SshdSessionFactoryBuilder;
import org.eclipse.jgit.util.FS;

public class SshService {
	
	private TransportConfigCallback transportConfigCallback;
	
	@SuppressWarnings("unused")
	public SshService() {
		
		// Configure the SshClient with custom identity (private key)
        SshClient sshClient = SshClient.setUpDefaultClient();
        sshClient.setClientIdentityLoader(ClientIdentityLoader.DEFAULT);
        sshClient.start();

        // Configure the SshdSessionFactory with the SshClient
        SshdSessionFactory sshdSessionFactory = new SshdSessionFactory() {
            protected void configureSession(ClientSession session) {
                // Custom configuration if needed, such as setting timeouts
            }
        };

        /* Optionally, load a specific identity (private key) if needed
        sshdSessionFactory.setSshClient(sshClient);
        sshdSessionFactory.setClientIdentityLoader(ClientIdentityLoader.DEFAULT);
        sshdSessionFactory.setKeyIdentityProvider(KeyIdentityProvider.wrapIdentities(
                Paths.get("/path/to/private/key")));  // Replace with your private key path
		*/

        // Ensure the session factory is not null before using it
        if (sshdSessionFactory == null) {
            throw new IllegalStateException("SSH session factory is null.");
        }

        // Configure the transport to use the custom SshdSessionFactory
        this.transportConfigCallback = new TransportConfigCallback() {
            @Override
            public void configure(Transport transport) {
                if (transport instanceof SshTransport) {
                    ((SshTransport) transport).setSshSessionFactory(sshdSessionFactory);
                }
            }
        };
        
		/* 
		 * Brother's code below from SSHservice.java in jackalope

		File sshDir = new File(FS.DETECTED.userHome(), "/.ssh");
		sshSessionFactory = new SshdSessionFactoryBuilder()
		        .setPreferredAuthentications("publickey")
		        .setHomeDirectory(FS.DETECTED.userHome())
		        .setSshDirectory(sshDir)
		        .build(null);
		*/
	}
	
	// Returns necessary information for Git commands that require SSH verification
	public TransportConfigCallback getTransportConfigCallback() {
		return transportConfigCallback;
	}
	
	/* Might use helper method to check that SSH service is not null
	public boolean sshReady() {
		if (transportConfigCallback != null) {
			return true;
		} else {
			return false;
		}
	}
	*/
}
