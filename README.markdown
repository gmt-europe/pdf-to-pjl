# PDF to PJL

LGPL License.

## Introduction

PDF to PJL is an application that allows control over printer output of a PDF document
through PJL job codes. PDF does not have support to e.g. specify the tray a page should
be output to. PDF to PJL provides this functionality.

## Command line arguments

PDF to PJL takes the following command line arguments:

* `-n <job name>`: The name of the print job. This parameter is mandatory.
* `-D <display name>`: The display name of the print job.
* `-i <input file name>`: The file name to use as input. If this parameter is omitted,
  `STDIN` is used instead.
* `-o <output file name>`: The file name to output the print job to. If this parameter
  is omitted, `STDOUT` is used instead.
* `-d <key>=<value>`: Registers a replacement variable.

## Adding PJL instructions

PDF to PJL allows printer instructions to be defined directly in the document. It processes
these instructions by searching a PDF page for a specific pattern. When this pattern is
encountered, the text is parsed and used to control the PJL job.

The following consturct sets a parameter for a page:

    <![PJL[SET KEY = VALUE]]>

To set e.g. the tray a page should be to:

    <![PJL[SET MEDIASOURCE = TRAY1]]>

Parameters can also be reset using the following construct:

    <![PJL[RESET KEY]]>

For example to reset the tray:

    <![PJL[RESET MEDIASOURCE]]>

Every page is searched for PJL instructions separately. This means that e.g. the tray a page
is output to can be specified per page. If a page does not contain any PJL instructions, the
PJL instructions from the previous page apply to that page too.

A full list of options that can be set can be found at
[http://h10032.www1.hp.com/ctg/Manual/bpl13208.pdf](http://h10032.www1.hp.com/ctg/Manual/bpl13208.pdf).

At the moment the only way the PJL parameters can be set is by inserting them as text directly
into the document. To hide the instructions from the final output, the text should be made the
same color as the background color of the page. Also make sure that the instructions to not line
wrap to make sure they are detected correctly. The size, font or color of the instruction text does not
matter.

## Replacement variables

The values of the PJL settings can also be parameterized. The following example shows how to
parameterize the output tray:

    <![PJL[SET MEDIASOURCE = ${FirstTray}]]>

To specify the value of a replacement variable, the `-d` option must be used on the command line.

## Usage

The PDF to PJL application can be used as follows:

    generate-document | java -jar pdf-to-jpl.jar -n 'print-job.pdf' | lp -d printer-name

The above command processes the output of the `generate-document` application and pipes it to `lp`.
Alternatively the `-i` and `-o` parameters can be used to input and output files instead of working
with `STDIN` and `STDOUT`.

## Bugs

Bugs should be reported through github at
[http://github.com/gmt-europe/pdf-to-pjl/issues](http://github.com/gmt-europe/pdf-to-pjl/issues).

## License

PDF to PJL is licensed under the LGPL 3.
