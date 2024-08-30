# pacLabsSSHtest1

Repository to work on automating connection to GitHub using SSH keys.

### Steps:
  1. Generate keys & add public to GH
  2. Verify connection to GH
  3. Perform Git commands on repository & attempt push
  4. Same as 3, but with private key located somewhere other than .ssh directory
       a) When calling ssh command to connect with GH --> path to private key can be specified
       b) Same as a) --> ssh-add can have path to private key specified
