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
def Cities=[]
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
Streets=(Streets.unique())
args.each {
    String dir= "$it".split('_').getAt(0.."$it".split('_').size()-2)
    dir=dir.replace("[","")
    dir=dir.replace("]","")
    dir=dir.replace(", ","_")
    inPath = "../$dir/InputFiles/" // can use relative address
    outPath = "../$dir/OutputFiles/"
    inFilename = "Name_and_Address-from-"+"$it"+"_with_outBuilding.csv"
    outFilename= "Name_and_Address-from-"+"$it"+".csv"
}

File inFile = new File(outPath + inFilename)
inFilename1="OutBuilding_With_Address.txt"
File inFile1 = new File(inFilename1)
File outFile = new File(outPath + outFilename)

outFile.delete()

String csvHeader ="outLineNum,outFirstName, outLastName, outMI, outJrMr, outJob, outJobLocation, outT, outStNum, outStName, outCity, outBuilding"
outFile << csvHeader + "\n"


def line2 = inFile.eachLine { line, lineNum ->
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
                    String toadd= line.split(',').getAt(0..7);
                    toadd=toadd.replace("[","")
                    toadd=toadd.replace("]","")
                    String [] add_part=address_parts[address_parts.length-1].trim().split(' ');
                    String city_or_street=add_part.getAt(1..add_part.length-1);
                    city_or_street=city_or_street.replace("[","")
                    city_or_street=city_or_street.replace("]","")
                    city_or_street=city_or_street.replace(",","")
                    if(Streets.contains(city_or_street)) {
                        toadd = toadd + ',' + add_part[0] +
                                ',' + city_or_street +
                                ',' + line.split(',').getAt(10) + ",null";
                    }
                    else if(Cities.contains(city_or_street)) {
                        toadd = toadd + ',' + add_part[0] +
                                ',' + line.split(',').getAt(9)+','+city_or_street+
                                ",null";
                    }
                    toadd=toadd.replace("[","")
                    toadd=toadd.replace("]","")
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