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
            outCSVFilename = "Name_and_Address-from-"+"$it"+"_with_outBuilding.csv"
            outRejectFilename = "reject-from-"+"$it"+"-Cleaned.txt"

        }
        File inFile = new File(inPath + inFilename)
        File outCSVFile = new File(outPath + outCSVFilename)
        File outRejectFile = new File(outPath + outRejectFilename)

        // Delete Files if already exist
        outCSVFile.delete()
        outRejectFile.delete()

        def Cities=[]
        String  inFilename2= "List_of_all_Cities.txt"
        File inFile2 = new File(inFilename2)
        def line2 = inFile2.eachLine { line1, lineNum ->
            Cities.add(line1.trim());
        }
        Cities=(Cities.unique())

        def Streets=[]
        String  inFilename3= "List_of_all_Streets.txt"
        File inFile3 = new File(inFilename3)
        def line3 = inFile3.eachLine { line, lineNum ->
        Streets.add(line.trim());
        }
        Streets=(Streets.unique())

        def Jobs=[]
        String  inFilename4= "List_of_Occupations.txt"
        File inFile4 = new File(inFilename4)
        def line4 = inFile4.eachLine { line4, lineNum ->
        Jobs.add(line4.trim());
        }
        Jobs=(Jobs.unique())

        def Buildings=[]
        String  inFilename5= "OutBuilding_With_Address.txt"
        File inFile5 = new File(inFilename5)
        def line5 = inFile5.eachLine { line5, lineNum ->
            String building=line5.split(',')[0].trim();
        Buildings.add(building);
        }
        Buildings=(Buildings.unique())

// Header of the CSV File
        String csvHeader ="outLineNum,outFirstName, outLastName, outMI, outJrMr, outJob, outJobLocation, outT, outStNum, outStName, outCity, outBuilding"
        outCSVFile << csvHeader + "\n"

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
            //String outAddress=null
            // Used for flow control and writing to reject file
            boolean hasName = false //there is a name
            boolean hasAdd = false // there is a address
            boolean hasTitle=false
            /*
            * EXTRACT NAME SECTION
            */

            //Split the line using ",". The first part is the name
            String name = line.split(',')[0]

            // Check for Mrs/jr and place it in outJrMrs Column.
            m = name =~ /(Mrs|Jr|HON|CAPT|Dr|Rev|Capt|Hon|Miss|jr)/
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
            if(!name_parts[name_parts.length-1].equals("Co")&&!name_parts[name_parts.length-1].equals("CO")&&!name_parts[name_parts.length-1].equals("SON")&&!name_parts[name_parts.length-1].equals("Bros")&&!name_parts[name_parts.length-1].equals("BROS")&&!name_parts[name_parts.length-1].equals("SON")) {

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
                else {
                    for (int i = 0; i <Jobs.size(); i++) {
                        if(line.split(',')[1].trim().contains(Jobs[i])) {
                        hasName = true;
                        name = line.split(',')[0].trim();
                        name_parts = name.split(' ');
                        outMI = name.split(' ')[name_parts.length - 1];
                        outLastName = name.split(' ')[name_parts.length - 2];
                        name.replace(outMI, "");
                        name.replace(outLastName, "")
                        outFirstName = name;
                            break;
                        }
                    }
                }

            }

            //Remove name from the line for further parsing
            //If the line doesnot have a name, do nothing and write it to the reject file

            if(!hasName)
                outRejectFile<< lineNum+ " NAME: "+ savedLine+"\n"

            //If the line has a Name , check for the address

            /*
            * EXTRACT ADDRESS SECTION
            * Note: It will parse if and only if
            * 1)Line has res same and the array element preceding it is a valid address
            *   A valid Address should be in the format <digits-Street Num> <Street Name>
            * 2) or Line has "res" or "bds" or "wks and bds" or "wks" or "rooms over" followed a valid address
            */

            if(hasName) {
                line = (line =~ /${name}( )?, /).replaceFirst("")

                //Split the line using ',' the last part of it will be either "res same" or a "res" or "bds" or "wks and bds" or "wks" or "rooms over" followed a valid address
                address = line.split(",")[line.split(',').length - 1].trim()

                // if the last part is "res same", it means the job location and the address is same for the person
                //In that case we will extract the array element previous to it as an address for that person
                if (address.equals("res same")||address.equals("res the same")) {
                    outAddress = line.split(",")[line.split(',').length - 2].trim()


                    // Check if the previous array element is a valid address
                    m = outAddress =~ /^(over|office )?([\d\s]+(to|and|-)?[\d\s]+(-)?[\d\s]*) ([A-Z][A-Za-z\s]+)/
                    if (m) {
                        outT = "res" +" ${m[0][1]}"
                        outStNum = "${m[0][2]}"
                        outStName= "${m[0][3]}"
                        hasAdd = true;
                    }
                    if (Cities.contains(outAddress)) {
                        outT = "res"
                        outCity= "${outAddress}"
                        hasAdd = true;
                    }
                    else if (Streets.contains(outAddress)) {
                        outT = "res"
                        outStName= "${outAddress}"
                        hasAdd = true;
                    }

                    line = (line =~ /${address}/).replaceFirst("")
                    line = (line =~ /, $/).replaceFirst("")
                    line = (line =~ /${outAddress}/).replaceFirst("")
                    line = (line =~ /, $/).replaceFirst("")//line = (line =~ /, ${address}/).replaceFirst("")
                //if the last part is "res" or "bds" or "wks and bds" or "wks" or "rooms over" followed a valid address
                } else if (address.contains("bds") || address.contains("works") ||address.contains("res") ||address.contains("res near") ||address.contains("res rear")||address.contains("bds over") ||address.contains("res over") ||address.contains("res rear of") || address.contains("wks and bds") || address.contains("wks") || address.contains("rooms over")||address.contains("rooms")) {
                    m = address =~ /^(bds|res|wks and bds|works|wks|res near|res rear|rooms over|rooms|res rear of|res over|bds over) ([\d\s]+[and|to|-]?[\d\s]*)([A-Z][A-Za-z\s]+)/
                    if (m) {
                        outT= "${m[0][1]}"

                        outStNum = "${m[0][2]}"
                        outStName= "${m[0][3]}"
                        hasAdd = true;
                    }

                    m = address =~ /^(bds|res|wks and bds|wks|res near|res rear|works|rooms over|rooms|res rear of|res over|bds over) ([A-Za-z\s]+)/

                    if(m) {
                        if (Cities.contains(m[0][2])) {
                            outT= "${m[0][1]}"
                            outCity= "${m[0][2]}"
                            hasAdd = true;
                        }
                        else if (Streets.contains(m[0][2])) {
                            outT= "${m[0][1]}"
                            outStName= "${m[0][2]}"
                            hasAdd = true;
                        }
                        else if (m[0][2].equals("at mine")) {
                            outT = "${m[0][1]}"
                            outCity = "Atlantic Mine"
                            hasAdd = true;
                        }
                        else if (m[0][2].equals("at mill")) {
                            outT = "${m[0][1]}"
                            outCity = "Atlantic Mine"
                            hasAdd = true;
                        }
                           else if (Buildings.contains(m[0][2]))
                        {
                            outT = "${m[0][1]}"
                            outBuilding = "${m[0][2]}"
                            hasAdd = true;
                        }
                    }

                    line = (line =~ /${address}/).replaceFirst("")
                    line = (line =~ /, $/).replaceFirst("")
                }

                //If the line does not have an address, do nothing and write it to the reject file
                if (!hasAdd)
                    outRejectFile << lineNum + " ADD: " + savedLine + "\n"

            }//If the Line has both name and address, write it the output CSV File
            if(hasName && hasAdd )
            {
                m = line =~ /a( )?(res|resident) (of Houghton )?(Co )?since \d+, /
                if(m) {
                    line = m.replaceFirst('')
                }
                if(line.size()!=0) {
                    line=line.trim();
                        m = line =~ /^([a-z][a-z\s,]+)/
                        if(m) {
                                outJob = "${m[0][1]}"
                                outJob = outJob.replace(',', '')
                                line = m.replaceFirst('')
                                line = line.trim();
                            if(line.size()!=0) {
                                outJobLocation = line.replace(',', '')
                            }
                        }
                }
                out = lineNum + ",${outFirstName},${outLastName},${outMI},${outJrMrs},${outJob},${outJobLocation},${outT},${outStNum},${outStName},${outCity},${outBuilding}" + "\n"
                outCSVFile << out
            }
        }

println "DONE: Extracting all possible entries with addresses"