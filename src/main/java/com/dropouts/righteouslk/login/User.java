package com.dropouts.righteouslk.login;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.dropouts.righteouslk.db.DatabaseManager;
import com.dropouts.righteouslk.posts.Post;

import javassist.bytecode.analysis.MultiArrayType;

@Path("/user")
public class User {

	private static DatabaseManager databaseManager;
	private static final Logger log = Logger.getLogger(Post.class);

	static {
		databaseManager = DatabaseManager.getInstance();
	}

	@POST
	@Path("/login")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response authenticate(@FormDataParam("username") String userId, @FormDataParam("password") String password) {

		Session session = databaseManager.getSession();
		ResultSet user = session.execute("SELECT * FROM righteous.user where user_id = '" + userId + "'");

		Row row = null;
		if (user.isExhausted()) {
			session.close();
			return Response.status(200).entity("Invalid Username or Password").build();
		} else {
			row = user.one();
		}

		if (row.getString("user_id").equals(userId) && row.getString("user_password").equals(password)) {

			session.close();
			return Response.status(200).entity("success").build();
		} else {
			session.close();
			return Response.status(200).entity("Invalid Username or Password").build();
		}
	}

	@POST
	@Path("/register")
	public Response register(@FormDataParam("id") String userId, @FormDataParam("username") String userName,
			@FormDataParam("password") String password, @FormDataParam("dob") String dob,
			@FormDataParam("gender") String gender, @FormDataParam("location") String location,
			@FormDataParam("proPic") InputStream proPicInputStream,
			@FormDataParam("proPic") FormDataContentDisposition proPicFileDetail) {

		Session session = databaseManager.getSession();
		ResultSet users = session.execute("SELECT * FROM righteous.user");
		
		Calendar calendar = new GregorianCalendar();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date date = null;
		try {
			date = simpleDateFormat.parse(dob);
		} catch (ParseException e1) {
			log.error("", e1);
		}
		calendar.setTime(date);

		for (Row row : users) {
			if (row.getString("user_id").equalsIgnoreCase(userId)) {
				return Response.status(200).entity("Username unavailable").build();
			}
		}

		byte[] imageBytes = null;
		try {
			imageBytes = org.apache.commons.io.IOUtils.toByteArray(proPicInputStream);
		} catch (IOException e) {
			log.error("Unable to retrieve the image", e);
		}

		ByteBuffer imageByteBuffer = ByteBuffer.wrap(imageBytes);

		PreparedStatement preparedStatement = session
				.prepare("INSERT INTO righteous.user (user_id, user_name, user_password, user_dob, user_gender, user_location, user_pic) " + "VALUES (?,?,?,?,?,?,?)");

		BoundStatement boundStatement = new BoundStatement(preparedStatement);
		session.execute(boundStatement.bind(userId,userName,password,calendar.getTime(),gender,location,imageByteBuffer));

		session.close();

		return Response.status(200).entity("Registration successful").build();
	}
}