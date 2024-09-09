package ssh.demo.exec;

import java.io.IOException;
import org.eclipse.jgit.api.errors.TransportException;

import ssh.demo.service.GitService;

/** 
 *  Class to execute clones/pushes from/to GitHub repository. Calls GitService.java to perform these operations.
 */
public class GitMain {
    
	@SuppressWarnings("static-access")
	public static void main(String[] args) throws IOException, TransportException{
	    // Create Git Service object
		GitService gitService = new GitService("git@github.com:userName/repoName.git", "/path/to/local_directory" /*, *OPTIONAL* "path/to/privKey"*/);
		gitService.cloneRepository();
		gitService.addCommitPush("file_to_push");
		
		// Before the end of the main method, close out of SSH service
		gitService.stopSshService();
	}
}
