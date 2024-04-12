/**
 * Parse_Entries_having_Person_Residing_With_and_Append_Address.groovy
 *
 * Created by Ankitha Pille on 01/03/2016
 * This script is for parsing entries having person residing with
 * eg : Abramson John r Simon Abramson
 * It also append adress to the entries
 */

//Input and Output Files
String inPath=""
String outPath= "";
String inFilename="";
String inFile3="";
String outCSVFilename=""
String outRejectFilename=""

args.each {
    String dir= "$it".split('_').getAt(0.."$it".split('_').size()-2)
    dir=dir.replace("[","")
    dir=dir.replace("]","")
    dir=dir.replace(", ","_")
    inPath = "../$dir/InputFiles/"
    outPath = "../$dir/OutputFiles/"
    inFilename = "reject-from-"+"$it"+"-Cleaned.txt"
    inFile3 = "Name_and_Address-from-"+"$it"+"_with_Dos.csv"
    outCSVFilename = "nameliveswith-from-"+"$it"+"-Cleaned.csv"
    outRejectFilename = "reject-from-"+"$it"+"-rejected.txt"
}

    File inFile = new File(outPath + inFilename)
    File inF4 = new File(outPath + inFile3)
    File outCSVFile = new File(outPath + outCSVFilename)
    File outRejectFile = new File(outPath + outRejectFilename)
    outCSVFile.delete()
    outRejectFile.delete()

    // Pattern for capital words
    String capW = "[A-Z][a-z]+"
    String LastName = "[A-Z01][a-z01]*(?:[A-Z01][a-z01]+)?"//should allow for names to have 2 capital characters (McDonald) can have 1 or 2 caps
    String capIorW = "[A-Z01][a-z01]*"

    def line1 = inFile.eachLine { line, lineNum ->
        String name="";
        String savedLine = line // Saving the original line to write to reject file
        def m // Reference for the matcher
        boolean hasName = false
        boolean hasAdd = false // there a address
        // Used for writing to csv file
        String outName = "" // Name output for the csv
        String outJrMrs = ""
        String outLivesWith=""
        def outNull = null // used to write null, probably can just write it
        m = line =~ /(\d+) ADD: /
        if (m) {
            lineNum=m[0][1]
            line = m.replaceFirst("")
        }
        m = line =~ /(\d+ NAME: )/
        if (m) {
            lineNum=m[0][1]
            line = m.replaceFirst("")
        }
        m = line =~ "^(${LastName}) (${LastName})( ${capIorW})?( (Mrs)|(jr))?"
        if(m) {
            outName = "${m[0][1]},${m[0][2]},${m[0][3]}"
            outJrMrs = "${m[0][4]}"
            outName  = (outName =~ /0/).replaceAll('O')
            outName = (outName =~ /1/).replaceAll('l')
            hasName = true
            line = m.replaceFirst('')
        }
        if(hasName) {
            m = line =~ / [r|h] (((Mrs)|(jr) )?(${capIorW} )?(${LastName} )(${LastName}))/
            if (m) {
                    outLivesWith = "${m[0][1]}";
                    outLivesWith  = (outLivesWith =~ /0/).replaceAll('O')
                    outLivesWith = (outLivesWith =~ /1/).replaceAll('l')
                    outLivesWith  = (outLivesWith =~ /Mrs/).replaceAll('')
                    String [] n_parts =outLivesWith.trim().split(' ');
                    if(n_parts.length==2) {
                        for (int i = n_parts.length - 1; i >= 0; i--)
                            name = name + " " + n_parts[i];
                    }
                    if(n_parts.length==3) {
                        name = name +" " +n_parts[2]+" "+n_parts[0]+" "+n_parts[1];
                    }
                    name=name.trim();
                    boolean found=false;
                    def line3= inF4.eachLine{ line1, lineNum1 ->
                        String p_name = ""
                        if(n_parts.length==3) {
                            p_name = line1.split(',')[1]+" "+line1.split(',')[2]+" "+line1.split(',')[3]
                        }
                        else {
                            p_name=line1.split(',')[1]+" "+line1.split(',')[2]

                        }
                        if (p_name.trim().equals(name.trim()))
                        {
                            String out  // Reference for outputting
                            String outStSide = line1.split(',')[10]
                            String outT = line1.split(',')[9]
                            String outStNum = line1.split(',')[11]
                            String outStName = line1.split(',')[13]
                            String outMoved = line1.split(',')[17]
                            String outDo = line1.split(',')[18]
                            String outWid = ""
                            String outSpouse = ""
                            String outJob = ""
                            String outWorkPlace = ""
                            String outCity = line1.split(',')[15]
                            String outRd = line1.split(',')[14]
                            String outLoc = line1.split(',')[16]
                            String outSt_changed = line1.split(',')[12]
                            line = m.replaceFirst('')
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
                            m = line =~/([a-z][a-z\s]+)((\d+ )?[A-Z][a-zA-Z\s]+)?/
                            if(m){
                                outJob= "${m[0][1]}"
                                outWorkPlace="${m[0][2]}"
                            }
                            else
                            {
                                outJob="${outNull}"
                                outWorkPlace= "${outNull}"
                            }

                            found= true;
                            out = lineNum + ",${outName},${outJrMrs},${outWid},${outSpouse},${outJob},${outWorkPlace},${outT},${outStSide},${outStNum},${outSt_changed},${outStName},${outRd},${outCity},${outLoc},${outMoved},${outDo}" + "\n"
                            outCSVFile << out
                         }
                    }
                    if(!found)
                    {
                        m = savedLine =~ /(\d+) ADD: /
                        if (m) {
                            savedLine = m.replaceFirst("")
                        }
                        m = savedLine =~ /(\d+ NAME: )/
                        if (m) {
                            savedLine = m.replaceFirst("")
                        }
                        outRejectFile << lineNum + " RESIDING WITH PERSON NOT FOUND: " + savedLine + "\n"
                    }

            }
            else
            {
                outRejectFile << savedLine + "\n"
            }
        }
        else
        {
                outRejectFile << savedLine + "\n"
        }
    }

    if(outCSVFile.exists()) {
    def line4 = outCSVFile.eachLine { line3, lineNum3 ->
        inF4 << line3 + "\n"
    }
    }
inFile.delete()
outCSVFile.delete()
println "DONE: Parsing Person residing with some other person and appending address"