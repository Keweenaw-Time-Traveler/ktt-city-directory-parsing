/**
 * Clean_Ready_txt.groovy
 * Created by Ankitha Pille on 01/03/2016
 * This script is Removing blank lines from input file
 *
 */


String inPath= "";
String inFilename="";
String outFilename="";
args.each {
    String dir= "$it".split('_').getAt(0.."$it".split('_').size()-2)
    dir=dir.replace("[","")
    dir=dir.replace("]","")
    dir=dir.replace(", ","_")
    inPath = "../$dir/InputFiles/"
    outPath = "../$dir/OutputFiles/"
    inFilename = "$it"+"_Cleaned-with empty lines.txt"
    outFilename = "$it"+"_Cleaned.txt"

}

File inFile = new File(inPath + inFilename)
File outFile = new File(inPath + outFilename)
outFile.delete();
    def line1 = inFile.eachLine { line, lineNum ->
        if (line.length() != 0) {
        outFile << line+"\n"
        }
    }

inFile.delete()
println "DONE: Removing Empty lines from cleaned.txt"

