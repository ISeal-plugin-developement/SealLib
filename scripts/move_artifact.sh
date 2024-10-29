#!/bin/bash

rm -r -f ~/Desktop/servers/paperLatest/plugins/SealLib-*.jar
mv ~/IdeaProjects/SealLib/target/output/* ~/Desktop/servers/paperLatest/plugins/
echo "SealLib artifact moved to server plugins folder"
echo "Here be dragons!"
