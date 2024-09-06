package ssh.demo.exec;

import java.io.IOException;
import ssh.demo.service.GitService;

/** 
 *  Class to execute clones/pushes from/to GitHub repository. Calls GitService.java to perform these operations.
 *  
 */
public class GitMain {
    
	// Test area for GitService methods
	
	@SuppressWarnings("static-access")
	public static void main(String[] args) throws IOException{
    	// Create Git Service object
    	GitService gitService = new GitService("git@github.com:userName/repoName.git", "/path/to/localDir");
    	//gitService.cloneRepository("git@github.com:userName/repoName.git", "/path/to/localDir");
    	gitService.addCommitPush("test.txt");
    }

}
