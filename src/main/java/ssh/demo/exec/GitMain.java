package ssh.demo.exec;

import ssh.demo.service.GitService;

/* 
 *  Class to execute clones/pushes from/to GitHub repository. Calls GitService.java to perform these operations.
 */
public class GitMain {

	@SuppressWarnings("static-access")
	public static void main(String[] args) throws Exception{
				
		String publicKeyPath = "pubkey_path.pem";
		String privateKeyPath = "privkey_path.pem";
		
		
		// Create Git Service object
		GitService gitService = new GitService("git@github.com:userName/repoName.git", "/path/to/local/gitDir");
		
		gitService.betaCreateSshService(publicKeyPath, privateKeyPath);
		//gitService.createSshService("path/to/ssh/directory", "**privateKey**");
		
		// Pick which method you would like to use from GitService --> cloneRepository() OR addCommitPush()
		//gitService.cloneRepository();
		gitService.addCommitPush();
		
		// Delete temporary key file 
		//gitService.deleteTempFile();
		
		// Delete temporary configuration file (only used when the private key is provided such as in the example above)
		//gitService.deleteConfigFile();
				
		
		// Before the end of the main method, close out of SSH service
		gitService.stopSshService();
	}

}