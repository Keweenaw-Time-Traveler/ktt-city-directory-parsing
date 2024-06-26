/**
 * Main_Constuct_CSV.groovy
 * Edited by Ankitha Pille on 01/03/2016
 * This script is the Main Script that calls all other Scripts.
 *
 * "List_of_cities_for_which_code_need_to_be_run.txt" contains the list of cities for which the script needs to be run.
 * Eg Houghton,Hancock, Dollay_Bay
 *
 */

// List having the cities for which the scripts need to be run

inFile = new File("List_of_cities_for_which_code_need_to_be_run.txt")
def line1 = inFile.eachLine { line, lineNum ->
        println("Executing for : "+ line)
        def CityName = line + "_1908"
        run(new File("Clean_Ready_txt.groovy"), "$CityName".split())
        run(new File("Remove_empty_lines_from_cleaned.groovy"), "$CityName".split())
        run(new File("Extract_Street_Names.groovy"), "$CityName".split())
        run(new File("Parse_AllEntries_with_Address.groovy"), "$CityName".split())
        run(new File("Parse_Entries_having_Only_job_and_WorkLocation.groovy"), "$CityName".split())
        run(new File("Parse_bds_with_other_person.groovy"), "$CityName".split())
        run(new File("Adding_Address_to_Entries_with_outBuilding.groovy"), "$CityName".split())
}
