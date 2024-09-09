# Performing Git Operations with SSH

This repository automates connecting to GitHub using SSH keys when they are (or aren't) in the default location (".ssh" directory).

## Getting Started:
  1. Generate keys & add public to GitHub
  2. Verify connection to GitHub in the CLI using "ssh -T git@github.com"
  3. Change methods in GitMain.java to fit what you want to do (functionality for "git clone (ssh)" & "git add", commit, & push exists currently)
  3b. Same as 3, but set the path to your private key (if NOT located in the ".ssh" directory) when constructing GitService instance in GitMain
