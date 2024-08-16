package communication;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import common.EmanagerProperties;
import general.DBUtil;
import general.Root;
import general.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Scanner;

public class SendOneOnOneChatMessage {

    private static final String TENANT_ID = "7ce747e4-8f6d-4e06-97ef-a9b76a063808";
    private static final String CLIENT_ID = "f9e27b52-6f20-499f-b3a3-0e58999c5206";
    private static final String CLIENT_SECRET = "ibO8Q~xaUdliQLNOoNavN.lPMb-s~Ovoeg1rRdnp";

    //private static final String SCOPE = "https://graph.microsoft.com/.default";
    private static final String SCOPE = "ChatMessage.Send";
    private static final String TOKEN_URL = "https://login.microsoftonline.com/eclinicalworks.com/oauth2/token";
    private static final String YOUR_USER_ID = "a7b4635a-1b19-4936-b681-e5732f3c9871"; // Replace this with your actual user ID
    static Logger logger = LogManager.getLogger(SendOneOnOneChatMessage.class);
    private static final String accessToken = "eyJ0eXAiOiJKV1QiLCJub25jZSI6IllrUVMzdEtqekJhT01qeV9pdFRVWDg3aTdxQW9yYURKbjQ4V2I0VTFOVXciLCJhbGciOiJSUzI1NiIsIng1dCI6IktRMnRBY3JFN2xCYVZWR0JtYzVGb2JnZEpvNCIsImtpZCI6IktRMnRBY3JFN2xCYVZWR0JtYzVGb2JnZEpvNCJ9.eyJhdWQiOiIwMDAwMDAwMy0wMDAwLTAwMDAtYzAwMC0wMDAwMDAwMDAwMDAiLCJpc3MiOiJodHRwczovL3N0cy53aW5kb3dzLm5ldC83Y2U3NDdlNC04ZjZkLTRlMDYtOTdlZi1hOWI3NmEwNjM4MDgvIiwiaWF0IjoxNzIzNzI1Mjk4LCJuYmYiOjE3MjM3MjUyOTgsImV4cCI6MTcyMzczMDY5MywiYWNjdCI6MCwiYWNyIjoiMSIsImFjcnMiOlsiYzIiXSwiYWlvIjoiQVZRQXEvOFhBQUFBQWxzZ2gydEdabnIrdStvNDE1blRVcTk4ZkhwTzhYTTN4N3RmM0xFSms2Tk9UZlB3UU44MWxGU3ZVTVJNWXVNaEdpZDVVaWE1c0VicmV2ZDF3YmZldTFMZk9xSHROSWNWU0dGVmZpNVpzNWM9IiwiYW1yIjpbInB3ZCJdLCJhcHBfZGlzcGxheW5hbWUiOiJHcmFwaCBFeHBsb3JlciIsImFwcGlkIjoiZGU4YmM4YjUtZDlmOS00OGIxLWE4YWQtYjc0OGRhNzI1MDY0IiwiYXBwaWRhY3IiOiIwIiwiZmFtaWx5X25hbWUiOiJQYXRlbCIsImdpdmVuX25hbWUiOiJKYXlhbSIsImlkdHlwIjoidXNlciIsImlwYWRkciI6IjE4Mi43My4xNjEuMjQyIiwibmFtZSI6IkpheWFtIFBhdGVsIiwib2lkIjoiYTdiNDYzNWEtMWIxOS00OTM2LWI2ODEtZTU3MzJmM2M5ODcxIiwib25wcmVtX3NpZCI6IlMtMS01LTIxLTIyMzA0MTIyMjItMzczMzUxOTE0Ny0zMzAzOTI1NTgwLTczOTQ0IiwicGxhdGYiOiIzIiwicHVpZCI6IjEwMDMyMDAzOUM2QjBGMkYiLCJyaCI6IjAuQVNjQTVFZm5mRzJQQms2WDc2bTNhZ1k0Q0FNQUFBQUFBQUFBd0FBQUFBQUFBQUFBQWRNLiIsInNjcCI6IkFQSUNvbm5lY3RvcnMuUmVhZC5BbGwgQVBJQ29ubmVjdG9ycy5SZWFkV3JpdGUuQWxsIENhbGVuZGFycy5SZWFkV3JpdGUgQ2hhbm5lbE1lc3NhZ2UuUmVhZFdyaXRlIENoYW5uZWxNZXNzYWdlLlNlbmQgQ2hhdC5DcmVhdGUgQ2hhdC5SZWFkIENoYXQuUmVhZEJhc2ljIENoYXQuUmVhZFdyaXRlIENvbnRhY3RzLlJlYWRXcml0ZSBEZXZpY2VNYW5hZ2VtZW50QXBwcy5SZWFkLkFsbCBEaXJlY3RvcnkuUmVhZFdyaXRlLkFsbCBGaWxlcy5SZWFkV3JpdGUuQWxsIEdyb3VwLlJlYWQuQWxsIEdyb3VwLlJlYWRXcml0ZS5BbGwgR3JvdXBNZW1iZXIuUmVhZC5BbGwgTWFpbC5SZWFkV3JpdGUgTm90ZXMuUmVhZFdyaXRlLkFsbCBPbmxpbmVNZWV0aW5nQXJ0aWZhY3QuUmVhZC5BbGwgT25saW5lTWVldGluZ3MuUmVhZCBPbmxpbmVNZWV0aW5ncy5SZWFkV3JpdGUgb3BlbmlkIFBlb3BsZS5SZWFkIFByZXNlbmNlLlJlYWQgUHJlc2VuY2UuUmVhZC5BbGwgcHJvZmlsZSBSZXBvcnRzLlJlYWQuQWxsIFNpdGVzLlJlYWRXcml0ZS5BbGwgVGFza3MuUmVhZFdyaXRlIFRlYW0uQ3JlYXRlIFRlYW1TZXR0aW5ncy5SZWFkLkFsbCBVc2VyLk1hbmFnZUlkZW50aXRpZXMuQWxsIFVzZXIuUmVhZCBVc2VyLlJlYWQuQWxsIFVzZXIuUmVhZEJhc2ljLkFsbCBVc2VyLlJlYWRXcml0ZSBVc2VyLlJlYWRXcml0ZS5BbGwgZW1haWwiLCJzaWduaW5fc3RhdGUiOlsiaW5rbm93bm50d2siLCJrbXNpIl0sInN1YiI6IkJJX0VLdVJIOHB0X0tHZkFxa2N2WEJ0c05KQmpkSEkyUGtwWVFGazVURm8iLCJ0ZW5hbnRfcmVnaW9uX3Njb3BlIjoiTkEiLCJ0aWQiOiI3Y2U3NDdlNC04ZjZkLTRlMDYtOTdlZi1hOWI3NmEwNjM4MDgiLCJ1bmlxdWVfbmFtZSI6ImpheWFtLnBhdGVsQGVjbGluaWNhbHdvcmtzLmNvbSIsInVwbiI6ImpheWFtLnBhdGVsQGVjbGluaWNhbHdvcmtzLmNvbSIsInV0aSI6IlBiT25rNWQwdUVXWHg0aDBhTjNsQUEiLCJ2ZXIiOiIxLjAiLCJ3aWRzIjpbImI3OWZiZjRkLTNlZjktNDY4OS04MTQzLTc2YjE5NGU4NTUwOSJdLCJ4bXNfY2MiOlsiQ1AxIl0sInhtc19pZHJlbCI6IjEgMTYiLCJ4bXNfc3NtIjoiMSIsInhtc19zdCI6eyJzdWIiOiJCcUZCa01FNDZTcERhbXpKTTljUVdydTlhODdDeUstS0ZYQm9VOG51Mnl3In0sInhtc190Y2R0IjoxNDI3OTEzNTIwfQ.jBaIRvfpwji1tODfmh2WhOmRaaLAY7oIouMoR7PtjoqAOXbPs0LUnhR5Yheq2o7x84fteAmnX4GQlYmKFGBAoH59nIixSpb1FV--qT09s0cQxaEFKPMZLESTjrZyXrkdejeWruLRKzWZcwfzOfhReTtmvEvowiovuTwcSX845v9FlWTISOpNCT0gDFMM2TYf39ON4QyMiqWa7be2JlLHoNVQX9oGFpYkdU74m2NChSGWhSCPuAGOVw5qEZk5FdlkIhrIFVReCaWxFrBIvKRF2muayAB-HCkskjq7jpiVYrsfVJWwBhesQWMy8q0G4Ffq9y7x5ni_vplg1TzFF1K5kQ";

    public static void main(String[] args) {

        try {
            Scanner scanner = new Scanner(System.in);
            JSONObject irData = getEmailByIrid("irid");
            String email = irData.optString("email");
            String userId = getUserId(email);
            sendMessageToUser(userId, irData);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method to convert HTML to plain text using Jsoup
    public static String convertHtmlToPlainText(String html) {
        return Jsoup.parse(html).text();
    }

    // Method to fetch IR data from the database and convert HTML content to plain text
    public static JSONObject getEmailByIrid(String irid) throws SQLException {
        String query = "SELECT u.uemail as email, CONCAT(u1.ufname, ' ', u1.ulname) AS createdbyname, cm.title, cm.description, " +
                "DATE_FORMAT(cm.createdon,'%m-%d-%Y %h:%i %p') AS createdate "+
                "FROM comm_internalrequset_mst cm " +
                "JOIN users u ON u.uid = cm.assignedto " +
                "JOIN users u1 ON u1.uid = cm.createdby " +
                "AND u.delFlag = 0 AND u.inactive = 0 AND u.UserType = 2 AND u.TLFlg <> 8 " +
                "AND u1.delFlag = 0 AND u1.inactive = 0 AND u1.UserType = 2 AND u1.TLFlg <> 8 " +
                "WHERE cm.irid = ?";

        JSONObject irData = new JSONObject();
        try (DBUtil dbUtil = new DBUtil(Root.SLAVE)) {
            irData = dbUtil.getJSONObjectFromDatabase(query, "", irid);
            irData.put("irid", irid); // Add irid to the JSONObject

            // Convert HTML to plain text
            String title = convertHtmlToPlainText(irData.optString("title"));
            String description = convertHtmlToPlainText(irData.optString("description"));

            // Update irData with plain text
            irData.put("title", title);
            irData.put("description", description);
            System.out.println(description);
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
        }
        return irData;
    }
    private static String getAccessToken() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        String body = "client_id=" + URLEncoder.encode(CLIENT_ID, StandardCharsets.UTF_8) +
                "&scope=" + URLEncoder.encode(SCOPE, StandardCharsets.UTF_8) +
                "&client_secret=" + URLEncoder.encode(CLIENT_SECRET, StandardCharsets.UTF_8) +
                "&resource=https://graph.microsoft.com/" +
                "&grant_type=client_credentials";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(TOKEN_URL))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("Access Token Response: " + response.body()); // Debugging log
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonResponse = objectMapper.readTree(response.body());

        return jsonResponse.get("access_token").asText();
    }

    public static String getUserId(String email) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        String url = "https://graph.microsoft.com/v1.0/users/" + email;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + accessToken)
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("User ID Response: " + response.body()); // Debugging log
        int responseCode = response.statusCode();

        if (responseCode == 200) {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonResponse = objectMapper.readTree(response.body());
            return jsonResponse.get("id").asText();
        } else {
            System.err.println("Failed to get user ID: " + response.body());
            throw new IOException("Error: " + responseCode + " - " + response.body());
        }
    }

    public static void sendMessageToUser(String userId, JSONObject irData) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        String url = "https://graph.microsoft.com/v1.0/chats";

        String jsonPayload = "{ \"chatType\": \"oneOnOne\", \"members\": [ " +
                "{ \"@odata.type\": \"#microsoft.graph.aadUserConversationMember\", \"roles\": [\"owner\"], \"user@odata.bind\": \"https://graph.microsoft.com/v1.0/users/" + YOUR_USER_ID + "\" }, " +
                "{ \"@odata.type\": \"#microsoft.graph.aadUserConversationMember\", \"roles\": [\"owner\"], \"user@odata.bind\": \"https://graph.microsoft.com/v1.0/users/" + userId + "\" } ] }";

        System.out.println("Creating Chat with Payload: " + jsonPayload); // Debugging log

        int maxRetries = 3;
        int attempt = 0;
        boolean success = false;

        while (attempt < maxRetries && !success) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", "Bearer " + accessToken)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload.toString()))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Create Chat Response: " + response.body()); // Debugging log
            int responseCode = response.statusCode();

            if (responseCode == 201) {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonResponse = objectMapper.readTree(response.body());
                String chatId = jsonResponse.get("id").asText();
                String irid = irData.optString("irid"); // Extract irid from irData
                sendChatMessage(chatId, irid, irData);
                success = true;
            } else {
                System.err.println("Failed to create chat: " + response.body());
                attempt++;
                if (attempt < maxRetries) {
                    System.out.println("Retrying... (" + attempt + ")");
                    Thread.sleep(2000); // wait for 2 seconds before retrying
                } else {
                    throw new IOException("Error: " + responseCode + " - " + response.body());
                }
            }
        }
    }

    public static void sendChatMessage(String chatId, String irid, JSONObject irData) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        String url = "https://graph.microsoft.com/v1.0/chats/" + chatId + "/messages";

        String createdBy = irData.optString("createdbyname");
        String title = irData.optString("title");
        String description = irData.optString("description");
        String notesTeams = irData.optString("notesTeams");
        String notesId = irData.optString("noteByName");
        String createDate=irData.optString("createdate");


        // Generate the dynamic link
        JSONObject deepLinkObj = new JSONObject();
        deepLinkObj.put("module", "bnVsbA");
        deepLinkObj.put("js", Utils.encodeBase64("utils.openNewIR('" + irid + "')" ));
        String link = EmanagerProperties.getValue("EMGR_WEB_URL") + "/jsp/DeepLink/Link.jsp?q=" + Utils.encodeBase64(deepLinkObj.toString());

        // Construct the Adaptive Card content in the desired format
        StringBuilder adaptiveCardContent = new StringBuilder();
        adaptiveCardContent.append("{\\\"type\\\":\\\"AdaptiveCard\\\",\\\"$schema\\\":\\\"http://adaptivecards.io/schemas/adaptive-card.json\\\",\\\"version\\\":\\\"1.6\\\",\\\"body\\\":[")
                .append("{\\\"type\\\":\\\"TextBlock\\\",\\\"text\\\":\\\"[IR Request # ")
                .append(irid)
                .append("](")
                .append(link)
                .append(")\\\",\\\"weight\\\":\\\"Bolder\\\",\\\"wrap\\\":true},")
                .append("{\\\"type\\\":\\\"TextBlock\\\",\\\"text\\\":\\\"Reason ").append(":   ")
                .append(title)
                .append("\\\",\\\"weight\\\":\\\"Bolder\\\",\\\"wrap\\\":true},")
                .append("{\\\"type\\\":\\\"TextBlock\\\",\\\"text\\\":\\\"Description:\\\",\\\"weight\\\":\\\"Bolder\\\",\\\"wrap\\\":true},")
                .append("{\\\"type\\\":\\\"TextBlock\\\",\\\"text\\\":\\\"").append(description).append("\\\",\\\"wrap\\\":true},")
                .append("{\\\"type\\\":\\\"TextBlock\\\",\\\"text\\\":\\\"Created By ").append(":   ").append(createdBy).append(" on ").append(createDate).append("\\\",\\\"weight\\\":\\\"Bolder\\\",\\\"wrap\\\":true}");


        // Conditionally add the Notes section
        if (!notesTeams.isEmpty()) {
            adaptiveCardContent.append(",{\\\"type\\\":\\\"TextBlock\\\",\\\"text\\\":\\\"Message From ").append(notesId)
                    .append(":\\\",\\\"weight\\\":\\\"Bolder\\\",\\\"wrap\\\":true},{\\\"type\\\":\\\"TextBlock\\\",\\\"text\\\":\\\"")
                    .append(notesTeams).append("\\\",\\\"wrap\\\":true}");
        }

        // Closing the Adaptive Card with the dynamic link for the button
        adaptiveCardContent.append("],\\\"actions\\\":[{\\\"type\\\":\\\"Action.OpenUrl\\\",\\\"title\\\":\\\"Click here to view this IR\\\",\\\"url\\\":\\\"").append(link).append("\\\"}]}");

        // JSON payload
        String jsonPayload = "{\n" +
                "  \"body\": {\n" +
                "    \"contentType\": \"html\",\n" +
                "    \"content\": \"<attachment id=\\\"4465B062-EE1C-4E0F-B944-3B7AF61EAF40\\\"></attachment>\"\n" +
                "  },\n" +
                "  \"attachments\": [\n" +
                "    {\n" +
                "      \"id\": \"4465B062-EE1C-4E0F-B944-3B7AF61EAF40\",\n" +
                "      \"contentType\": \"application/vnd.microsoft.card.adaptive\",\n" +
                "      \"content\": \"" + adaptiveCardContent.toString() + "\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + accessToken)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("Send Message Response: " + response.body()); // Debugging log
        System.out.println("Response Code: " + response.statusCode());
    }

    private static void generateTokenNew(String urlParams) {
        int respCode;
        try {
            URL url = new URL(TOKEN_URL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setDoOutput(true);
            con.setUseCaches(false);
            con.addRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            OutputStream wr = con.getOutputStream();
            wr.write(urlParams.getBytes());
            wr.flush();
            wr.close();
            respCode = con.getResponseCode();
            System.out.println("respCode " + respCode);
            StringBuilder response = new StringBuilder();

            Scanner sc = new Scanner(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8));
            String inputStr = "";
            while (sc.hasNextLine() && (inputStr = sc.nextLine()) != null) {
                response.append(inputStr);
            }
            System.out.println("response : " + response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


