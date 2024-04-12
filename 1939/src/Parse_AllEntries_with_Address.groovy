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
        String inFilename1="";
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
            outFilename= "Name_and_Address-from-"+"$it"+"_with_Dos.csv"
            inFilename1 = "Name_StreetName-from-"+"$it"+"-Cleaned.csv"
            outCSVFilename = "Name_and_Address-from-"+"$it"+"_with_Dos.csv"
            outRejectFilename = "reject-from-"+"$it"+"-Cleaned.txt"

        }
        File inFile = new File(inPath + inFilename)
        File outCSVFile = new File(outPath + outCSVFilename)
        File outRejectFile = new File(outPath + outRejectFilename)

        //Create Street Dictionary
        def Streets = []
        File inFile1 = new File(outPath + inFilename1)
        def line2 = inFile1.eachLine { line, lineNum ->
            Streets.add(line.split(',')[5].trim());
        }
        Streets=(Streets.unique())
        Streets=Streets.toArray();

        //Create City Dictionary
        def Cities=[]
        String  inFilename2= "List_of_all_Cities.txt"
        File inFile2 = new File(inFilename2)
        def line3 = inFile2.eachLine { line1, lineNum ->
            Cities.add(line1.trim());
        }
        Cities=(Cities.unique())
        Cities=Cities.toArray();

        // Delete Files if already exist
        outCSVFile.delete()
        outRejectFile.delete()

        // Header of the CSV File
        String csvHeader ="outLineNum,outFirstName, outLastName, outMI, outJrMrs, outWid, outSpouse, outJob, outWorkPlace, outT, outStSide, outStNum, outStNum(changed), outSt, outRd, outCity, outLoc, outMoved, outH/Rdo"
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

        // Used for flow control and writing to reject file
            boolean hasName = false
            boolean hasAdd = false // there a address

        // Used for writing to csv file
            String outName = ""
            String outStSide=""
            String outT=""
            String outStNum=""
            String outStName=""
            String outMoved=""
            String outDo=""
            String outWid=""
            String outSpouse=""
            String outJob=""
            String outWorkPlace=""
            String outCity=""
            String outRd=""
            String outLoc=""
            String outSt_changed=""
            String outJrMrs=""
            def outNull = null // used to write null, probably can just write it

            /*
            * EXTRACT NAME SECTION
            */

            // Check for Mrs/jr and place it in outJrMrs Column.
            m=line=~/(Mrs|jr)/
            if(m)
                {
                outJrMrs="${m[0][1]}"
                line = m.replaceFirst('')
                }
            else
            {
            outJrMrs="${outNull}"
            }

            // Extract Last Name, First Name and Middle Initial

            m = line =~ "^(${LastName}) (${FirstName}) (${capIorW} )?"
            if(m){
                outName = "${m[0][1]},${m[0][2]},${m[0][3]}"
                // Remove the O and 1 in the Name(OCR Error)
                // Need to be moved it to entruConstruct
                outName  = (outName =~ /0/).replaceAll('O')
                outName = (outName =~ /1/).replaceAll('l')
                hasName = true
                line = m.replaceFirst('')
                }
                else{
                //if the line do not have name write it to reject file
                //println "REJECT NAME: " + savedLine
                outRejectFile << lineNum + " NAME: " + savedLine + "\n"
            }

            /*
            * EXTRACT ADDRESS SECTION
            */

            if (hasName) {
            //Check for h do and r do
            //and Check for outMoved
            m = line =~ /(?:\(([\w ]+)\))?(?: ([hr] do))?$/
            if (m) {
                outRd="${outNull}"
                outLoc="${outNull}"
                outMoved = "${m[0][1]}";
                outDo = "${m[0][2]}";
                line = m.replaceFirst("")
            }
            if (m[0][2]) {
                outT = "${outNull}";
                outStSide = "${outNull}";
                outStNum = "${outNull}";
                outSt_changed = "${outNull}";
                outStName = "${outNull}";
                outCity = "${outNull}";
                hasAdd = true
            }
            /**
             * Extract street address
             * two types rddd street, hddd street
             */
            line = (line =~ / $/).replaceFirst("")
            m = line =~ /\b([rh])\s?([\dA-Ca]+)(1\/2)? (\(\d+\) )?([\w ]+)$/
            if (m) {

                outRd="${outNull}"
                outLoc="${outNull}"
                hasAdd = true
                outT = "${m[0][1]}";
                outStSide = "${outNull}";
                if(m[0][3]) {
                    outStNum = "${m[0][2]}-${m[0][3]}";
                }
                else
                outStNum = "${m[0][2]}";
                outSt_changed = "${m[0][4]}";
                outSt_changed = (outSt_changed =~ /\(/).replaceFirst('')
                outSt_changed = (outSt_changed =~ /\)/).replaceFirst('')
                outStName = "${m[0][5]}";
                outCity = "${outNull}"

                /**
                 * Extract street address if the street name has "rd/dr"
                 *
                 */
                //"rd/dr" goes in Column "outrd" and the rest to "outStName"
                m1 = outStName =~ / (rd|dr)/
                if (m1) {
                    outRd = "${m1[0][1]}";
                    outStName = (outStName =~ ~/ (rd)/).replaceFirst('')
                }

                //If the street name has "loc"
                //"loc" goes in Column "outLoc" and the rest to "outCity"
                m1 = outStName =~ / (loc)/
                if (m1) {
                    outLoc = "${m1[0][1]}";
                    outCity = (outStName =~ ~/ (loc)/).replaceFirst('')
                    outStName = "${outNull}";
                }
                line = m.replaceFirst("")
                }

                /**
                 * Extract street address for pattern r  "City/Street"                *
                 */

                m = line =~ /\b([rh]) ([A-Z0-9][a-zA-Z0-9\s]+)/
                if (m) {
                    outT = "${m[0][1]}";
                    outRd="${outNull}";
                    outLoc="${outNull}";
                    outStSide = "${outNull}";
                    outStNum = "${outNull}";
                    outSt_changed = "${outNull}";
                    //Check in the Street Directory for the street name
                    def Stfound = Streets.find { m[0][2] =~ /${it}/ }
                    if (Stfound!=null) {
                        hasAdd = true
                        outStName = "${m[0][2]}";
                        outCity = "${outNull}";
                        m1 = outStName =~ / (rd|dr)/
                        if (m1) {
                            outRd = "${m1[0][1]}";
                            outStName = (outStName =~ ~/ (rd)/).replaceFirst('')
                        }
                        m1 = outStName =~ / (loc)/
                        if (m1) {
                            outLoc = "${m1[0][1]}";
                            outCity = (outStName =~ ~/ (loc)/).replaceFirst('')
                            outStName = "${outNull}";
                        }
                    line = m.replaceFirst("")
                    }

                    //Check in the City Directory for the street name
                    else {
                    def cityfound = Cities.find { m[0][2] =~ /${it}/ }
                        if (cityfound != null) {
                            hasAdd = true
                            outStName = "${outNull}";
                            outCity = "${m[0][2]}"
                            m1 = outCity =~ / (loc)/
                            if (m1) {
                                outLoc = "${m1[0][1]}";
                                outCity = (outCity =~ ~/ (loc)/).replaceFirst('')
                            }
                            line = m.replaceFirst("")
                        }
                    }
                }

                /**
                 * Extract street for pattern  r [es|ns|ws|ss]
                 *
                 */

                //Check if the street contains es/ns/ws/ss

                m = line =~ /\b([rh]) (es|ns|ws|ss|rear|nw cor|cor|nsw cor|sw cor|se cor) ([a-zA-Z0-9\s]+)/
                if (m) {
                    hasAdd = true
                    outRd="${outNull}"
                    outLoc="${outNull}"
                    outCity = "${outNull}"
                    outT = "${m[0][1]}";
                    outStSide = "${m[0][2]}";
                    outStNum = "${outNull}";
                    outSt_changed = "${outNull}";
                    outStName = "${m[0][3]}";
                    m1 = outStName =~ / (rd|dr)/
                    if (m1) {
                        outRd = "${m1[0][1]}";
                        outStName = (outStName =~ ~/ (rd)/).replaceFirst('')
                    }
                    m1 = outStName =~ / (loc)/
                    if (m1) {
                        outLoc = "${m1[0][1]}";
                        outCity = (outStName =~ ~/ (loc)/).replaceFirst('')
                        outStName = "${outNull}";
                    }
                    line = m.replaceFirst("")

                }

                //If the person do not have a address add it to the reject list
                if (!hasAdd) {
                    outRejectFile << lineNum + " ADD: " + savedLine + "\n"
                }
            }

            /**
             * EXTRACT SPOUSE, JOB, WORK PLACE
             */
            if(hasName && hasAdd) {
                // clean line, so we can match on the ends
                line = (line =~ /\s+$/).replaceFirst('')
                line = (line =~ /^\s+/).replaceFirst('')
                m = line =~/\((?:(wid) )?([\w\s]+)\)/
                if (m)
                {
                    outWid="${m[0][1]}";
                    outSpouse= "${m[0][2]}"
                    line = m.replaceFirst("")
                }
                else
                {
                    outWid="${outNull}"
                    outSpouse= "${outNull}"
                }
                m = line =~/([a-z][a-z\s]+)((\d+ )?[A-Z][a-zA-Z\s&]+)?/
                if(m){
                    outJob= "${m[0][1]}"
                    outWorkPlace="${m[0][2]}"
                }
                else
                {
                    outJob="${outNull}"
                    outWorkPlace= "${outNull}"
                }
                out = lineNum +",${outName},${outJrMrs},${outWid},${outSpouse},${outJob},${outWorkPlace},${outT},${outStSide},${outStNum},${outSt_changed},${outStName},${outRd},${outCity},${outLoc},${outMoved},${outDo}" + "\n"
                outCSVFile << out
            }

        }
println "DONE: Extracting all possible entries with addresses"