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
/*def Cities=[]
String  inFilename3= "List_of_all_Cities.txt"
File inFile3 = new File(inFilename3)
def line5 = inFile3.eachLine { line5, lineNum ->
    Cities.add(line5.trim());
}
Cities=(Cities.unique())

def Streets=[]
String  inFilename4= "List_of_all_Streets.txt"
File inFile4 = new File(inFilename4)
def line4 = inFile4.eachLine { line4, lineNum ->
    Streets.add(line4.trim());
}
Streets=(Streets.unique())*/
	    
args.each {
    String dir= "$it".split('_').getAt(0.."$it".split('_').size()-2)
    dir=dir.replace("[","")
    dir=dir.replace("]","")
    dir=dir.replace(", ","_")
    inPath = "../$dir/InputFiles/" // can use relative address
    outPath = "../$dir/OutputFiles/"
    inFilename = "Name_and_Address-from-"+"$it"+".csv"
    outFilename= "Name_and_Address-from-"+"$it"+"-final.csv"
}
def Cities=[]
        String  inFilename2= "List_of_all_Cities.txt"
        File inFile2 = new File(inFilename2)
        def line9 = inFile2.eachLine { line1, lineNum ->
            Cities.add(line1.trim());
        }
        Cities=(Cities.unique())


        def Streets=[]
        String  inFilename3= "List_of_all_Streets.txt"

        File inFile3 = new File(outPath+inFilename3)
        if(inFile3.exists()) {
        def line8 = inFile3.eachLine { line, lineNum ->
        Streets.add(line.trim());
        }
	}
       
	String  inFilename6= "List_of_extra_Cities.txt"

        File inFile6 = new File(inFilename6)
        if(inFile6.exists()) {
        def line6 = inFile6.eachLine { line, lineNum ->
        Streets.add(line.trim());
        }
	}
File inFile = new File(outPath + inFilename)
inFilename1="OutBuilding_With_Address.txt"
File inFile1 = new File(inFilename1)
File outFile = new File(outPath + outFilename)

outFile.delete()

String csvHeader ="outLineNum,outFirstName, outLastName, outMI, outJrMr, outSpouse, outJob, outJobLocation, outT, outStreetSide, outCor, outStNum,outStName,outRelNum,outRelDirection,outOfAnd,outRelStreet,outCity,outBuilding"
        outFile << csvHeader + "\n"


def line2 = inFile.eachLine { line, lineNum ->
	    
	    String outStNum= null
            String outStName=null
            String outCity=null
            String outBuilding1=null
            String outJob= null
            String outStreetSide=null
            String outCor=null
            String outRelNum=null
            String outRelDirection=null
            String outOfAnd=null
            String outRelStreet=null
          
        String []line_parts= line.split(',');
        String outBuilding=line_parts[line_parts.length-1].trim()
        if (!("${outBuilding}".trim().equals("null")))
            {
               // println(line)
                int buildingfound=0;
                String buildingaddress;
                def line3 = inFile1.eachLine { line1, lineNum1 ->
                    String []line_parts1= line1.split(',');
                    if(line_parts1[0].trim().equals(outBuilding))
                    {
                        buildingfound=1
                        buildingaddress=line1;
                    }
                }
                if(buildingfound==1)
                {
                    String[] address_parts= buildingaddress.split(',')
                    String toadd= line.split(',').getAt(0..8);
                    toadd=toadd.replace("[","")
                    toadd=toadd.replace("]","")

                    String add_part=address_parts[1].trim();
                   
		
		m = add_part =~ /^([0-9]+) ([0-9A-Za-z\s]+)/
                    if (m) {
                        
                        outStNum = "${m[0][1]}"
                        outStName= "${m[0][2]}"
                      }

		m = add_part =~ /^([A-Z][A-Za-z\s]+)/

                    if(m) {
                        if (Cities.contains(m[0][1])) {
                            outCity= "${m[0][1]}"
                            }
                        else if (Streets.contains(m[0][1])) {
                            outStName= "${m[0][1]}"
                         }
		}

		if(add_part.startsWith("ws")||add_part.startsWith("es")||add_part.startsWith("ne")||add_part.startsWith("ns")||add_part.startsWith("ss")||add_part.startsWith("sw")||add_part.startsWith("se"))
                        {
                            adr=add_part;
                            outStreetSide=adr.split(" ")[0]
                            adr=adr.replace(outStreetSide,"").trim();
                            if(adr.split(" ")[0].equals("cor"))
                            {
                                outCor=adr.split(" ")[0]
                                adr=adr.replace(outStreetSide,"").trim();
                            }
                            m1 = adr =~ /^([A-Za-z\s]+)/
                            if(m1)
                            {
                                outStName="${m1[0][1]}"
                                adr=adr.replace(outStName,"").trim();
                            }
                            m1 = adr =~ /^([0-9\s]+)/
                            if(m1)
                            {
                                outRelNum="${m1[0][1]}"
                                adr=adr.replace(outRelNum,"").trim();
                            }
                            m1 = adr =~ /^([e|w|s|n])/
                            if(m1)
                            {
                                outRelDirection="${m1[0][1]}"
                                adr=adr.replace(outRelDirection,"").trim();
                            }
                            m1 = adr =~ /^(of|and)/
                            if(m1)
                            {
                                outOfAnd="${m1[0][1]}"
                                adr=adr.replace(outOfAnd,"").trim();
                            }
                            m1 = adr =~ /^([A-Za-z\s]+)/
                            if(m1)
                            {
                                outRelStreet="${m1[0][1]}"
                                adr=adr.replace(outRelStreet,"").trim();
                            }
                         }
                        else if(add_part.startsWith("cor"))
                        {

                            adr=add_part;
                            outCor=adr.split(" ")[0]
                            adr=adr.replace(outCor,"").trim();
                            if(adr.contains("and")) {
                                outStName = adr.split("and")[0].trim()
                                outRelStreet = adr.split("and")[1].trim()
                                outOfAnd = "and"
                            }
                            else
                                outStName=adr;
                            }
toadd= toadd+ ", ${outStreetSide},${outCor},${outStNum},${outStName},${outRelNum},${outRelDirection},${outOfAnd},${outRelStreet},${outCity},${outBuilding1}"

//println(toadd)
                   outFile<<toadd+"\n"
                }

            }
            else
            {
            outFile<<line+"\n"
        }
    }
inFile.delete()
println "DONE: Adding Address to Entries having outBuilding"
