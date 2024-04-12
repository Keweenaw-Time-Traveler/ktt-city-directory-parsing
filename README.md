## KeTT City Directory Parsing Scripts

This repository contains pdf and corresponding txt files of OCR historical City Directory data for serveral cities in the Keweenaw Peninsula broken up into directories for different years by folder. Each folder contains an src folder with the scripts used to parse the 
data for that particular year. The scripts were written in Groovy. 

A folder for each year contains:

sub-folders for each city containing input and output files after the scripts were executed. For input files these include the original OCR pdf files and the corresponding txt files. The outline files contain both raw and cleaned csv file from when the scripts were executed.

An src folder in each directory contains the parsing scripts and readme files.

1. City Directory Year
   - City 
     - InputFiles
      - pdf, csv
     - OutputFiles
       - csv, txt
   - src
      - Groovy scripts
      - Readme file   

For steps to run the scripts, see the Readme.txt file located in the src directory of each year. 
