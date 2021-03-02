###  merge 分支
在test分支内merge dev分支
git merge dev

###
其他分支上做了修改，将修改移自己的分支
git stash

git stash pop

### pull

pull 则是将远程主机的dev分支最新内容拉下来后与当前本地分支直接合并 fetch+merge

git pull origin dev
