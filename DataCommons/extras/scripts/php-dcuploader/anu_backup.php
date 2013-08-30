<?php

/*
 * Laurent Dousset, 2013, laurent.dousset@pacific-credo.fr, Marseilles, France
 * You may freely use and distribute this file.
 * Please keep this notice if you do so.
 * This script comes with no warranty and you are solely responsible for its use and
 * the consequences of it.
 * ---------------------------------------------------------
 * THIS SCRIPT HAS BEEN UNPACKED AND UNTEMPLATED FOR CLARITY
 * It does not use fopen since this function is on many hosts deactivated.
 * But it needs libcurl and it needs PHP to be compiled --with-curl 
 * If cURL is not available on your host, change host!!
 *
 * This script backs up an UTF-8 text file from localhost to ANU commons
 * It checks for user authorization, then reads the content
 * of the file to be posted into a variable, and ends by 
 * creating the cURL post and displaying the result
 * 
 * NOTE: Script should work with PHP4, but have only tested on PHP5
 * NOTE: make sure PHP has enough memory allocation if you want to transfer huge files
 * NOTE: all echos can be commented out, of course...
 * NOTE: adapt the user verification bit to your framework
 * NOTE: to use this script: url to the script file and add as variable
 * filename=aboslutePathToYourFileThatNeedsToBeBackedUp
 * For example
 * http://something.com/anu_backup.php?filename=/var/www/backups/my_backup_filename.txt
 
 * NOTE: all that is between [ ] must be replaced with your personal data (delete the [ ] )
 *
 */


// start with user authtentication procedures
// needs to be adapted to your application
include("../classes/class_user.php");
$the_User = new User;
$the_User->checked = false;
$the_User->check_cookie();
if (!$the_User->checked) { $the_User->check_admin(); 
} else {
    // User is authorized, we can proceede
     
    // get the ABSOLUTE path + filename of the content to be backed up
    // and read the content into variable
    $filename = $_REQUEST['filename'];
    // check if file exists and if it is readable
    if (file_exists($filename))
    {
        if (!$handle = fopen($filename, 'r')) 
        {
            print "Cannot open file ($filename)";
            exit;     
        } else {       
            // file_get_contents does not need fopen and fclose.
            // But since we have opened the file to check if it is 
            // readable, we also need to reclose it here
            fclose($handle);
            // initialise fileData variable
            $fileData = '';
            // read content. Needs at least PHP 4.3.0
            $fileData = file_get_contents($filename);
            
            // uncomment the echo below if you want to see what has been read into the variable
            // echo $fileData;           
            
            echo "File has been read ok<br>";
                        
            // calls the below function to post the content
            // creates the cURL object
            post_content($fileData, basename($filename));
        }
    }  
}

//***************************************************//
// to be confirmed, but it may be that this function has elements that need PHP5
function post_content($fileData,$fileBaseName)
{
    $url = "https://[replace this with the URL you got from ANU]/$fileBaseName";
    $filesize = strlen($fileData); // Number of bytes in variable
    $md5Hash = md5($fileData);

	// create the header of the post. Adapt to your situation    
    $header = array(
        "Content-type: application/octet-stream",
        "Accept: text/plain",
        "Content-MD5: $md5Hash",
        "User-Agent: [replace with something you want]",
        "Content-Length: $filesize",
        "X-Auth-Token: [replace with your Token]"
        );
    
    // we create a virtual contents file in memory for the body of the post
    $fh = fopen('php://memory','rw');
    fwrite( $fh, $fileData);
    rewind($fh);
    
    // Initiate cURL
    $ch = curl_init();

    echo "cURL has been initiated<br>";
    curl_setopt($ch, CURLOPT_URL, $url);
    //curl_setopt($ch, CURLOPT_VERBOSE, True);
    curl_setopt($ch, CURLOPT_POST, True);
    curl_setopt($ch, CURLOPT_HEADER, True);
    curl_setopt($ch, CURLINFO_HEADER_OUT, True);
    curl_setopt($ch, CURLOPT_RETURNTRANSFER, True);
    // we do not bother checking ANU's certificate
    curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, False);
    curl_setopt($ch, CURLOPT_HTTPHEADER,$header);
    curl_setopt($ch, CURLOPT_INFILE, $fh);
    curl_setopt($ch, CURLOPT_INFILESIZE, $filesize);
    
    //execute post
    $result = curl_exec($ch);
    
    // print some info on what we sent -- can be commented out
    $info = curl_getinfo($ch);
    foreach ($info as $key => $value) {
        echo "$key : $value<br>";
    }
    
    //close cURL connection
    curl_close($ch);
    
    // Print info and server response -- can be commented out
    $serverResponse = substr($result, -$info['download_content_length']);
    echo $serverResponse;
}


?>
