package com.hcl.tokbox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Hashtable;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.json.JSONObject;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opentok.Archive;
import com.opentok.ArchiveProperties;
import com.opentok.MediaMode;
import com.opentok.OpenTok;
import com.opentok.Session;
import com.opentok.SessionProperties;
import com.opentok.exception.OpenTokException;

/**
 * 
 * @author Ravi
 *
 */

@Path("/startArchive")
public class Archived {

	@POST
	@Produces("application/json")
	public Response createOpenTokSession(InputStream customer) {
		String mSessionId = null;
		int httpErrorCode = 200;
		/******************** Params Get ***************************/

		System.out.println("\n----------RAVI--------" + customer.toString());
		StringBuilder stringBuilder = new StringBuilder();
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(customer));
			String line = null;
			while ((line = in.readLine()) != null) {
				stringBuilder.append(line);
			}
		} catch (Exception e) {
			System.out.println("Error Parsing: - " + e.getMessage());
		}
		System.out.println("\n----------RAVI--------Data Received: " + stringBuilder.toString());
		try {
			RoomInfo agentInfo = (RoomInfo) new ObjectMapper().readValue(stringBuilder.toString(), RoomInfo.class);
			mSessionId = agentInfo.getSession_id();
			System.out.println("\n--------SessionId::: - " + mSessionId);
		} catch (IOException e) {
			System.out.println("\n----------Exception--------" + e.getMessage());
		}

		/**********************************************/

		OpenTok opentok = new OpenTok(Config.apiKey, Config.apiSecret);
		Session session = null;
		String result=null;
		JSONObject jsonObject = new JSONObject();
		try {
			if (mSessionId != null) {
				session = opentok.createSession(new SessionProperties.Builder().mediaMode(MediaMode.ROUTED).build());
				// A simple Archive (without a name)
				Archive archive = opentok.startArchive(mSessionId,
						new ArchiveProperties.Builder().hasVideo(true).build());
				// Store this archiveId in the database for later use
				String archiveId = archive.getId();
				System.out.println("Archive Id:::" + archiveId);
				jsonObject.put("Status", "1");
				jsonObject.put("Message", "Archive Started");
				jsonObject.put("Archive_Id",archiveId);
				result = jsonObject.toString();
			}else{
				jsonObject.put("Status", "0");
				jsonObject.put("Message", "Archive Not Started");
				jsonObject.put("Archive_Id","0");
				result = jsonObject.toString();
			}

			return Response.status(200).entity(result).build();

		} catch (OpenTokException e1) {
			System.out.println("Exception" + e1.getMessage());
			return Response.status(404).entity("Could not generate").build();
		}
	}

	@POST
	@Path("/stopArchive")
	@Consumes("application/json")
	public Response register(InputStream customer) {
		String mArchiveId = null;
		String result=null;
		int httpErrorCode = 200;
		/******************** Params Get ***************************/

		System.out.println("\n----------RAVI--------" + customer.toString());
		StringBuilder stringBuilder = new StringBuilder();
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(customer));
			String line = null;
			while ((line = in.readLine()) != null) {
				stringBuilder.append(line);
			}
		} catch (Exception e) {
			System.out.println("Error Parsing: - " + e.getMessage());
		}
		System.out.println("\n----------Stop Archiving--------Data Received: " + stringBuilder.toString());
		try {
			RoomInfo agentInfo = (RoomInfo) new ObjectMapper().readValue(stringBuilder.toString(), RoomInfo.class);
			mArchiveId = agentInfo.getArchiveId();
			System.out.println("\n--------ArchiveId::: - " + mArchiveId);
		} catch (IOException e) {
			System.out.println("\n----------Exception--------" + e.getMessage());
		}

		/**********************************************/

		OpenTok opentok = new OpenTok(Config.apiKey, Config.apiSecret);
		Session session = null;
		JSONObject jsonObject = new JSONObject();
		try {
			if (mArchiveId != null) {
				session = opentok.createSession(new SessionProperties.Builder().mediaMode(MediaMode.ROUTED).build());
				// Stop an Archive from an archiveId (fetched from database)
				Archive archive = opentok.stopArchive(mArchiveId);
				// Store this archiveId in the database for later use
				jsonObject.put("Status", "1");
				jsonObject.put("Message", "Archive Stoped");
				jsonObject.put("Archive_Id",mArchiveId);
				result = jsonObject.toString();
			}else{
				jsonObject.put("Status", "0");
				jsonObject.put("Message", "Archive Not Stoped");
				jsonObject.put("Archive_Id",mArchiveId);
			}


			return Response.status(200).entity(result).build();

		} catch (OpenTokException e1) {
			System.out.println("Exception" + e1.getMessage());
			return Response.status(404).entity("Could not generate").build();
		}
	}

}
