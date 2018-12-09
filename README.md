png2petscii
-----------

This is an experimental program to try to generate various BBS graphics
with PNGs and JPEGs as source.  

It's my first JavaFX project so I might be doing everything wrong here.

For PETSCII, currently it can generate output in color or monochrome, using 
either the uppercase/graphics or lowercase/uppercase character sets.  You can
also save the file as a PRG or P00 file, so that you can run it easily on an
emulator or real Commodore 64.

For ATASCII, there is only one output option.

Future plans
------------

* For ATASCII, filter out attempts to type an escaped CR
* Load PETSCII fonts through a bin file instead of a png file
* fix loading from PNGs with alpha channels
* Image preprocessing.
  * quantize source image to C64 palette
  * quantize each tile to most plentiful color
* command line utility

