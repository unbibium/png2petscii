png2petscii
-----------

This is an experimental program to try to generate Commodore
BBS graphics with PNGs and JPEGs as source.

It's my first JavaFX project so I might be doing everything wrong here.

Currently it seems to be processing the input and generating output,
accoridng to the display to stdout.  But my attempts to preview the
output visually with a Canvas object have failed so far.

I was only able to get this far by sacrificing the automatic scaling
I got when I loaded the image into a JavaFX ImageView control.  I
couldn't get it to read pixels from the resulting image object.
So, instead I'm loading it into a BufferedImage object, which is
in a completely different branch of Java, but it's an object I got
working well enough to load the font into memory with it.

Future plans
------------

* Scale image before converting
* Display preview
* Save to file
  * S00 sequential file
  * P00 BASIC program
* Color output
* ATASCII support
* command line utility


