package ssh.demo.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.function.Function;
import java.lang.IllegalStateException;

import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.config.keys.ClientIdentityLoader;
import org.apache.sshd.client.session.ClientSession;
import org.eclipse.jgit.api.TransportConfigCallback;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.SshTransport;
import org.eclipse.jgit.transport.Transport;
import org.eclipse.jgit.transport.sshd.SshdSessionFactory;
import org.eclipse.jgit.transport.sshd.SshdSessionFactoryBuilder;
import org.eclipse.jgit.util.FS;

public class SshService {
	
	private TransportConfigCallback transportConfigCallback;
	private SshClient sshClient;
	private SshdSessionFactory sshdSessionFactory = null;
	private File privateKeyFile;
	private File configFile;
	
	@SuppressWarnings("unused")
	public SshService() {
		
		// Configure the SshClient with default client identity
        this.sshClient = SshClient.setUpDefaultClient();
        this.sshClient.setClientIdentityLoader(ClientIdentityLoader.DEFAULT);
        this.sshClient.start();

        // Configure the SshdSessionFactory with the SshClient
        //this.sshdSessionFactory = new SshdSessionFactory() {
            //protected void configureSession(ClientSession session) {}
        //};

        File defaultSshDir = new File(FS.DETECTED.userHome(), "/.ssh");
        this.sshdSessionFactory = new SshdSessionFactoryBuilder()
                .setPreferredAuthentications("publickey")
                .setHomeDirectory(FS.DETECTED.userHome())
                .setSshDirectory(defaultSshDir)
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
	
	@SuppressWarnings("unused")
	public SshService(String privKeyPath) {
		//String configPath = privKeyPath + "/config";
		//File configFilePath = Paths.get(configPath).toFile();
		
		// Configure the SshClient with default client identity
		this.sshClient = SshClient.setUpDefaultClient();
		this.sshClient.setClientIdentityLoader(ClientIdentityLoader.DEFAULT);
        this.sshClient.start();

        // Configure the SshdSessionFactory with the SshClient and location of private key
        File sshDir = new File(FS.DETECTED.userHome(), privKeyPath);
        this.sshdSessionFactory = new SshdSessionFactoryBuilder()
                .setPreferredAuthentications("publickey")
                .setHomeDirectory(FS.DETECTED.userHome())
                .setSshDirectory(sshDir)
                .build(null);
        
        
        //this.sshdSessionFactory.setSshDirectory(privKeyDir);
        
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
	public SshService(String privKeyPath, String privateKey) throws IOException{
		
		System.out.println("Taking key as param for SSH service");
		
	    // Set up SSH client
	    this.sshClient = SshClient.setUpDefaultClient();
	    this.sshClient.setClientIdentityLoader(ClientIdentityLoader.DEFAULT);
	    this.sshClient.start();
	    
	    File sshDir = new File(FS.DETECTED.userHome(), privKeyPath);
	    
	    // Create a temporary file to store the private key
	 	this.privateKeyFile = new File(sshDir, "id_rsa");
	 	
	 	// Write the private key to the temporary file
	    java.nio.file.Files.writeString(this.privateKeyFile.toPath(), privateKey);
	    
	    createConfigFile(sshDir);

	 	// Create SSH session factory
        this.sshdSessionFactory = new SshdSessionFactoryBuilder()
                .setPreferredAuthentications("publickey")
                .setHomeDirectory(FS.DETECTED.userHome())
                .setSshDirectory(sshDir)
                .build(null);

	    // Ensure the session factory is not null before using it
	    if (this.sshdSessionFactory == null) {
	    	throw new IllegalStateException("SSH session factory is null.");
	    }

	    this.transportConfigCallback = new TransportConfigCallback() {
            @Override
            public void configure(Transport transport) {
                if (transport instanceof SshTransport) {
                    ((SshTransport) transport).setSshSessionFactory(sshdSessionFactory);
                }
            }
        };
	}
	
	/*
	@SuppressWarnings("unused")
	public SshService(String privateKey, boolean bool) throws IOException{
		
	    // Set up SSH client
	    this.sshClient = SshClient.setUpDefaultClient();
	    this.sshClient.setClientIdentityLoader(ClientIdentityLoader.DEFAULT);
	    this.sshClient.start();
	    
	    // NEED TO --> SshdSessionFactoryBuilder.setDefaultKeysProvider(Function<File, Iterable<KeyPair>>);
        this.sshdSessionFactory = new SshdSessionFactoryBuilder()
                .setPreferredAuthentications("publickey")
                .setHomeDirectory(FS.DETECTED.userHome())
                .build(null);

	    // Ensure the session factory is not null before using it
	    if (this.sshdSessionFactory == null) {
	    	throw new IllegalStateException("SSH session factory is null.");
	    }

	    this.transportConfigCallback = new TransportConfigCallback() {
            @Override
            public void configure(Transport transport) {
                if (transport instanceof SshTransport) {
                    ((SshTransport) transport).setSshSessionFactory(sshdSessionFactory);
                }
            }
        };
	} */
	
	// Returns necessary information for Git commands that require SSH verification
	public TransportConfigCallback getTransportConfigCallback() {
		return transportConfigCallback;
	}
	
	public void createConfigFile(File sshDir) throws IOException {
		this.configFile = new File(sshDir, "config");
		
		String txt = "Host github.com\n"
				+ "  AddKeysToAgent no\n"
				+ "  StrictHostKeyChecking no";
		
		java.nio.file.Files.writeString(this.configFile.toPath(), txt);
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
	
	// Helper method to delete temporary key file... not super efficient and need some way to enforce
	public void deleteTempFile() {
		this.privateKeyFile.delete();
	}
	public void deleteConfigFile() {
		this.configFile.delete();
	}
}