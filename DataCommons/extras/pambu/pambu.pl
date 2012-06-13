#!/usr/bin/perl

my $author_tmplt_id = "tmplt:6";
my $coll_tmplt_id = "tmplt:7";
my $layout = "def:display";
my $matchstring = "<b>Identifier:<\/b> (([A-Za-z0-9]|-|\.)+:(([A-Za-z0-9])|-|\.|~|_|(%[0-9A-F]{2}))+)<br \/>";

my $layout = "def:display";

my $author_file = "test_authors_2.txt";
my $collection_file = "test_titles_2.txt";

my %authorids = ();

open (OUTPUTFILE, '>output.txt');

my $group_id = "9";
my $curl = "C:/WorkSpace/Software/curl/curl-7.24.0-ssl-sspi-zlib-static-bin-w32/curl";
my $user = "-X POST --user user2:visitor";
my $auth_url = "http://67h5p1s.uds.anu.edu.au:9081/DataCommons/rest/display/new?layout=$layout&tmplt=$author_tmplt_id";
my $coll_url = "http://67h5p1s.uds.anu.edu.au:9081/DataCommons/rest/display/new?layout=$layout&tmplt=$coll_tmplt_id";
my $linkurl = "http://67h5p1s.uds.anu.edu.au:9081/DataCommons/rest/display/addLink?item=";

my $curlcmd = $curl.' '.$user.' '.$auth_url;

# Save the authors
open AUTH_FILE, $author_file or die $!;

while (<AUTH_FILE>) {
	print OUTPUTFILE "-----------------------------\n";
	my @author = split(/;/);
	my $data='--data "type=Party"';
	$data=$data.' --data "subType=author"';
	$data=$data.' --data "ownerGroup='.$group_id.'"';
	$data=$data.' --data "externalId='.@author[0].'"';
	$data=$data.' --data "name='.@author[1].'"';
	$data=$data.' --data "abbrName='.@author[2].'"';
	
	my $command = $curlcmd.' '.$data;
	print OUTPUTFILE $command."\n";
	my $response = `$command`;
	if ($response =~ m/$matchstring/i) {
		print OUTPUTFILE "Has Identifier: ".$1."\n";
		$authorids{@author[0]} = $1;
	}
	else {
		print OUTPUTFILE "Error saving document for @author[0]"."\n";
	}
}
close(AUTH_FILE);

#!/usr/bin/perl

# while (($key, $value) = each(%authorids)) {
#	print "Key: ".$key.", Value: ".$value."\n";
# }

$curlcmd = $curl.' '.$user.' '.$coll_url;

# Save the collections and add a link to the author
open COLL_FILE, $collection_file or die $!;

while (<COLL_FILE>) {
	print OUTPUTFILE "-----------------------------\n";
	my @collection = split(/;/);
	my $size = @collection;
	print OUTPUTFILE "Size: ".$size."\n";
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
	my $response = `$command`;
	if ($response =~ m/$matchstring/i) {
		print OUTPUTFILE "Has Identifier: ".$1."\n";
		my $author = @collection[1];
		print OUTPUTFILE "Collection Author: ".$author."\n";
		my $authoritem = $authorids{$author};
		print OUTPUTFILE "Author: ".$authoritem."\n";
		
		if($authoritem) {
			$linkdata = ' --data "linkType=isOutputOf"';
			$linkdata = $linkdata.' --data "itemId=info:fedora/'.$authoritem.'"';
			$linkcommand = $curl.' '.$user.' '.$linkurl.$1.$linkdata;
			$response = `$linkcommand`;
		}
	}
	else {
		print OUTPUTFILE "Error saving document for @collection[0]\n";
	}
}
close(COLL_FILE);

close(OUTPUTFILE);