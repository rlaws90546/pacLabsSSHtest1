package ssh.demo.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.lang.IllegalStateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.TransportConfigCallback;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.SshTransport;
import org.eclipse.jgit.transport.Transport;
import org.eclipse.jgit.transport.sshd.SshdSessionFactory;
import org.apache.commons.io.FileUtils;

import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.config.keys.ClientIdentityLoader;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.client.simple.SimpleClient;
import org.apache.sshd.common.keyprovider.KeyIdentityProvider;

import ssh.demo.service.SshService;

/**
 *  Class to provide Main.java with methods needed to push changes to GitHub.
 * 
 *  What to implement >   1. Modification of a file, take filename as parameter
 * 			              2. New file created, also take filename as parameter
 */
public class GitService {
	
	// Variables for repository to be cloned, path to the cloned directory in file system, and instance of Git
	private String repo;
	private Path localDir;
	private Git git;
	private SshService sshService;
	//private String userName;
	//private String email;
	
	@SuppressWarnings("unused")
	private final Logger log = LoggerFactory.getLogger(getClass());

	
	/* 
	 * Constructor to assign values to non-ssh Git parameters
	 * 
	 * NEED TO --> create overloaded constructor for Git
	 */
    public GitService(/*String userName, String email,*/ String repoName, String cloneDirName) throws IOException{
	    //this.userName = userName;
    	//this.email = email;
    	this.repo = repoName;
	    this.localDir = Paths.get(cloneDirName).toAbsolutePath().normalize();
	    
        try {
			git = Git.init().setDirectory(this.localDir.toFile()).call();
		} catch (IllegalStateException | GitAPIException e) {
			e.printStackTrace();
		}
        
        sshService = new SshService();
	    
	    // Open the existing local repository
	    //git = Git.open(this.localDir.toFile());
	    //this.sshService = new SshService();
	    
    }
    
    
    /* 
	 * Helper method to clone repository from GitHub.
	 */
	@SuppressWarnings("static-access")
	public void cloneRepository(String repo, String path) throws IOException {
    	System.out.println("Cloning " + repo + " to " + path);
    	try {
    		File dir = new File(path);
    		FileUtils.cleanDirectory(dir);
    		if (sshService != null) {
    			git.cloneRepository().setTransportConfigCallback(sshService.getTransportConfigCallback())
    			.setDirectory(Paths.get(path).toFile()).setURI(repo)
    			.call();
    		}
    	} catch (GitAPIException err) {
    		throw new IllegalStateException("\nCould not clone Git repository: ", err);
	    }
    }
    
	
	
	
	/* 
	 * Helper method to add & commit changes made to the Git repository.
	 */
    @SuppressWarnings("unused")
	public final void addCommitPush(String fileName) throws IOException {
    	try {
            // Add, commit, and push changes
            git.add().addFilepattern(fileName).call();
            git.commit().setMessage("Automated commit added " + fileName + " to repository").call();
            if (sshService != null) {
	            git.push()
	               .setRemote(repo)
	               .setTransportConfigCallback(sshService.getTransportConfigCallback())
	               .call();
	            System.out.println("Pushed " + fileName + " to remote repository successfully!");
            } else {
            	System.out.println("Something happened 	:(");
            }
        } catch (GitAPIException e) {
            e.printStackTrace();
        }
    }

}
