## Checkout all branches from remote as tracking branches. Based on  i
## https://stackoverflow.com/questions/379081/track-all-remote-git-branches-as-local-branches/6300386#6300386

UPSTREAM=$1
MYREPO=$2

usage() {
   echo "Usage:"
   echo "$0 <upstream-remote> <target-remote>"
   echo ""
   echo "Example which ensures remote named 'maxandersen' have all the same branches and tags as 'origin'"
   echo "$0 origin maxandersen"
   exit 1
}

if [ -z "$UPSTREAM" ]
then
 echo Missing upstream remote name.
 usage
fi

if [ -z "$MYREPO" ]
then
 echo Missing target remote name. 
 usage
fi

read -p "1. This will setup '$MYREPO' to track all branches in '$UPSTREAM' - Are you sure ?" -n 1 -r

if [[ $REPLY =~ ^[Yy]$ ]]
then
 for brname in `git branch -r | grep "$UPSTREAM" | grep -v master | grep -v HEAD | sed -e 's/.*\///g'`;
 #for brname in `git branch -r | grep "$UPSTREAM" | grep -v HEAD | sed -e 's/.*\///g'`;
   do git branch --track $brname $UPSTREAM/$brname ;
 done
fi

read -p "2. This will push all local branches and tags into '$MYREPO' - Are you sure ?" -n 1 -r

if [[ $REPLY =~ ^[Yy]$ ]]
then
 git push --all $MYREPO
 git push --tags $MYREPO
fi

