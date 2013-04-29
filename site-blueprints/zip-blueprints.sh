#!/bin/bash
if [ -f ~/blueprints.zip ]; then rm ~/blueprints.zip; fi
for f in brochure corporate empty minimal retail; do cd $f/src/main/site; zip -r ~/blueprints $f -x \*/.svn/\*; cd ../../../../; done
