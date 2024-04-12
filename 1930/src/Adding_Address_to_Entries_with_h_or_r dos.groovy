/**
 * Adding_Address_to_Entries_with_h_or_r dos.groovy
 *
 * Created by Ankitha Pille on 01/03/2016
 * This script is for Adding address for entries having h do and r do
 *
 */

//Input and Output address
String inPath= "";
String inFilename="";
String outPath="";
String outFilename="";
args.each {
    String dir= "$it".split('_').getAt(0.."$it".split('_').size()-2)
    dir=dir.replace("[","")
    dir=dir.replace("]","")
    dir=dir.replace(", ","_")
    inPath = "../$dir/InputFiles/" // can use relative address
    outPath = "../$dir/OutputFiles/"
    inFilename = "Name_and_Address-from-"+"$it"+"_with_Dos.csv"
    outFilename= "Name_and_Address-from-"+"$it"+".csv"
}

File inFile = new File(outPath + inFilename)
File outFile = new File(outPath + outFilename)

outFile.delete()

def line1 = inFile.eachLine { line, lineNum ->
        String horr= line.split(',')[18]
        if (("${horr}".trim().equals("h do"))||("${horr}".trim().equals("r do")))
            {
                def line3 = inFile.eachLine { line1, lineNum1 ->
                if(lineNum1+1==lineNum)
                    {
                        String toadd= line.split(',').getAt(0..8);
                        toadd=toadd.replace("[","")
                        toadd=toadd.replace("]","")
                        toadd=toadd+','+line1.split(',').getAt(9..18);
                        toadd=toadd.replace("[","")
                        toadd=toadd.replace("]","")
                        outFile<<toadd.trim()+"\n"
                    }
                }
            }
            else
            {
            outFile<<line+"\n"
        }
    }
inFile.delete()
println "DONE: Adding Address to Entries having h or r Dos"