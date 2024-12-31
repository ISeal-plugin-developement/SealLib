#!/bin/bash

rm -r -f ~/Desktop/servers/paper1201/plugins/SealLib-*.jar
mv ~/IdeaProjects/SealLib/target/output/* ~/Desktop/servers/paper1201/plugins/
echo "SealLib artifact moved to server plugins folder"
echo "Here be dragons!"
