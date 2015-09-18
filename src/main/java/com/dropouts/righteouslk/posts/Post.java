package com.dropouts.righteouslk.posts;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.sql.rowset.serial.SerialException;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.DatatypeConverter;

import org.apache.log4j.Logger;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.utils.Bytes;
import com.dropouts.righteouslk.db.DatabaseManager;

@Path("/post")
public class Post {
	
	private static final Logger log = Logger.getLogger(Post.class);

	@POST
	@Path("/submit")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response postEvent(@FormDataParam("file") InputStream uploadedInputStream,
			@FormDataParam("file") FormDataContentDisposition fileDetail,
			@FormDataParam("postStr") String postStr,
			@FormDataParam("userId") String userId, 
			@FormDataParam("postLocation") String postLocation, 
			@FormDataParam("timestamp") String time)
	{
		byte[] imageBytes = null;
		Session session = DatabaseManager.getInstance().getSession();

		try {
			imageBytes = org.apache.commons.io.IOUtils.toByteArray(uploadedInputStream);
		} catch (IOException e) {
			log.error("Unable to retrieve the image", e);
		}
		
		log.info(postStr + " " +  userId + " " + postLocation + " " + time);
		
//		ResultSet result = session.execute("SELECT user_name, user_pic FROM righteous.user WHERE user_id = '"+ "" +"' ");
		
//		Row row = result.one();
		
//		byte[] userPic = row.getBytes("user_pic").array();
//		String userName = row.getString("user_name");
		
		
//		String imgStr = new String(image);
//		String imgStr = Bytes.toHexString(image);
		ByteBuffer imageByteBuffer = ByteBuffer.wrap(imageBytes);
		
		PreparedStatement preparedStatement = session.prepare("INSERT INTO righteous.post_from_user_id (post_pic, post_str, post_user_id, "
				+ "post_user_name, post_user_pic, post_location, post_timestamp) "
				+ "VALUES (?,?,?,?,?,?,?)");
		
		BoundStatement boundStatement = new BoundStatement(preparedStatement);
		
		Calendar calender = new GregorianCalendar(2011, Calendar.JULY, 3);
		calender.add(Calendar.DAY_OF_MONTH, -7);
		
		session.execute(boundStatement.bind(imageByteBuffer, "fdgh", "sdfsdf", "sad", imageByteBuffer,"", calender.getTime()));

		ResultSet imageTest = session.execute("SELECT post_pic FROM righteous.post_from_user_id WHERE post_user_id = 'sdfsdf'");
		
		Row row = imageTest.one();
		String fileLocation = "/home/hub/Desktop/" + fileDetail.getFileName();

		byte[] bytes = null;
		try {
			FileOutputStream out = new FileOutputStream(new File(fileLocation));
			int read = 0;
			
			out = new FileOutputStream(new File(fileLocation));
			
			WritableByteChannel channel = Channels.newChannel(out);

			channel.write(row.getBytes("post_pic"));
			
			out.flush();
			out.close();
		} catch (IOException e) {
			log.error("", e);
		}
		String output = "File successfully uploaded to : " + fileLocation;
		
		return Response.status(200).entity(bytes).build();
	}
	
	public String writeToFile(InputStream uploadedInputStream, FormDataContentDisposition fileDetail){
		String fileLocation = "/home/hub/Desktop/" + fileDetail.getFileName();

		try {
			FileOutputStream out = new FileOutputStream(new File(fileLocation));
			int read = 0;
			byte[] bytes = new byte[1024];
			out = new FileOutputStream(new File(fileLocation));
			while ((read = uploadedInputStream.read(bytes)) != -1) {
				out.write(bytes, 0, read);
			}
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		String output = "File successfully uploaded to : " + fileLocation;
		
		return output;
	}
}
