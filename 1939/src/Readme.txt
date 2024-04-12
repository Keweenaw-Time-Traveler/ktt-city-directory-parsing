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

Script Description:

1) Step 1: Cleaning Ready.txt- Remove all the junk characters and extra spaces
2) Step 2: Remove Blank Lines: Remove all the empty lines from the cleaned input file
3) Step 3: Extract Name and Street from the input files: 
	a) Go through the cleaned input file and extract only name and street names. It will identify street name if they are preceded by and r/h and street number.(r <Street Number><Street Name>)
	b)It generates an CSV file with name "Name_StreetName-from-Trimountain_1930-Cleaned.csv" which is used to create street dictionary

4) Step 4: Run the script to parse all the entries having address 
 	a)Create street dictionary(using "Name_StreetName-from-Trimountain_1930-Cleaned.csv") and city dictionary(using the file "List_of_all_Cities.txt")
	b)Check if the line has Name - The name should have at least First Name and Last Name. Middle Initial and "Mrs/Jr" are optional. For the First Name only two capital letters are allowed.  
	c)If the line has a name, check if the line has a address
Address can be any of below format:
r/h<digits- Street Number><Street Name>
r/h<Street/City> - To verify this we use street/city dictionary
	d)The line will get rejected if it either does not have a name or a address

5) Step 5: Parse Entries which do not have address but have the name of the person with whom he/she resides with. 
Eg: John r Mary - It implies John resides with Mary and John will have the same address as Mary

	a) Go through the reject file and extracts all the entries matching pattern <person name1> r <person name2> 
	b) Find the address of <person name2> from the output CSV file, append it to <person name1> and write it to the output CSV
	c) The line will get rejected if <person name1> is not found in the output CSV

6) Step 6: Parse Entries which have only job and job location.

	a) Create job dictionary using  "List_of_Occupations.txt"  
	b) Go through the reject file and extracts all the entries matching pattern <person name><job- all small letters><job location- First capital letter followed by small letter or digits>
	c) Check if the extracted job is an valid job, i.e. if it is in the job dictionary. 
	d) Write the name, job and job location in the output CSV- Note that in this case the entires will not have address
	e) Lines can get rejected if the identified "job" is not in "List_of_Occupations.txt". In that case if it a valid occupation, we need to add it to the file "List_of_Occupations.txt" and re run the scripts.

7) Step 7: Append address for entries having h do or r do

	a) Go through the output CSV and find out entries having h do or r do (let it be line 1)  
	b) Also find the line preceding line1(let it be line2)  
	c) Append address of line2 to line1 and write it back to the output CSV     