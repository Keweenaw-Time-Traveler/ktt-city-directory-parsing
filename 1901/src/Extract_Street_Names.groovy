/**
 * Parse_AllEntries_with_Address.groovy
 *
 * Created by Robert Pastel on 9/9/2016.
 * Created by Ankitha Pille on 01/03/2016
 * This script is for Parsing all entries having Address
 *
 */
// Input and Output File
String inPath= "";
String inFilename="";
String outPath="";
String outFilename="";
String outCSVFilename="";
String outRejectFilename="";
args.each {
    String dir= "$it".split('_').getAt(0.."$it".split('_').size()-2)
    dir=dir.replace("[","")
    dir=dir.replace("]","")
    dir=dir.replace(", ","_")
    inPath = "../$dir/InputFiles/"
    outPath = "../$dir/OutputFiles/"
    inFilename = "$it"+"_Cleaned.txt"
    outCSVFilename = "List_of_all_Streets.txt"

}
File inFile = new File(inPath + inFilename)
File outCSVFile = new File(outPath + outCSVFilename)

// Delete Files if already exist
outCSVFile.delete()

def line1 = inFile.eachLine { line, lineNum ->
    String savedLine = line // Saving the original line to write to reject file
    def m // Reference for the matcher
    String out  // Reference for outputting
    // Used for writing to csv file
    //def outNull = null // used to write null, probably can just write it
    String outLastName = null
    String outFirstName = null
    String outMI =null
    String outJrMrs = null
    String outT=null
    String outStNum= null
    String outStName=null
    String outCity=null
    String outBuilding=null
    String outJob= null
    String outJobLocation=null
    String outSpouse=null
    //String outAddress=null
    // Used for flow control and writing to reject file
    boolean hasName = false //there is a name
    boolean hasAdd = false // there is a address
    boolean hasTitle=false
    /*
    * EXTRACT NAME SECTION
    */

    //Split the line using ",". The first part is the name
    m = line =~ /\(([A-Za-z,\s]*)\)/
    if (m) {
        outSpouse="${m[0][1]}"
        line = (line =~ /\([A-Za-z,\s]*\)/).replaceFirst("")
    }

    String name = line.split(',')[0]

    // Check for Mrs/jr and place it in outJrMrs Column.
    if(!name.contains("Co")&&!name.contains("CO")&&!name.contains("SON")&&!name.contains("Bros")&&!name.contains("BROS")) {

        m = name =~ / (St|Mrs|Jr|HON|CAPT|Dr|Rev|Capt|Hon|Miss|jr)\b/
        if (m) {
            outJrMrs = "${m[0][1]}"
            //if "Mrs/Jr" exists then replace it with "" in the name
            //It means we are removing "Mrs/Jr" for further parsing
            name = (name =~ /${m[0][1]}\s?/).replaceFirst("")
            line = (line =~ /${m[0][1]}\s?/).replaceFirst("")
            hasTitle=true;
        }
        //Extract Last Name, First Name and Middle Initial

        //Trim name for remove any leading or preceding spaces
        name = name.trim();
        //Split name using delimiter " ", first part is "FirstName", second part is "LastName" and the third part is "Middle Initial"
        String[] name_parts = name.split(" ")

        //Avoid parsing a company

        //name should have at least 2 parts- FirstName and Last Name
        //name should not have more than 3 parts- if more than 3 parts exists it will be considered as an advertisement
        if (name_parts.size() >= 2 && name_parts.size() <= 3) {
            hasName = true
            outFirstName = name_parts[0];
            outLastName = name_parts[1];
            if (name_parts.size() == 3)
                outMI = name_parts[2];
        }
        else if (name_parts.size() == 1) {
            if(hasTitle) {
                hasName= true
                outLastName = name_parts[0];
            }
        }
    }


    if(hasName) {
        line = (line =~ /${name}( )?, /).replaceFirst("")
        //Split the line using ',' the last part of it will be either "res same" or a "res" or "bds" or "wks and bds" or "wks" or "rooms over" followed a valid address
        address = line.split(",")[line.split(',').length - 1].trim()

        if (address.contains("bds") || address.contains("works") ||address.contains("res") ||address.contains("rms")||address.contains("res near") ||address.contains("res rear")||address.contains("bds over") ||address.contains("res over") ||address.contains("res rear of") || address.contains("wks and bds") || address.contains("wks") || address.contains("rooms over")||address.contains("rooms")) {
            m = address =~ /^(bds|res|wks and bds|works|wks|res near|res rear|rooms over|rooms|res rear of|rms|res over|bds over) ([\dA]*) ([0-9A-Za-z\s]+)/
            if (m) {
                outCSVFile << "${m[0][3]}"+"\n"
            }

        }
    }
}

println "DONE: Extracting all street names"
