#!/bin/bash

# This builds the release notes for a branch from Git. 

set -e

echo "Building Release notes"

mkdir -p target/classes
echo "=============" | tee -a ReleaseNotes.txt
echo "Release Notes - Last Four Weeks - As At $(date) on repository $(basename `git rev-parse --show-toplevel`)"  | tee -a ReleaseNotes.txt
echo "============="  | tee -a ReleaseNotes.txt

## Commits with reference to Rally
git log --since=4.weeks --pretty="%s" | grep -io "\(DE\|US\|QC\)[0-9]\{3,\}" | uniq | awk '{print $1; print system("git log --pretty=\"%ci %an %s\" | grep -i -v \"Merge\" | grep -i "$1)}' |  sed -re 's/^[0-9]{1,2}$/ /g' | tee -a ReleaseNotes.txt

echo "" | tee -a ReleaseNotes.txt
echo "" | tee -a ReleaseNotes.txt
echo "" | tee -a ReleaseNotes.txt
echo "=============" | tee -a ReleaseNotes.txt
echo "Git Comment Learning Opportunities - Last Four Weeks"  | tee -a ReleaseNotes.txt
echo "============="  | tee -a ReleaseNotes.txt

## Recent committers with no reference to Rally
git log --since=4.weeks --pretty="%h %<|(30)%an %s" | grep -i -v "\(\(DE\|US\|QC\)[0-9]\{3\}\|\[\.*\?DEV\.*\?\]\|Merge\)"| awk '{print substr($0,9,20)}' | cat | sort | uniq -c | sort --reverse | tee -a ReleaseNotes.txt

echo "" | tee -a ReleaseNotes.txt
echo "" | tee -a ReleaseNotes.txt
echo "" | tee -a ReleaseNotes.txt
echo "=============" | tee -a ReleaseNotes.txt
echo "Standards for Git Commits" | tee -a ReleaseNotes.txt
echo "=============" | tee -a ReleaseNotes.txt
echo "http://dwgps0026.btfin.com/twiki/bin/view/nextgen/TechDiscussionsGitComments#Examples" | tee -a ReleaseNotes.txt

echo "Finished building Release notes"

