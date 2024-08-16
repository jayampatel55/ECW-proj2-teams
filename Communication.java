@POST
@Path("msg")
public void sendTeamsMessage(@FormParam("data") String data) {
	try {
		JSONObject obj = Utils.decodeBase64toJSONObject(data);
		int loginId = Utils.getParamIntS(httpRequest.getSession().getAttribute("uid").toString(), 0);
		String irid = obj.optString("irid");
		String userIds = obj.optString("userIds");
		String notesTeams = obj.optString("notesTeams");
		String[] userIdsArr = userIds.split(",");
		GeneralController gc = new GeneralController();

		JSONObject irData = SendOneOnOneChatMessage.getEmailByIrid(irid);
		String email = irData.optString("email");
		irData.put("notesTeams", notesTeams);
		irData.put("noteByName", gc.getUserName(loginId));
		String userId = SendOneOnOneChatMessage.getUserId(email);
		SendOneOnOneChatMessage.sendMessageToUser(userId, irData);
		for (String userId2 : userIdsArr) {
			int userInt = Utils.getParamInt(userId2, 0);
			//userIdList.add(gc.getUserEmail(Utils.getParamInt(userId,0)));
			if(userInt > 0) {
				String userId1 = SendOneOnOneChatMessage.getUserId(gc.getUserEmail(userInt));
				SendOneOnOneChatMessage.sendMessageToUser(userId1, irData);
			}

		}
	} catch (Exception e) {
		logger.error(e.getMessage(), e);
	}
}