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
    inFilename = "reject-from-"+"$it"+"-cleaned.txt"
    outCSVFilename = "Name_and_Address-from-"+"$it"+".csv"
    outRejectFilename = "Reject-from-"+"$it"+".txt"
}

//Create job dictionary
    def Jobs=[]
    String  inFilename1= "List_of_Occupations.txt"
    File inFile1 = new File(inFilename1)
    def line2 = inFile1.eachLine { line, lineNum ->
    Jobs.add(line.trim());
    }

    File outCSVFile = new File(outPath + outCSVFilename)
    File inFile = new File(outPath + inFilename)
    File outRejectFile = new File(outPath + outRejectFilename)

    outRejectFile.delete();

    def line1 = inFile.eachLine { line, lineNum ->
         // Saving the original line to write to reject file
        def m // Reference for the matcher
        String out  // Reference for outputting
        String outLastName = null
        String outFirstName = null
        String outMI =null
        String outJrMrs = null
        String outT=null
        String outStNum= null
        String outStName=null
        String outCity=null
        String outJob= null
        String outJobLocation=null
        String outBuilding=null
        String outLineNum=null
        String outSpouse=null
        String outStreetSide=null
        String outCor=null
        String outRelNum=null
        String outRelDirection=null
        String outOfAnd=null
        String outRelStreet=null

        boolean hasName = false //there is a name
        boolean hasAdd = false // there is a address
        boolean hasTitle=false

        m = line =~ /(\d+) (NAME): /
        if(m)
	    {
            outReason= "${m[0][2]}"
            outLineNum="${m[0][1]}"
            line = m.replaceFirst('')
	    }
        
	    m = line =~ /(\d+) (ADD): /
        if(m)
        {
            outReason= "${m[0][2]}"
            outLineNum="${m[0][1]}"
            line = m.replaceFirst('')
	    }

        String savedLine = line

    	/*
            * EXTRACT NAME SECTION
            */

        //Split the line using ",". The first part is the name

        m = line =~ / Tel \d+/
        if(m) {
            line = m.replaceFirst('')
            line = (line =~ /,$/).replaceFirst("")

        }

        m = line =~ /\(([A-Za-z0-9,\s]*)\)/
        if (m) {
            if (!"${m[0][1]}".contains("aged"))

            {
                outSpouse = "${m[0][1]}"
            }
            line = (line =~ /\([A-Za-z0-9,\s]*\)/).replaceFirst("")

        }

        String name = line.split(',')[0]

        // Check for Mrs/jr and place it in outJrMrs Column.
        m = name =~ /(Mrs|Jr|Dr|HON|CAPT|Rev|Capt|Hon|Miss|jr)/
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

        //Avoid parsing a company- "Co", "Bros"

            //name should have at least 2 parts- FirstName and Last Name
            //name should not have more than 3 parts- if more than 3 parts exists it will be considered as an advertisement
        if(!name_parts[name_parts.length-1].equals("Co")&&!name_parts[name_parts.length-1].equals("CO")&&!name_parts[name_parts.length-1].equals("SON")&&!name_parts[name_parts.length-1].equals("Bros")&&!name_parts[name_parts.length-1].equals("BROS")) {

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

        //Remove name from the line for further parsing
        //If the line doesnot have a name, do nothing and write it to the reject file

        if(!hasName) {
            outRejectFile << outLineNum + " NAME: " + savedLine + "\n"
        }

        //If Line has a name, check for address
        // If the line has a res/resident of Houghton since <year> remove it.
        if(hasName) {
            line = (line =~ /${name}( )?, /).replaceFirst("")
            m = line =~ /a( )?(res|resident) (of Houghton )?(Co )?since \d+, /
            if(m) {
                line = m.replaceFirst('')
            }
         // Check if line has a Job and Job Location.
            if(line.size()!=0) {
                if(!line.contains(',')) {
                    for (int i = 0; i <Jobs.size(); i++) {
                        if(line.contains(Jobs[i]))
                        {
                            outJob=(Jobs[i])
                            outJob = outJob.replace(',', '')
                            line=(line.replace(Jobs[i],"").trim())
                            if(line.size()!=0 && !line.contains("res ")&&!line.contains("bds "))
                                outJobLocation=line.trim();
                            break;
                        }
                    }
                }
                else {
                    String[] job = line.split(',')
                    for (int i = 0; i < Jobs.size(); i++) {
                        if(line.contains(Jobs[i])&&line.indexOf(Jobs[i])==0)
                        {
                            outJob=(Jobs[i])
                            outJob = outJob.replace(',', '')
                            line=(line.replace(Jobs[i],"").trim())
                            line= line.replace(",","")
                            if(line.size()!=0 && !line.contains("res ")&&!line.contains("bds ")&&!line.contains("rms "))
                                outJobLocation=line.trim();

                            break;
                        }
                    }
                }
            }
            if(!outJobLocation.equals(null)) {
                out = outLineNum + ",${outFirstName},${outLastName},${outMI},${outJrMrs},${outSpouse},${outJob},${outJobLocation},${outT},${outStreetSide},${outCor},${outStNum},${outStName},${outRelNum},${outRelDirection},${outOfAnd},${outRelStreet},${outCity},${outBuilding}" + "\n"
                outCSVFile << out

            }
            else

            {
                if(outJob.equals(null))
                    outRejectFile<< outLineNum +" "+outReason+", JOB NOT FOUND: "+savedLine+"\n"
                else
                outRejectFile<< outLineNum+" "+outReason+", JOB LOCATION NOT FOUND: "+savedLine+"\n"

            }

        }

    }
    inFile.delete();
    println "DONE: Parsing entries having only job and work place"
