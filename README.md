png2petscii
-----------

This is an experimental program to try to generate Commodore
BBS graphics with PNGs and JPEGs as source.

It's my first JavaFX project so I might be doing everything wrong here.

Currently it can generate output in color or monochrome, using either
the uppercase/graphics or lowercase/uppercase character sets.

You can save the output as a raw text file, or a program file that
will print the output.  Program files can be saved in PRG or P00
format so that you can see them on an emulator.

Future plans
------------

* Image preprocessing.
  * quantize source image to C64 palette
  * quantize each tile to most plentiful color
* ATASCII support
* command line utility

