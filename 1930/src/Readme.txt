Only Step

Run Main_Construct_CSV.groovy

This script will in turn call the below scripts one by one

	1. Clean_Ready_txt.groovy - For cleaning Ready.txt
	2. Remove_empty_lines_from_cleaned.groovy - Removing blank lines for the cleaning txt
	3. Extract_PersonName_And_Street.groovy - Extract person Name and Street from the Input File
	4. Parse_AllEntries_with_Address.groovy - Parsing all the entries having address
	5. Parse_Entries_having_Person_Residing_With_and_Append_Address.groovy - Parsing all the entries having person and residing with and append address to it
	6. Parse_Entries_having_Only_job_and_WorkLocation.groovy - Parsing entries having only job and work location
	7. Adding_Address_to_Entries_with_h_or_r dos.groovy- Adding address to all the entries having h do ans r do

Files Used:

	1. List_of_all_Cities.txt - List of cities to create city dictionary
	2. List_of_cities_for_which_code_need_to_be_run.txt - List of cities for which the scripts need to be run
	3. List_of_Occupations.txt - List of all jobs

    
Input 
Trimountain_1930_Ready.txt - Generating from PDF
Trimountain_1930_Cleaned.txt - Generating from cleaning <cityname_yearname>_Ready.txt


Outputs:

Name_and_Address-from-Trimountain_1930.csv - Output CSV File
Name_StreetName-from-Trimountain_1930-Cleaned.csv - CSV file containing person name and street
Reject-from-Trimountain_1930.txt- Reject File