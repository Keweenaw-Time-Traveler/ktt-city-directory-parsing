/**
 * Clean_Ready_txt.groovy
 *
 * Created by Robert Pastel on 9/9/2016.
 * Created by Ankitha Pille on 01/03/2016
 * This script is for cleaning ready.txt by Removing extra spaces and junk characters
 *
 */

//Input and output Files

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
    inFilename = "$it"+"_Ready.txt"
    outFilename= "$it"+"_Cleaned-with empty lines.txt"
}
File inFile = new File(inPath + inFilename)
File outFile = new File(inPath + outFilename)
outFile.delete()

/**
 * Define the alphabet, meaning the group of character we care about
 */
String alphabet = "A-Za-z 0-9)(-" // A through Z, spaces (no taps), digits, and parentheses (no escaping)
// Needed to add hyphens. Note "-" needs to either at the end or beginning.
def alphaPattern = /[^A-Za-z 0-9\)\(-]/ // This works too, but have to escape parentheses

// Note that "[^" negates the group, ie anything but this a group.
/**
 * We throw out periods, commas, etc even though they do exist in the txt file, but we do not need them.
 * We need to keep hyphens at the end of the line for line continuation.
 * But there is a problem with hyphens appearing appearing at the end of words
 * Other directories might want to keep some  punctuations.
 */
/**
 * I expect that the complete file can be kept in memory.
 *
 * List of clean lines
 */
List cleanLines = [] as Queue
/**
 * Symbol representing line continuation, ie indent.
 */
String indentSymbol = "XXXX"

/**
 * The script goes through three phases to make an entry.
 *  1. Basic clean, which includes removing all wanted characters.
 *  2. Fixing obvious miss reading, in this case the letter "l" for the number "1"
 *  3. Construct the entry from multiple indented lines.
 *
 *  "Wanted characters" are defined in the string variable, alphabet.
 *  It is important to enumerate only the characters needed to parse
 *  an entry.
 */
// Clean the txt and put in a list
inFile.eachLine{line, lineNum ->
    /**
     * BASIC CLEANING
     */
    /**
     *  Clean the line of characters not wanted
     *
     * I got the idea from:
     *
     * http://programminghistorian.org/lessons/cleaning-ocrd-text-with-regular-expressions
     *
     * Another good articles for beginners to regex is
     *
     * http://programminghistorian.org/lessons/understanding-regular-expressions
     *
     * but only uses a text editor for cleaning text, not programming.
     * Using a text editor to test patterns is handy.
     */
    def matches =  line =~ "[^(${alphabet})|^,|^-|^/|^*]"
    line = matches.replaceAll('') // matcher has a replaceAll
    /**
     * Clean up spaces
     */
    // Clean leading spaces
    matches =  line =~ /^\s+/
    line = matches.replaceAll('')
    // Clean trailing spaces
    matches =  line =~ /\s+$/
    line = matches.replaceAll('')
    // Clean multiple spaces
    matches =  line =~ /\s+/
    line = matches.replaceAll(' ') // replace with a single space
    /**
     * FIX OBVIOUS MISS READING
     *
     * This section should be expanded as we notice common mistakes
     * that the OCR makes. For example, the script could replace JJ with H.
     * Sometimes i and l are confused. I do not know how to fix that without
     * a dictionary.
     */
    /**
     * Clean up dangling hyphens
     * do them separately because a match will capture a space that could've matched with another hyphen
     */
    matches =  line =~ /\b- /
    line = matches.replaceAll(' ') // need to add the space back in
    matches = line =~ / -\b/
    line = matches.replaceAll(' ')
    matches =  line =~ / - /
    line = matches.replaceAll(' ')
    matches =  line =~ /-/
    line = matches.replaceAll(' ')

    /**
<<<<<<< HEAD:Laurium-1930/Laurium-1930-project/src/entryConstruct.groovy
     * Replace letter 'l' with number '1'
     *
     *  Complicated if there are multiple occurrences.
     *  First find the matches and then iterate
     *  using the match to create a new pattern to find and replace.
     *
     *  http://naleid.com/blog/2008/05/19/dont-fear-the-regexp
     *
     *  See the bottom of the blog
     *
     *  Another fix would be to remove the number 1 from the middle of a word.
     *  We do not make that cleaning because this directory does make use of
     *  numbers in words.
     */
    /** Edit Gary Spikberg: 10/26
    *As this code is, it will not handle any errors of hill when it should be h111
     *It will also fix instances of " ri " ## and " hi " ## but will not catch if it records the ## as ll or OO
     */
    // Digit on the right
    matches  =  line =~ /\sbds i\s(\d)/
    matches.each{m, d -> line = (line =~ "${m}").replaceFirst(" bds 1"+d) }
    matches  =  line =~ /\sres i\s(\d)/
    matches.each{m, d -> line = (line =~ "${m}").replaceFirst(" res 1"+d) }
    matches  =  line =~ /bds l(\d|O|l)/
    matches.each{m, d -> line = (line =~ "${m}").replaceFirst("bds 1"+d) }
    matches  =  line =~ /res l(\d|O|l)/
    matches.each{m, d -> line = (line =~ "${m}").replaceFirst("res 1"+d) }
    // Digit on the left
    matches  =  line =~ /res i(\d|OO|Ol|ll)/
    matches.each{m, d -> line = (line =~ "${m}").replaceFirst("res 1"+d) }
    matches  =  line =~ /bds i(\d|OO|Ol)/
    matches.each{m, d -> line = (line =~ "${m}").replaceFirst("bds 1"+d) }
    // Digit on the left
    /*matches  = line  =~ /(\d)i/
    matches.each{m, d -> line = (line =~ "${m}").replaceFirst(d+"1")}

    matches  =  line =~ /O(\d|O|l)/
    matches.each{m, d -> line = (line =~ "${m}").replaceFirst("0"+d) }
    // Digit on the left
    matches  = line  =~ /(\d)l/
    matches.each{m, d -> line = (line =~ "${m}").replaceFirst(d+"1") }
    matches  = line  =~ /(\d)l/
    matches.each{m, d -> line = (line =~ "${m}").replaceFirst(d+"1") }
    matches  = line  =~ /(\d)l/
    matches.each{m, d -> line = (line =~ "${m}").replaceFirst(d+"1") }
    matches  = line  =~ /(\d)O/
    matches.each{m, d -> line = (line =~ "${m}").replaceFirst(d+"0") }
    matches  = line  =~ /(\d)O/
    matches.each{m, d -> line = (line =~ "${m}").replaceFirst(d+"0") }
*/


   // matches = line =~ /lOO/
    //matches.each{m, d-> line = (line =~ "${m}").replaceFirst("100")}
    /**
     * Add to the list of clean lines.
     */
//    String outLine = wLine
 /*   println line
=======
     * Replace letter 'l' with number '1' in the address
     * Replace letter 'O' with number '0' in the address
     * Replace letter 'Y2' with '1/2' in the address
    */

     m = line =~ /\b(res|bds) ([\d]+[Y|y|i|l|I|j|J|O|Q|V|G|o|k|f|e|S|V|^|*|\/]*[\d]*) /
    if(m) {
        String outSt="${m[0][2]}"
        outSt  = (outSt =~ /\s/).replaceAll('')
        outSt  = (outSt =~ /Y2/).replaceAll("1/2")
        outSt  = (outSt =~ /S/).replaceAll('5')
        outSt = (outSt =~ /y2/).replaceAll('1/2')
        outSt  = (outSt =~ /i/).replaceAll('1')
        outSt = (outSt=~ /l/).replaceAll('1')
        outSt  = (outSt =~ /I/).replaceAll('1')
        outSt  = (outSt =~ /D/).replaceAll('0')
        outSt = (outSt =~ /O/).replaceAll('0')
        outSt = (outSt =~ /o/).replaceAll('0')
        outSt  = (outSt =~ /Q/).replaceAll('0')
        outSt = (outSt =~ /V2/).replaceAll('1/2')
        outSt  = (outSt =~ /G/).replaceAll('0')
        outSt  = (outSt =~ /Vk/).replaceAll('1/2')
        outSt  = (outSt =~ /Vfe/).replaceAll('1/2')
        outSt  = (outSt =~ /Vfc/).replaceAll('1/2')
        outSt  = (outSt =~ /\*4/).replaceAll('1/2')
        outSt  = (outSt =~ /\^4/).replaceAll('1/2')
        outSt  = (outSt =~ /\^2/).replaceAll('1/2')
        outSt  = (outSt =~ /j\^/).replaceAll('1/2')
        outSt  = (outSt =~ /J\^/).replaceAll('1/2')
        outSt  = (outSt =~ /j4/).replaceAll('1/2')
        outSt  = (outSt =~ /J4/).replaceAll('1/2')

        outSt  = (outSt =~ /1\/2/).replaceAll(' 1/2')
        line= ( line=~ "${m[0][1]} ${m[0][2]}").replaceFirst("${m[0][1]} ${outSt}")
        line= line.replace("${m[0][1]} ${m[0][2]}","${m[0][1]} ${outSt}")
    }
    cleanLines << line
}
//println cleanLines
/**
 * CONSTRUCT ENTRY
 *
 * The entry is constructed by contenating lines that are
 * marked with indent symbol. Consequently it needs to
 * look at the next line. It does this by using a Queue.
 */
while(!cleanLines.empty){
    // Groovy does not have a bottom checking loop. See
    // https://www.rosettacode.org/wiki/Loops/Do-while#Groovy
    String entry = cleanLines.poll()
    while(true){
        String nextLine = cleanLines.peek() // peek for the test
        def matchIndent =  nextLine =~ "^${indentSymbol}" // the test for the indent symbol
        if (matchIndent){
            cleanLines.poll() // Match, so remove it from the list and throw away
            String continueEntry = matchIndent.replaceFirst('') //remove the indent symbol

            // If hyphen at the end of the entry then remove it and don't add next line with a space
            def mHyphen =  entry =~ /-$/
            if(mHyphen){
                entry = mHyphen.replaceFirst('') // remove the hyphen
                entry += continueEntry // add without space
            }
            else{
                entry += ' '+continueEntry //add with space
            }
        }
        else { // No indent symbol, write to file
            outFile << entry+'\n'
            break
        }
    } // end infinite loop

}
println "DONE: Cleaning ready.txt"