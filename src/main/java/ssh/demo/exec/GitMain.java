package ssh.demo.exec;

import java.io.IOException;
import org.eclipse.jgit.api.errors.TransportException;

import ssh.demo.service.GitService;


/* 
 *  Class to execute clones/pushes from/to GitHub repository. Calls GitService.java to perform these operations.
 */
public class GitMain {

	@SuppressWarnings("static-access")
	public static void main(String[] args) throws IOException, TransportException{
		// Create Git Service object
		GitService gitService = new GitService("git@github.com:userName/repoName.git", "/path/to/local/gitDir", "**privateKey**", "/desired/path/to/temp/file");
		
		//gitService.cloneRepository();
		gitService.addCommitPush("file_to_push");
		
		// Delete temporary key file 
		gitService.deleteTempFile();
		
		// Before the end of the main method, close out of SSH service
		gitService.stopSshService();
	}
}

