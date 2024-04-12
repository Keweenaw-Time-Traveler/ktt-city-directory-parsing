/**
 * Clean_Ready_txt.groovy
 *
 * Created by Ankitha Pille on 01/03/2016
 * This script is for parsing entries having only job and work location
 *
 */

//Input and Output
String outPath= "";
String inFilename="";
String outFilename="";
String outCSVFilename=""
String outRejectFilename=""
args.each {
    String dir= "$it".split('_').getAt(0.."$it".split('_').size()-2)
    dir=dir.replace("[","")
    dir=dir.replace("]","")
    dir=dir.replace(", ","_")
    outPath = "../$dir/OutputFiles/"
    inFilename = "reject-from-"+"$it"+"-rejected.txt"
    outCSVFilename = "Name_and_Address-from-"+"$it"+"_with_Dos.csv"
    outRejectFilename = "Reject-from-"+"$it"+".txt"
}

//Create job dictionary
def jobs=[]
String  inFilename1= "List_of_Occupations.txt"
File inFile1 = new File(inFilename1)
def line2 = inFile1.eachLine { line, lineNum ->
    jobs.add(line.trim());
}

File outCSVFile = new File(outPath + outCSVFilename)
File inFile = new File(outPath + inFilename)
File outRejectFile = new File(outPath + outRejectFilename)

String capW = "[A-Z][a-z]+"
String LastName = "[A-Z01][a-z]*[A-Z]?[a-z01]+"//should allow for names to have 2 capital characters (McDonald) can have 1 or 2 caps
String FirstName = "[A-Z01][a-z01]*(?:[A-Z01][a-z01]+)?"//should allow for names to have 2 capital characters (McDonald) can have 1 or 2 caps
String capIorW = "[A-Z01][a-z01]*"


    outRejectFile.delete()
    def line1 = inFile.eachLine { line, lineNum ->
        String savedLine = line // Saving the original line to write to reject file
        def m // Reference for the matcher
        String out  // Reference for outputting
        String outName = ""
        String outStSide = ""
        String outT = ""
        String outStNum = ""
        String outStName = ""
        String outMoved = ""
        String outDo = ""
        String outWid = ""
        String outSpouse = ""
        String outJob = ""
        String outWorkPlace = ""
        String outCity = ""
        String outRd = ""
        String outLoc = ""
        String outSt_changed = ""
        String outJrMrs = ""

        def outNull = null // used to write null, probably can just write it
        // Used for flow control and writing to reject file
        boolean hasName = false
        m = line =~ /(\d+) ADD: /
        if (m) {
            lineNum = m[0][1]
            line = m.replaceFirst("")
         }
        m = line =~ /(\d+ NAME: )/
        if (m) {
            lineNum = m[0][1]
            line = m.replaceFirst("")
        }
        m=line=~/(Mrs|jr) /
        if(m) {
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
            outName  = (outName =~ /0/).replaceAll('O')
            outName = (outName =~ /1/).replaceAll('l')
            hasName = true
            line = m.replaceFirst('')
        }
        else
        {
        outRejectFile <<  "${savedLine}"+ "\n"
        }
        if (hasName) {
            m1 = line =~/\((?:(wid) )?([\w\s]+)\)/
            if (m1)
            {

                outWid="${m1[0][1]}";
                outSpouse= "${m1[0][2]}"
                line = m1.replaceFirst("")
            }
            else
            {
                outWid="${outNull}"
                outSpouse= "${outNull}"
            }
            m = line =~ /\b([a-z][a-z\s]+)((\d+ )?[A-Z0-9][a-zA-Z0-9&\s]+)/
            if (m) {
              outDo = "${outNull}"
              outMoved = "${outNull}"
              outRd = "${outNull}"
              outLoc = "${outNull}"
              outT = "${outNull}";
              outStSide = "${outNull}";
              outStNum = "${outNull}";
              outSt_changed = "${outNull}";
              outStName = "${outNull}";
              outCity = "${outNull}"
              outJob = "${m[0][1]}"
              outWorkPlace = "${m[0][2]}"
              if(jobs.contains("${outJob}".trim()))
              {
                  out = lineNum + ",${outName},${outJrMrs},${outWid},${outSpouse},${outJob},${outWorkPlace},${outT},${outStSide},${outStNum},${outSt_changed},${outStName},${outRd},${outCity},${outLoc},${outMoved},${outDo}" + "\n"
                  outCSVFile << out
              }
              else
              {
                  outRejectFile <<  "${savedLine}"+ "\n"
              }
            }
            else
            {
              outRejectFile <<  "${savedLine}"+ "\n"
            }

        }
    }
inFile.delete()
println "DONE: Parsing entries having only job and work place"