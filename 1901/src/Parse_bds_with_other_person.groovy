/**
 * Extract_PersonName_And_Street.groovy
 *
 * Created by Ankitha Pille on 01/03/2016
 * This script is for parsing entries bds with some other person
 */


//Make the occupation Dictionary
def Jobs=[]
String  inFilename4= "List_of_Occupations.txt"
File inFile4 = new File(inFilename4)
def line4 = inFile4.eachLine { line4, lineNum ->
    Jobs.add(line4.trim());
}
Jobs=(Jobs.unique())

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
    inFilename = "Reject-from-" + "$it"+".txt"
    inFilename1= "Name_and_Address-from-"+"$it"+".csv"
    outRejectname="Reject-final-from-" + "$it"+".txt"
}

File inFile = new File(outPath + inFilename)
File inFile1 = new File(outPath + inFilename1)
File outReject = new File(outPath + outRejectname)
outReject.delete();

//Read from the reject file
def line1 = inFile.eachLine { line, lineNum ->
    String savedLine = line
    String name_in_output=""
    Boolean alreadyfound= false;
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
    String outStreetSide=null
    String outCor=null
    String outRelNum=null
    String outRelDirection=null
    String outOfAnd=null
    String outRelStreet=null

//if the last part of the line has "bds" and "rms"

    if(line.split(",")[line.split(",").length-1].startsWith(" bds")||line.split(",")[line.split(",").length-1].startsWith(" rms")||line.split(",")[line.split(",").length-2].startsWith(" bds")||line.split(",")[line.split(",").length-2].startsWith(" rms"))
    {
        String savedLine1=line;
        String outLineNum="";
        String outReason="";
        //Remove <line Number><Rejection reason> from the line
        m = line =~ /(\d+) (NAME): /
        if(m)
        {
            outReason= "${m[0][2]}"
            outLineNum="${m[0][1]}"
            savedLine1 = m.replaceFirst('')
        }

        m = line =~ /(\d+) (ADDRESS, JOB LOCATION NOT FOUND): /
        if(m)
        {
            outReason= "${m[0][2]}"
            outLineNum="${m[0][1]}"
            savedLine1 = m.replaceFirst('')
        }
        m = line =~ /(\d+) (ADDRESS, JOB NOT FOUND): /
        if(m)
        {
            outReason= "${m[0][2]}"
            outLineNum="${m[0][1]}"
            savedLine1 = m.replaceFirst('')
        }
        // Remove "bds" and "rms" from the line
String bds_With=""
if(line.split(",")[line.split(",").length-1].startsWith(" bds")||line.split(",")[line.split(",").length-1].startsWith(" rms"))
    bds_With=line.split(",")[line.split(",").length-1].trim()
else
    bds_With=line.split(",")[line.split(",").length-2].trim()

        bds_With= bds_With.replace("bds ","")
        bds_With= bds_With.replace("rms ","")
        //Remove the title
        m = bds_With =~ /^(St|Mrs|Jr|HON|CAPT|Dr|Rev|Capt|Hon|Miss|jr) /
        if(m) {
            bds_With = m.replaceFirst('')
        }
	m = bds_With =~ /\(([A-Za-z0-9,\s]*)\)/
        if(m) {
            bds_With = m.replaceFirst('');
        }
            bds_With = bds_With.trim();
   
        // Rearrange the names to match it with the output csv
        String l_name= (bds_With.split(' ')[bds_With.split(' ').length-1])
        bds_With=bds_With.replace(bds_With.split(' ')[bds_With.split(' ').length-1],"").trim()
        String name_to_match= l_name+ " "+ bds_With
        //Read from the output file
        def line2 = inFile1.eachLine { line2, lineNum2 ->
                String name_in_output2=""
                //If the person do not have a middle take only first two parts of the part
                if(line2.split(",")[3].equals("null")) {
                    name_in_output = line2.split(",")[1].trim() + " " + line2.split(",")[2].trim()
                    name_in_output2 = line2.split(",")[1].trim() + " " + line2.split(",")[2].trim().substring(0,1)
                   }
                    //If the person has middle take
                    //name_in_output= First_Name+ Last Name + Middle Initail
                    //name_in_output2= First_Name+ Initial of Last Name + Middle Initail
                else {
                    name_in_output = line2.split(",")[1].trim() + " " + line2.split(",")[2].trim() + " " + line2.split(",")[3].trim()
                    name_in_output2 = line2.split(",")[1].trim() + " " + line2.split(",")[2].trim().substring(0,1) + " " + line2.split(",")[3].trim().substring(0,1)
                }
            //match with both name_in_output and name_in_output2.
                if((name_in_output.equals(name_to_match)||name_in_output2.equals(name_to_match))&& alreadyfound==false) {

                    alreadyfound=true;
                String l_num=line.split(":")[0].split(' ')[0].trim()
                line=line.replace(line.split(":")[0]+": ","");
                String name= line.split(',')[0].trim()


                m = name =~ /\(([A-Za-z0-9,\s]*)\)/
                if (m) {
                        if (!"${m[0][1]}".contains("aged")) {
                            outSpouse = "${m[0][1]}"
                        }
                        name = (name =~ /\([A-Za-z0-9,\s]*\)/).replaceFirst("")
                        line = (line =~ /\([A-Za-z0-9,\s]*\)/).replaceFirst("")
                }
                m = name =~ /^(Mrs|Jr|HON|CAPT|Dr|Rev|Capt|Hon|Miss|jr) /
                if (m) {
                    outJrMrs = "${m[0][1]}"
                    name = (name =~ /${m[0][1]}\s?/).replaceFirst("")
                }
                line = (line =~ /${name}( )?, /).replaceFirst("")
                if(line.split(',')[line.split(',').length-1].trim().startsWith("bds")) {
                    line= line.replace(line.split(',')[line.split(',').length-1],"")
                    line = (line =~ /,$/).replaceFirst("")
                }

                if(line.size()!=0) {
                    if(!line.contains(',')) {
                        for (int i = 0; i <Jobs.size(); i++) {
                            if(line.contains(Jobs[i]))
                            {
                                outJob=(Jobs[i])
                                outJob = outJob.replace(',', '')
                                line=(line.replace(Jobs[i],"").trim())
                                if(line.size()!=0 && !line.contains("res")&&!line.contains("bds")&&!line.contains("rms"))
                                    outJobLocation=line.trim();
                                break;
                            }
                        }
                    }
                    else {
                        String[] job = line.split(',')
                        for (int i = 0; i < Jobs.size(); i++) {
                             if(line.contains(Jobs[i])&&line.indexOf(Jobs[i])==0) {
                                outJob=(Jobs[i])
                                outJob = outJob.replace(',', '')
                                line=(line.replace(Jobs[i],"").trim())
                                line= line.replace(",","")
                                if(line.size()!=0 && !line.contains("res")&&!line.contains("bds")&&!line.contains("rms"))
                                outJobLocation=line.trim();
                                break;
                             }
                        }
                    }
                }
              
                outFirstName=name.split(' ')[0]
	        if (name.split(' ').length==2)
                outLastName= name.split(' ')[1]
                if (name.split(' ').length==3)
                outMI= name.split(' ')[2]
                outT= line2.split(',')[8]
                outStreetSide= line2.split(',')[9]
                outCor=line2.split(',')[10]
                outStNum= line2.split(',')[11]
                outStName= line2.split(',')[12]
                outRelNum= line2.split(',')[13]
                outRelDirection= line2.split(',')[14]
                outOfAnd= line2.split(',')[15]
                outRelStreet= line2.split(',')[16]
                outCity= line2.split(',')[17]
                outBuilding= line2.split(',')[18]

                out = l_num + ",${outFirstName},${outLastName},${outMI},${outJrMrs},${outSpouse},${outJob},${outJobLocation},${outT},${outStreetSide},${outCor},${outStNum},${outStName},${outRelNum},${outRelDirection},${outOfAnd},${outRelStreet},${outCity},${outBuilding}" + "\n"
                inFile1<<out
        }
    }
    if (alreadyfound==false)
    {
    outReject<<outLineNum+" "+outReason+", PERSON NOT FOUND: "+savedLine1+"\n"
    }
    }
    else

    outReject<<savedLine+"\n"

}
inFile.delete()
println "DONE: Extracting Person Names and Streets"
