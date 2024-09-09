package ssh.demo.service;

import java.io.File;
import java.io.IOException;
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
	private SshClient sshClient;
	private SshdSessionFactory sshdSessionFactory;
	
	@SuppressWarnings("unused")
	public SshService() {
		
		// Configure the SshClient with default client identity
        this.sshClient = SshClient.setUpDefaultClient();
        this.sshClient.setClientIdentityLoader(ClientIdentityLoader.DEFAULT);
        this.sshClient.start();

        // Configure the SshdSessionFactory with the SshClient
        this.sshdSessionFactory = new SshdSessionFactory() {
            protected void configureSession(ClientSession session) {}
        };

        // Ensure the session factory is not null before using it
        if (this.sshdSessionFactory == null) {
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
	}
	
	@SuppressWarnings("unused")
	public SshService(String privKeyPath) {
		
		// Configure the SshClient with default client identity
		this.sshClient = SshClient.setUpDefaultClient();
		this.sshClient.setClientIdentityLoader(ClientIdentityLoader.DEFAULT);
        this.sshClient.start();

        // Configure the SshdSessionFactory with the SshClient and location of private key
        File privKeyDir = Paths.get(privKeyPath).toFile();
        this.sshdSessionFactory = new SshdSessionFactoryBuilder()
                .setPreferredAuthentications("publickey")
                .setHomeDirectory(FS.DETECTED.userHome())
                .setSshDirectory(privKeyDir)
                .build(null);
        
        // Ensure the session factory is not null before using it
        if (this.sshdSessionFactory == null) {
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
	}
	
	// Returns necessary information for Git commands that require SSH verification
	public TransportConfigCallback getTransportConfigCallback() {
		return transportConfigCallback;
	}
	
	// Helper method to stop SSH client when necessary
	public void stopService() throws IOException {
		if (this.sshClient.isStarted()) {
			this.sshClient.stop();
			this.sshdSessionFactory.close();
			System.out.println("SSH Service is stopped.");
		} else {
			System.out.println("SSH Service was not started");
		}
	}
	
}