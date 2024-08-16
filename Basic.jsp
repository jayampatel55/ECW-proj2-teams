//This code is not a full frontend, just the button and function syntax
//NOTE: All the three function must be in this same file, you can edit css and frontend

//Button
<button class="DarkGrayish" type="button" id="sendMsgTeams">Send Teams Message</button>

//Button function
commBasicInfo.$prefix.find("#sendMsgTeams").on("click",function(){

						 	var html = '<div class="form-group input-group" style="margin: 1px 0 0 0;">'+
						 			'<h5>To:</h5>'+
									'<input type="text" class="form-control input-sm" id="teamsUser" placeholder="Select users" autocomplete="off">'+

									'<h5>Note:</h5>'+
									'<textarea type="text" class="form-control input-sm" id="teamsNotes">'+
									'</textarea>'+
								 '<span class="input-group-btn" id="" >'+
								 '<button class="btn btn-default btn-sm" type="button" onclick="$(this).parent().parent().find(\'input\').val(\'\')" style="height: 30px;align-items: center; display: flex;">'+
								 '<i class="fa fa-minus"></i>'+
									'</button>'+
					 				'</span>'+
									'</div>';
							 var Container = ".ir_teams_popup";
							 BootstrapDialog.show({
								 title: 'Send IR Notification To',
								 message: html,
								 draggable: true,
								 closable: true,
								 closeByBackdrop: false,
								 closeByKeyboard: false,
								 cssClass: 'ir_teams_popup',
								 buttons: [{
									 label: 'Cancel',
									 cssClass: 'btn-default btn-sm',
									 action: function (dialog) {
										 dialog.close();
									 }
								 }, {
									 label: 'Proceed',
									 cssClass: 'btn-primary btn-sm',
									 action: function (dialog) {
										 var userIds = $.trim($(Container + " #teamsUser").val()).length > 0 ? $(Container + " #teamsUser").val() : "";
										 var notesTeams = $.trim($(Container + " #teamsNotes").val());

										 commBasicInfo.sendMessage(userIds,notesTeams);
										 dialog.close();
									 }
								 }],
								 onshown: function (dialog) {

									 var ecwUserUrl =Home.restUrl + "General/getEcwUsersFortokenInput?limit=10";
									 $(Container + " #teamsUser").tokenInput(ecwUserUrl, {
										 theme: "facebook",
										 preventDuplicates: true
									 });
								 }
							 });

					 })

//Ajax call
commBasicInfo.sendMessage = function(userIds,notesTeams){
			var finalObj = {};
			finalObj["userIds"] = userIds;
			finalObj["notesTeams"] = notesTeams;
			finalObj["irid"] = commIr.irid;


					$.ajax({
						url:commBasicInfo.url+"msg",
						type:"post",
						headers: {"X-CSRF-Token":window.top.emgrSecurity.csrfToken},
						data : {data: Base64.encode(JSON.stringify(finalObj))},
						cache: false,
						dataType:"json"
					})
		}