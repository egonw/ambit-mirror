<#include "/html.ftl" >
<head>
<#include "/header.ftl" >

<script type='text/javascript' src='${ambit_root}/scripts/jopentox.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/jopentox-ui.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/jopentox-ui-substance.js'></script>
<link rel="stylesheet" href="${ambit_root}/style/jtoxkit.css"/>
<script type='text/javascript' src='${ambit_root}/jquery/purl.js'></script>

	<script type='text/javascript'>
	
	$(document).ready(function() {
	  	var oTable = substance.defineSubstanceTable("${ambit_root}","${ambit_request_json}","#substances",true,null,'Trt',true);
	  	loadHelp("${ambit_root}","substance");
	  	$( "#selectable" ).selectable( "option", "distance", 18);
	  	downloadForm("${ambit_request}");
	  	
		var purl = $.url();
		$('#search').attr('value',purl.param('search')===undefined?'':purl.param('search'));
		
		var typeToSelect = purl.param('type')===undefined?'':purl.param('type');
        $("#selecttype option").each(function (a, b) {
	          if ($(this).val() == typeToSelect ) $(this).attr("selected", "selected");
	    });		
	});
	</script>

</head>
<body>


<div class="container" style="margin:0;padding:0;">

<form method='GET' name='searchform' id='searchform' action='${ambit_root}/substance' style='padding:0;margin:0;'>
<!-- banner -->
<div class="row remove-bottom" id="header">
	<#include "/toplinks.ftl">
</div>
<div class="row remove-bottom">
		<#include "/logo.ftl">
		<div class="thirteen columns remove-bottom" id="query">
		<div class="six columns alpha">
			<div class="remove-bottom h3">
					Substances
			</div>
		    <div class='help'>
		     Mono-constituent, multiconstituent, additives, impurities.
		    </div>
		     <a href='${ambit_root}/substance' title='All substances'>All</a> | 
		     <a href='${ambit_root}/substance?type=CompTox&search=Ambit+Transfer' title='Substances with external identifier set to "other:CompTox=Ambit Transfer"'>CompTox</a>
		</div>
		<div class="four columns omega">
			<div class="remove-bottom h3">
				&nbsp;
			</div>
		    <div class='h6'>
				<select id='selecttype' name="type">
				  <option value="name">Name</option>
				  <option value="uuid">UUID</option>
				  <option value="">External identifier</option>
				  <option value="CompTox">CompTox</option>
				  <option value="DOI">DOI</option>
				  <option value="reliability" title='1 (reliable without restriction)|2 (reliable with restrictions)|3 (not reliable)|4 (not assignable)|other:empty (not specified)'>Reliability</option>
				  <option value="purposeFlag" title='key study|supporting study'>Study purpose</option>
				  <option value="studyResultType" title='experimental result|estimated by calculation|read-across|(Q)SAR'>Study result type</option>
				  <option value="isRobustStudy" title='true|false'>Robust study</option>
				  <option value="citation" title='Experiment reference'>Publication/report describing the experiment</option>
				  <option value="topcategory" title='One of P-CHEM, ENV FATE, ECOTOX, TOX'>One of P-CHEM, ENV FATE, ECOTOX, TOX</option>
				  <option value="endpointcategory" title='Endpoint category (e.g. EC_FISHTOX_SECTION)'>Endpoint category (e.g. EC_FISHTOX_SECTION)</option>
				  <option value="params" title='Protocol parameter'>Protocol parameter</option>
				</select>
		    </div>			
		</div>			
		<div class="four columns omega">
			<div class="remove-bottom h3">
				&nbsp;
			</div>
		    <div class='h6'>
		    	<input type='text'  id='search' name='search' value='' tabindex='1' >
		    </div>			
		</div>		
		<div class="two columns omega">
			<div class="remove-bottom h3">
				&nbsp;
			</div>
		    <div class='h6'>
		    	<input class='ambit_search' id='submit' type='submit' value='Search' tabindex='2'>
		    </div>			
		</div>	
		</div>
</div>		
<div class="row remove-bottom" >
	  <div id="header_bottom" class="remove-bottom">&nbsp;</div>
</div>

</form>
<div class="three columns" style="padding:0 2px 2px 2px 0;margin-right:0;" >
<#include "/menu_substance.ftl">
	<div class='row' id='download' style='background: #F2F0E6;margin: 3px; padding: 0.4em; font-size: 1em; '>
	<a href='#' id='uri'><img src='${ambit_root}/images/link.png' alt='text/uri-list' title='Download as URI list'></a>
	<!-- Not supported yet
	<a href='#' id='rdfxml'><img src='${ambit_root}/images/rdf.gif' alt='RDF/XML' title='Download as RDF/XML (Resource Description Framework XML format)'></a>
	<a href='#' id='rdfn3'><img src='${ambit_root}/images/rdf.gif' alt='RDF/N3' title='Download as RDF N3 (Resource Description Framework N3 format)'></a>
	-->
	<a href='#' id='json' target=_blank><img src='${ambit_root}/images/json.png' alt='json' title='Download as JSON'></a>
	</div>
<!-- help-->		
	<div class='row half-bottom chelp' style='padding:0;margin:0;' id='pagehelp'></div>
	<div class='row remove-bottom chelp' style='padding:0;margin:0;font-weight:bold;' id='keytitle'>		
	</div>
	<div class='row half-bottom chelp' style='padding:0;margin:0;' id='keycontent'>		
	</div>		
</div>

<div class="thirteen columns remove-bottom" style="padding:0;" >

 		<div class="row remove-bottom ui-widget-header ui-corner-top">
 		&nbsp;
 		</div>

		<!-- Page Content
		================================================== -->
		<div class="row" style="padding:0;" >
			<table id='substances' class='substancetable ambit2' cellpadding='0' border='0' width='100%' cellspacing='0' style="margin:0;padding:0;" >
			<thead>
			<tr>
			<th></th>
			<th>Substance Name</th>
			<th>Substance UUID</th>
			<th>Type Substance Composition</th>
			<th>Public name</th>
			<th>Reference substance UUID</th>
			<th title='Legal entity'>Owner</th>
			<th>Details</th>
			<th title='Remove the substance and all related studies'><span class='ui-icon ui-icon-trash' style='float: left; margin: .1em;'></span></th>
			</tr>
			</thead>
			<tbody></tbody>
			</table>
	
		</div>
		


<div class='row add-bottom' style="height:140px;">&nbsp;</div>
</div>

<#include "/footer.ftl" >
</div> <!-- container -->
</body>
</html>
