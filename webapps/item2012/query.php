<?php

include ("validate.php");

?>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd"><html>
<head>
<link href="/item/styles/jquery-ui-1.9.1.custom.min.css" rel="stylesheet" type="text/css">
<link href="/item/styles/jquery.dataTables.css" rel="stylesheet" type="text/css">
<link href="/item/styles/layout-default-latest.css" rel="stylesheet" type="text/css">
<link href="/item/styles/item.css" rel="stylesheet" type="text/css">
<link href="http://www.fraunhofer-repdose.de/repdose/query_css.css" rel="stylesheet" type="text/css">
<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
<meta name="description" content="REPDOSE database">
<meta name="keywords" content="ambit,qsar,repdose,structure search">
<meta name="robots" content="index,follow">
<META NAME="GOOGLEBOT" CONTENT="index,FOLLOW">
<meta name="copyright" content="Copyright 2008-2012. Ideaconsult Ltd. www.ideaconsult.net">
<meta name="author" content="jeliazkova.nina@gmail.com">
<meta name="language" content="English">
<meta name="revisit-after" content="7">
<link rel="SHORTCUT ICON" href="favicon.ico">
<title>REPDOSE Structure search</title>
<script type='text/javascript' src='/item/jquery/jquery-1.8.2.js'></script>
<script type='text/javascript' src='/item/jquery/jquery-ui-1.9.1.custom.min.js'></script>
<script type='text/javascript' charset='utf8' src='/item/jquery/jquery.dataTables-1.9.0.min.js'></script>
<script type='text/javascript' src='/item/jquery/purl.js'></script>
<script type='text/javascript' src='/item/jquery/jquery.base64.min.js'></script>
<script type="text/javascript" src="/item/jquery/jquery.layout-latest.min.js"></script>
<script type='text/javascript' src='/item/scripts/jlayout.js'></script>
<script type='text/javascript' src='/item/scripts/jcompound.js'></script>
<script type='text/javascript' src='/item/scripts/ambit_structures.js'></script>
<script type="text/javascript" src="/item/jquery/jquery.cookies.2.2.0.min.js"></script>


<script type='text/javascript'>
	var purl = $.url();
	var option = purl.param('option')===undefined?'auto':purl.param('option');
	var params = {
			'threshold'	:purl.param('threshold')===undefined?'0.8':purl.param('threshold'),
			'media'		:'application/x-javascript',  //jsonp datatype does not allow sending headers !!!!!
			//'search'	:purl.param('search')===undefined?'50-00-0':purl.param('search'),
			'b64search'	:purl.param('search')===undefined?$.base64.encode('50-00-0'):$.base64.encode(purl.param('search')),
			'pagesize'	:purl.param('pagesize')===undefined?'10':purl.param('pagesize')					
			};

	$(document)
			.ready(
					function() {
						
						initLayout();
												
						//ajax
						$.ajaxSetup({
							cache : false  //while dev
						});
						var url;
						var queryService = 'https://neu.fraunhofer-repdose.de:8443/item';
						switch(option)
						{
						case 'similarity': 
						  $('#qtype').text('Similarity search');	
						  $('#qthreshold').text('Tanimoto >= '+purl.param('threshold'));
						  url = queryService	+ "/query/similarity?" + $.param(params,false);
						  break;
						case 'smarts':
						  $('#qtype').text('Substructure search');
						  $('#qthreshold').text('');
  						  url = queryService	+ "/query/smarts?" + $.param(params,false);
						  break;
						default: //auto
						  $('#qtype').text('Search by identifier');
						  $('#qthreshold').text('');
						  url = queryService + "/query/compound/search/all?"+ $.param(params,false);
						}
						
						if (purl.param('search').length>60) $('#qvalue').text(purl.param('search').substring(0,60)+" ...");
						else  $('#qvalue').text(purl.param('search'));
						$('#qvalue').attr('title',purl.param('search'));
						
						var oTable = defineStructuresTable(url, queryService,purl.param('option')=='similarity');

					});
	$(function() {
		$(".repdose").button();
		$( document ).tooltip();
	});

</script>

<script>
		$.cookie('repdose.search', purl.param('search')===undefined?'50-00-0':purl.param('search'), { expires: 1 });
		$.cookie('repdose.threshold', purl.param('threshold')===undefined?'0.8':purl.param('threshold'), { expires: 1 });
		$.cookie('repdose.option', purl.param('option')===undefined?'auto':purl.param('option'), { expires: 1 });
		$.cookie('repdose.pagesize', purl.param('pagesize')===undefined?'10':purl.param('pagesize'), { expires: 1 });
</script>

</head>

<body>

<div class="ui-layout-north pane">
	<div id="container2">
	<div id="container1">
		<div id="col1">
			<label title='Repeated Dose Toxicity database structure search results'>RepDose structure search results</label>
			&nbsp;&nbsp;
		</div>
		<div id="col2">
			<a href='/repdose/'>Back to RepDose</a>
			&nbsp;
			<a href='index.php'>New structure search</a>
		</div>			
	</div>
	</div>
</div>


		
<!-- uncomment to link to repdose
<form action="/repdose/index.php" method="post"> 
-->
<form action="/repdose/query_parameters.php" method="post">  <!--  This is just for testing ! -->			 
	
<div class="ui-layout-center search" style="padding: 1 1 1 1;">
		<div>
			<div style="width='100%';margin-left: 0px; margin-right: 0px;float:clear;">
				<table class='compoundtable' id='structures'  cellpadding='2' cellspacing='0'  border='1' width='100%'>
					<thead>
						<th>
						<a href="#" id='selectall' title='Click to select all records' onClick='selectcas(true);'><u>Select</u></a><br>
						<a href="#" id='unselect' title='Click to unselect all records'  onClick='selectcas(false);'><u>Unselect</u></a>						
						</th>
						<th>#</th>
						<th>CAS</th>
						<th>Structure</th>
						<th>Name</th>
						<th>Similarity</th>
						<th>SMILES</th>
						<th>InChI</th>
						<th>InChI Key</th>
					</thead>
					<tbody></tbody>
				</table>
			</div>


		</div>


</div>

<div class="ui-layout-south pane" >
	<div id="container2">
	<div id="container1">
		<div id="col1" class='q'>
			Query:  <span id='qtype'></span>&nbsp;<span id='qthreshold'></span>
			<br>
			<b><span id='qvalue'></span></b>
			<span id='description' style='display:none;'></span>
		</div>
		<div id="col2">
			<input  class='repdose' type='submit' value='Take selected results to RepDose'>
		</div>			
	</div>
	</div>	
</div>
</form>

</body>

</html>

