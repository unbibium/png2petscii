png2petscii
-----------

This is an experimental program to try to generate Commodore
BBS graphics with PNGs and JPEGs as source.

It's my first JavaFX project so I might be doing everything wrong here.

Currently it will generate a color image.  The code to generate
monochrome images is still there, and may be more useful.

You can save the output as a raw text file, or a program file that
will print the output.  Program files can be saved in PRG or P00
format.  The output is very inefficient; it sends a redundant RVS 
ON/OFF and color code for each character.

Future plans
------------

* Optimize PETSCII output.
* Image preprocessing.
  * quantize source image to C64 palette
  * quantize each tile to most plentiful color
* ATASCII support
* command line utility

