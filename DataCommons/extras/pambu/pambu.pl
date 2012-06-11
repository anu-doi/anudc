#!/usr/bin/perl
#
# pambu.pl
# 
# Australian National University Data Commons
#
# This script allows the loading of data from the pambu microfilm database in to the
# Data Commons
#
# Version	Date		Developer				Description
# 0.1		08/06/2012	Genevieve Turner (GT)	Initial

my $author_tmplt_id = [AUTH TMPLT ID];
my $coll_tmplt_id = [COLL TMPLT ID;
my $layout = "def:display";
my $matchstring = "<b>Identifier:<\/b> (([A-Za-z0-9]|-|\.)+:(([A-Za-z0-9])|-|\.|~|_|(%[0-9A-F]{2}))+)<br \/>";

my $author_file = "test_authors.txt";
my $collection_file = "test_titles.txt";

my %authorids = ();

open (OUTPUTFILE, '>output.txt');

my $group_id = "9";
my $curl = "C:/WorkSpace/Software/curl/curl-7.24.0-ssl-sspi-zlib-static-bin-w32/curl";
my $user = "-X POST --user user2:visitor";
my $auth_url = "http://[APP SERVER:PORT]/DataCommons/rest/display/new?layout=$layout&tmplt=$author_tmplt_id";
my $coll_url = "http://[APP SERVER:PORT]/DataCommons/rest/display/new?layout=$layout&tmplt=$coll_tmplt_id";
my $linkurl = "http://[APP SERVER:PORT]/DataCommons/rest/display/addLink?item=";

my $curlcmd = $curl.' '.$user.' '.$auth_url;

# Save the authors
open FILE, $author_file or die $!;

while (<FILE>) {
	print OUTPUTFILE "-----------------------------\n";
	my @author = split(/;/);
	
# Set the form fields
	my $data='--data "type=Party"';
	$data=$data.' --data "subType=author"';
	$data=$data.' --data "ownerGroup='.$group_id.'"';
	$data=$data.' --data "externalId='.@author[0].'"';
	$data=$data.' --data "name='.@author[1].'"';
	$data=$data.' --data "abbrName='.@author[2].'"';
	
	my $command = $curlcmd.' '.$data;
	print OUTPUTFILE $command."\n";
	my $return = `$command`;
	if ($return =~ m/$matchstring/i) {
		print OUTPUTFILE "Has Identifier: ".$1."\n";
		$authorids{@author[0]} = $1;
	}
	else {
		print OUTPUTFILE "Error saving document for @author[0]"."\n";
	}
}
close(FILE);

#!/usr/bin/perl

while (($key, $value) = each(%authorids)) {
	print "Key: ".$key.", Value: ".$value."\n";
}

$curlcmd = $curl.' '.$user.' '.$coll_url;

# Save the collections and add a link to the author
open FILE, $collection_file or die $!;

while (<FILE>) {
	print OUTPUTFILE "-----------------------------\n";
	my @collection = split(/;/);
	
# Set the form fields
	my $data='--data "type=Collection"';
	$data=$data.' --data "subType=collection"';
	$data=$data.' --data "ownerGroup='.$group_id.'"';
	$externalId = @collection[0];
	if ($externalId =~ m/^doc/i) {
		$data=$data.' --data "holdingType=doc"';
	}
	elsif ($externalId =~ m/^ms/i) {
		$data=$data.' --data "holdingType=ms"';
	}
	$data=$data.' --data "externalId='.@collection[0].'"';
	$data=$data.' --data "serialNum='.@collection[2].'"';
	$data=$data.' --data "name='.@collection[3].'"';
	$data=$data.' --data "dateText='.@collection[4].'"';
	$data=$data.' --data "numReels='.@collection[5].'"';
	$data=$data.' --data "format='.@collection[6].'"';
	$data=$data.' --data "holdingLocation='.@collection[7].'"';
	$data=$data.' --data "accessRights='.@collection[8].'"';
	$data=$data.' --data "briefDesc='.@collection[9].'"';
	$data=$data.' --data "fullDesc='.@collection[10].'"';
	$data=$data.' --data "sortVal='.@collection[11].'"';
	$data=$data.' --data "digital='.@collection[12].'"';
	$data=$data.' --data "email=pambu@anu.edu.au"';
	$data=$data.' --data "anzforSubject=210313"';
	
	my $command = $curlcmd.' '.$data;
	print OUTPUTFILE $command."\n";
	my $return = `$command`;
	if ($return =~ m/$matchstring/i) {
		print OUTPUTFILE "Has Identifier: ".$1."\n";
		my $author = @collection[1];
		print OUTPUTFILE "Collection Author: ".$author."\n";
		my $authoritem = $authorids{$author};
		print OUTPUTFILE "Author: ".$authoritem."\n";
		
		if($authoritem) {
			$linkdata = ' --data "linkType=isOutputOf"';
			$linkdata = $linkdata.' --data "itemId=info:fedora/'.$authoritem.'"';
			$linkcommand = $curl.' '.$user.' '.$linkurl.$1.$linkdata;
			$return = `$linkcommand`;
			print OUTPUTFILE $return."\n";
		}
	}
	else {
		print OUTPUTFILE "Error saving document for @collection[0]\n";
	}
}
close(FILE);

close(OUTPUTFILE);