package com.dropouts.righteouslk.db;

import com.datastax.driver.core.Session;

public class DatabaseCreator {
	
	private DatabaseManager databaseManager;
	private Session session;
	
	public DatabaseCreator(){
		databaseManager = DatabaseManager.getInstance();
		session = databaseManager.getSession();
	}
	
	public void create() {
		
		session.execute(
                "CREATE TABLE IF NOT EXISTS righteous.user"
                + " (" +
                        "user_id text," +
                        "user_name text," +
                        "user_password text," +
                        "user_dob timestamp," +
                        "user_gender text," +
                        "user_location text," +
                        "user_pic blob," +
                        "PRIMARY KEY(user_id)" +
                        ");");
		
		
		session.execute(
				"CREATE TABLE IF NOT EXISTS righteous.post_from_user_id (" +
						"post_pic blob," +
						"post_str text," +
						"post_user_id text," +
						"post_user_name text," +
						"post_user_pic blob," +
						"post_location text," +
						"post_timestamp timestamp," +
						"PRIMARY KEY(post_user_id, post_timestamp))"+
				"WITH CLUSTERING ORDER BY (post_timestamp DESC);");
		
		
		session.execute(
				"CREATE TABLE IF NOT EXISTS righteous.user_following_users (" +
						"user_id text," +
						"folliwing_user_id text," +
						"PRIMARY KEY(user_id, folliwing_user_id));");
		
	}
}
