 
   jQuery(document).ready(function(){
	   issueNoteUrls = {
			   divR1I1:    ctx + "/CaseManagementView.do?hc=996633&method=listNotes&providerNo=" + providerNo + "&demographicNo=" + demographicNo + "&issue_code=SocHistory&title=" + socHistoryLabel + "&cmd=divR1I1",
               divR1I2:    ctx + "/CaseManagementView.do?hc=996633&method=listNotes&providerNo=" + providerNo + "&demographicNo=" + demographicNo + "&issue_code=MedHistory&title=" + medHistoryLabel + "&cmd=divR1I2",
               divR2I1:    ctx + "/CaseManagementView.do?hc=996633&method=listNotes&providerNo=" + providerNo + "&demographicNo=" + demographicNo + "&issue_code=Concerns&title=" + onGoingLabel + "&cmd=divR2I1",
               divR2I2:    ctx + "/CaseManagementView.do?hc=996633&method=listNotes&providerNo=" + providerNo + "&demographicNo=" + demographicNo + "&issue_code=Reminders&title=" + remindersLabel + "&cmd=divR2I2",
               divR3I1:    ctx + "/CaseManagementView.do?hc=996633&method=listNotes&providerNo=" + providerNo + "&demographicNo=" + demographicNo + "&issue_code=DiagnosticNotes&title=" + diagnosticNotesLabel + "&cmd=divR3I1",
               divR3I2:    ctx + "/CaseManagementView.do?hc=996633&method=listNotes&providerNo=" + providerNo + "&demographicNo=" + demographicNo + "&issue_code=PastOcularHistory&title=" + pastOcularHistoryLabel + "&cmd=divR3I2",
               divR4I1:    ctx + "/CaseManagementView.do?hc=996633&method=listNotes&providerNo=" + providerNo + "&demographicNo=" + demographicNo + "&issue_code=PatientLog&title=" + patientLogLabel + "&cmd=divR4I1",
               divR4I2:    ctx + "/CaseManagementView.do?hc=996633&method=listNotes&providerNo=" + providerNo + "&demographicNo=" + demographicNo + "&issue_code=OcularMedications&title=" + ocularMedicationsLabel + "&cmd=divR4I2"                                         
       };
       //add diagnostic notes, past ocular history, patient log, ocular medications
       //add a row       
       addCppRow(3);
       addCppRow(4);
       
     	//link save button
       
		jQuery("#save_measurements").live('click',function(e){
			e.preventDefault();
			touchColor();
			
			//save all measurements
			var postData = "";
			jQuery("input[measurement]").each(function() {
				if(postData.length > 0) {
					postData += "&";
				}
				var name = jQuery(this).attr("measurement");
				var value = jQuery(this).val();
				var data = name + "=" + value;
				postData += data;
			});

			jQuery.ajax({type:'POST',url:ctx+'/oscarEncounter/MeasurementData.do?action=saveValues&demographicNo='+demographicNo,data:postData,success: function(){
				alert('test');
			}});
		});
		
		
       jQuery("#cppBoxes").append("<div id=\"measurements_div\"></div>");

     
       jQuery.ajax({url:ctx+"/eyeform/exam.jsp",dataType: "html",success: function(data) {
			jQuery("#measurements_div").append(data);
			//create comma separated list of the measurement types (from attribute)
			var types='';
			jQuery("input[measurement]").each(function() {
				if(types.length > 0) {
					types += ',';
				}
				types += jQuery(this).attr("measurement");
			});

			//make a call to update the existing values
			jQuery.ajax({dataType: "script", url:ctx+"/oscarEncounter/MeasurementData.do?demographicNo="+demographicNo+"&types="+types+"&action=getLatestValues"});
						
       }});


       init();
      
       
      
       jQuery.ajax({url:ctx+"/eyeform/NoteData.do?method=getCurrentNoteData&demographicNo="+demographicNo+"&noteId="+savedNoteId,dataType: "html",success: function(data) {
			jQuery("#current_note_addon").append(data);
       }});
       	   	  
       
        
       
	   addLeftNavDiv("ocularprocedure");	                         
       popColumn(ctx + "/oscarEncounter/displayOcularProcedure.do?hC=009999","ocularprocedure","ocularProcedure", "leftNavBar", this);

       addLeftNavDiv("specshistory");	                         
       popColumn(ctx + "/oscarEncounter/displaySpecsHistory.do?hC=009999","specshistory","specsHistory", "leftNavBar", this);
       
       addLeftNavDiv("macro");	      
       popColumn(ctx + "/oscarEncounter/displayMacro.do?hC=009999","macro","macro", "leftNavBar", this);
       
     });

 