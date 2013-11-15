package helpers;

import java.net.UnknownHostException;
import java.util.ArrayList;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.QueryBuilder;
import com.mongodb.BasicDBList;

public class MongoControlCenter {

	private MongoClient mongoClient;
	private DB db;

	public MongoControlCenter(String address, int port)
			throws UnknownHostException {

		mongoClient = new MongoClient(address, port);

	}

	/**
	 * 
	 * Sets the database of the Mongo DB controller.
	 * 
	 * @param dbName
	 *            The name of the Database to access
	 */
	public void setDatabase(String dbName) {
		db = mongoClient.getDB(dbName);
	}

	/**
	 * 
	 * Closes the connection to the database.
	 */
	public void closeConnection() {
		mongoClient.close();
	}

	public Object[] getEventsForUser(String user) {

		DBObject query = new QueryBuilder()
				.or(new BasicDBObject("assignee", user))
				.or(new BasicDBObject("watchers", user))
				.or(new BasicDBObject("reporter", user))
				.or(new BasicDBObject("businessOwner", user)).get();

		// ArrayList<Integer> ids = new ArrayList<Integer>();

		BasicDBList ids = new BasicDBList();

		BasicDBObject eventQuery = new BasicDBObject("entity.entityId",
				new BasicDBObject("$in", ids));

		DBCollection init = db.getCollection("initiatives");
		DBCollection risks = db.getCollection("risks");
		DBCollection mile = db.getCollection("milestones");
		DBCollection event = db.getCollection("events");

		ArrayList<DBObject> result = new ArrayList<DBObject>();

		DBCursor c = init.find(query);
		DBObject t;
		try {
			while (c.hasNext()) {
				t = c.next();
				ids.add(((BasicDBObject) t).getInt("entityId"));
			}
		} finally {
			c.close();
		}

		c = event.find(eventQuery.append("entity.entityType", "INITIATIVE"));

		try {
			while (c.hasNext()) {
				result.add(c.next());
			}
		} finally {
			c.close();
		}

		ids = new BasicDBList();

		c = risks.find(query);
		try {
			while (c.hasNext()) {
				t = c.next();
				// System.out.println(((BasicDBObject)t).getInt("entityId"));
				ids.add(((BasicDBObject) t).getInt("entityId"));
			}
		} finally {
			c.close();
		}
		eventQuery = new BasicDBObject("entity.entityId", new BasicDBObject(
				"$in", ids));
		c = event.find(eventQuery.append("entity.entityType", "RISK"));

		try {
			while (c.hasNext()) {
				result.add(c.next());
			}
		} finally {
			c.close();
		}

		ids = new BasicDBList();
		c = mile.find(query);
		try {
			while (c.hasNext()) {
				ids.add(((BasicDBObject) c.next()).getInt("entityId"));
			}
		} finally {
			c.close();
		}
		eventQuery = new BasicDBObject("entity.entityId", new BasicDBObject(
				"$in", ids));
		c = event.find(eventQuery.append("entity.entityType", "MILESTONE"));

		try {
			while (c.hasNext()) {
				result.add(c.next());
			}
		} finally {
			c.close();
		}

		return result.toArray();

	}

	@SuppressWarnings("unchecked")
	public Object[] getTeamEventsForUser(String user) {

		DBObject query = new BasicDBObject("username", user);

		DBCollection users = db.getCollection("users");

		ArrayList<String> groups = new ArrayList<String>();

		DBCursor cursor = users.find(query);

		try {
			while (cursor.hasNext()) {
				groups = (ArrayList<String>) cursor.next().get("groups");
			}
		}

		finally {
			cursor.close();
		}

		ArrayList<DBObject> teamEntities = new ArrayList<DBObject>();
		BasicDBList ids = new BasicDBList();

		BasicDBObject eventQuery = new BasicDBObject("entity.entityId",
				new BasicDBObject("$in", ids));

		for (String group : groups) {

			DBObject teamQuery = new QueryBuilder()
					.or(new BasicDBObject("businessGroups", group))
					.or(new BasicDBObject("providerGroups", group)).get();

			DBCollection event = db.getCollection("events");

			DBCollection init = db.getCollection("initiatives");

			DBCursor teamCursor = init.find(teamQuery);
			DBObject t;
			try {
				while (teamCursor.hasNext()) {
					t = teamCursor.next();
					ids.add(((BasicDBObject) t).getInt("entityId"));
				}
			} finally {
				teamCursor.close();
			}

			teamCursor = event.find(eventQuery.append("entity.entityType",
					"INITIATIVE"));

			try {
				while (teamCursor.hasNext()) {
					teamEntities.add(teamCursor.next());
				}
			} finally {
				teamCursor.close();
			}

			ids.clear();

			DBCollection risks = db.getCollection("risks");

			teamCursor = risks.find(teamQuery);

			try {
				while (teamCursor.hasNext()) {
					t = teamCursor.next();
					ids.add(((BasicDBObject) t).getInt("entityId"));
				}
			} finally {
				teamCursor.close();
			}

			eventQuery = new BasicDBObject("entity.entityId",
					new BasicDBObject("$in", ids));
			teamCursor = event.find(eventQuery.append("entity.entityType",
					"RISK"));

			try {
				while (teamCursor.hasNext()) {
					teamEntities.add(teamCursor.next());
				}
			} finally {
				teamCursor.close();
			}

			ids.clear();

			DBCollection mile = db.getCollection("milestones");

			teamCursor = mile.find(teamQuery);

			try {
				while (teamCursor.hasNext()) {
					t = teamCursor.next();
					ids.add(((BasicDBObject) t).getInt("entityId"));
				}
			} finally {
				teamCursor.close();
			}

			eventQuery = new BasicDBObject("entity.entityId",
					new BasicDBObject("$in", ids));
			teamCursor = event.find(eventQuery.append("entity.entityType",
					"MILESTONE"));

			try {
				while (teamCursor.hasNext()) {
					teamEntities.add(teamCursor.next());
				}
			} finally {
				teamCursor.close();
			}

		}

		return teamEntities.toArray();
	}

	/**
	 * 
	 * Gets all the entities the supplied user is a part of.
	 * 
	 * 
	 * @param user
	 *            The user supplied.
	 * @return An array of entity documents the user belongs to.
	 */
	public Object[] getInitiativesByUser(String user) {

		DBObject query = new QueryBuilder()
				.or(new BasicDBObject("assignee", user))
				.or(new BasicDBObject("watchers", user))
				.or(new BasicDBObject("reporter", user))
				.or(new BasicDBObject("businessOwner", user)).get();
		DBCollection coll = db.getCollection("initiatives");

		DBCursor cursor = coll.find(query);

		ArrayList<DBObject> list = new ArrayList<DBObject>();

		try {
			while (cursor.hasNext()) {
				list.add(cursor.next());
			}
		} finally {
			cursor.close();
		}
		return list.toArray();

	}

	/**
	 * 
	 * Gets all of the documents in a specific collection
	 * 
	 * @param collection
	 *            The collection being queried.
	 * @return An array of all documents in that collection.
	 */
	public Object[] getAllDocuments(String collection) {
		DBCollection coll = db.getCollection(collection);

		DBCursor cursor = coll.find();
		ArrayList<DBObject> list = new ArrayList<DBObject>();

		try {
			while (cursor.hasNext()) {
				list.add(cursor.next());
			}
		} finally {
			cursor.close();
		}
		return list.toArray();
	}

}
