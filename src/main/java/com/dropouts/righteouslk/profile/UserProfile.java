package com.dropouts.righteouslk.profile;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.media.multipart.FormDataParam;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.dropouts.righteouslk.db.DatabaseManager;
import com.google.gson.JsonObject;

@Path("/profile")
public class UserProfile {

	@POST
	@Path("/get")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response getProfile(@FormDataParam("userId") String userId){
		
		Session session = DatabaseManager.getInstance().getSession();
		ResultSet user = session.execute("SELECT * FROM righteous.user where user_id = '" + userId + "'");
		
		Row row = null;
		JsonObject profileJson = new JsonObject();
		
		if (!user.isExhausted()){
			row = user.one();
			profileJson.addProperty("user_id", row.getString("user_id"));
			profileJson.addProperty("user_name", row.getString("user_name"));
			profileJson.addProperty("user_password", row.getString("user_password"));
			profileJson.addProperty("user_dob", row.getString("user_dob"));
			profileJson.addProperty("user_gender", row.getString("user_gender"));
			profileJson.addProperty("user_location", row.getString("user_location"));
//			profileJson.addProperty("user_pic", row.getBytes("user_pic").array());
		}
		return null;
	}
}
