/**
 * Extract_PersonName_And_Street.groovy
 *
 * Created by Ankitha Pille on 01/03/2016
 * This script is for extracting person name and street
 *
 */

// Input and Output Files
String inPath= "";
String inFilename="";
String outPath="";
String outFilename="";
args.each {
    String dir= "$it".split('_').getAt(0.."$it".split('_').size()-2)
    dir=dir.replace("[","")
    dir=dir.replace("]","")
    dir=dir.replace(", ","_")
    inPath = "../$dir/InputFiles/"
    outPath = "../$dir/OutputFiles/"
    inFilename = "$it"+"_Cleaned.txt"
    outCSVFilename= "Name_StreetName-from-"+"$it"+"-Cleaned.csv"
}
File inFile = new File(inPath + inFilename)
File outCSVFile = new File(outPath + outCSVFilename)
outCSVFile.delete()

//CSV Header
String csvHeader ="outLineNum,outFirstName, outLastName, outMI, outJrMrs, outSt"
outCSVFile << csvHeader + "\n"

// Pattern for capital words
String capW = "[A-Z][a-z]+"
String LastName = "[A-Z01][a-z]*[A-Z]?[a-z01]+"//should allow for names to have 2 capital characters (McDonald) can have 1 or 2 caps
String FirstName = "[A-Z01][a-z01]*(?:[A-Z01][a-z01]+)?"//should allow for names to have 2 capital characters (McDonald) can have 1 or 2 caps
String capIorW = "[A-Z01][a-z01]*"

def line1 = inFile.eachLine { line, lineNum ->
String savedLine = line // Saving the original line to write to reject file
def m // Reference for the matcher
String out  // Reference for outputting
def outNull = null // used to write null, probably can just write it

// Used for flow control and writing to reject file
boolean hasName = false
boolean hasAdd = false // there a address

// Used for writing to csv file
String outName = "" // Name output for the csv
String outStreetName = "" // Residency and house number and street
    m = line =~ "^(${LastName}) (${FirstName}) (${capIorW} )?((Mrs)|(jr))?"
    if(m){
        outName = "${m[0][1]},${m[0][2]},${m[0][3]},${m[0][4]}"
        outName  = (outName =~ /0/).replaceFirst('O')
        outName = (outName =~ /1/).replaceFirst('l')
        hasName = true
        line = m.replaceFirst('')
    }
    if (hasName) {
        m = line =~ /\b([rh])\s?([\d]+) (\(\d+\) )?([\w ]+)/
        if (m) {
            hasAdd = true
            outStreetName = m[0][4]
            line = m.replaceFirst("")
        }
    }
    if(hasName && hasAdd) {
        out = lineNum + ",${outName},${outStreetName}" + "\n"
        outCSVFile << out
    }
}

println "DONE: Extracting Person Names and Streets"