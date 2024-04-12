Only Step

Run Main_Construct_CSV.groovy

This script will in turn call the below scripts one by one

	1. Clean_Ready_txt.groovy - For cleaning Ready.txt
	2. Remove_empty_lines_from_cleaned.groovy - Removing blank lines for the cleaning txt
	3. Extract_Street_Names.groovy - Extract Streets from the Input File
	4. Parse_AllEntries_with_Address.groovy - Parsing all the entries having address
	5. Parse_Entries_having_Only_job_and_WorkLocation.groovy - Parsing entries having only job and work location
	6. Parse_bds_with_other_person.groovy- Parsing all the entries having person and bds with and append address to it
	6. Adding_Address_to_Entries_with_outBuilding.groovy- Adding address to all the entries having outBuilding

Files Used:

	1. List_of_all_Cities.txt - List of cities to create city dictionary
	2. List_of_cities_for_which_code_need_to_be_run.txt - List of cities for which the scripts need to be run
	3. List_of_Occupations.txt - List of all jobs

    
Input 
Trimountain_1908_Ready.txt - Generating from PDF
Trimountain_1908_Cleaned.txt - Generating from cleaning <cityname_yearname>_Ready.txt


Outputs:

Name_and_Address-from-Lake_Linden_1908-final.csv - Output CSV File
List_of_all_Streets.txt - file containing streets
Reject-final-from-Lake_Linden_1908.txt- Reject File
