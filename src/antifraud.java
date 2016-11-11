import java.util.*;
import java.io.*;

public class antifraud {

	/*
	 * This function creates a graph by reading in id1 and id2 values from
	 * batch_input and has the information of different users of PayMo stored as
	 * keys in a HashMap and a Person object as Value which contains information
	 * about the user like his userID, an adjacency list of all his
	 * friends(immediate connection).
	 */
	public static void createGraph(HashMap<Integer, Person> people, String file) {
		/* Reads the edges present in the graph */
		Scanner reader = null;
		String line;
		String[] splitLine;
		int source, destination;
		try {
			reader = new Scanner(new FileReader(file));
			reader.nextLine();
			while (reader.hasNext()) {
				line = reader.useDelimiter("\n").next();
				splitLine = line.split(",");
				source = Integer.parseInt(splitLine[1].trim());
				destination = Integer.parseInt(splitLine[2].trim());
				Person sender = new Person(source);
				Person receiver = new Person(destination);
				createEdge(people, sender, receiver);
			}
			reader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	 * This function updates the HashMap by adding the sender on key side and
	 * appending the receiver in the friend list of sender and also by adding
	 * the receiver on key side and appending the sender in the friend list of
	 * receiver Before creating a new edge it does a check if the edge already
	 * exists.
	 */
	public static void createEdge(HashMap<Integer, Person> people,
			Person sender, Person receiver) {
		if (people.containsKey(sender.getID())
				&& people.get(sender.getID()).getFriends()
						.contains(receiver.getID())) {
			return;
		}
		if (people.containsKey(sender.getID())) {
			people.get(sender.getID()).addFriend(receiver.getID());

		} else {
			sender.addFriend(receiver.getID());
			people.put(sender.getID(), sender);
		}
		if (people.containsKey(receiver.getID())) {
			people.get(receiver.getID()).addFriend(sender.getID());
		} else {
			receiver.addFriend(sender.getID());
			people.put(receiver.getID(), receiver);
		}

	}

	/*
	 * This function prints the adjacency list for all the users in the
	 * batch_input implemented for better understanding the graph structure
	 * visually and not part of solution
	 */

	// public static void printNetworkGraph(HashMap<Integer, Person> people) {
	// BufferedWriter writer = null;
	// try {
	// writer = new BufferedWriter(new FileWriter("paymo_input/output"));
	// for (Map.Entry<Integer, Person> entry : people.entrySet()) {
	// writer.write(entry.getKey() + "->");
	// List<Integer> edgeList = people.get(entry.getKey())
	// .getFriends();
	// // writer.write(edgeList.size() + "");
	// // writer.newLine();
	// for (int i = 0; i < edgeList.size(); i++) {
	// if (i != edgeList.size() - 1) {
	// writer.write(edgeList.get(i) + "->");
	// } else {
	// writer.write(edgeList.get(i) + "");
	// writer.newLine();
	// }
	// }
	// }
	// writer.close();
	// } catch (IOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }

	/*
	 * After constructing the graph based on batch_input it gives the current
	 * state of the Paymo network then we process stream_input transactions and
	 * classify the transactions as trusted or unverified depending on whether
	 * they are friends(feature1) or friends of friends(feature2) or are within
	 * 4 degree of separation.
	 */

	/*
	 * Feature1 i.e. to check if sender and receiver are friends is Implemented
	 * by checking if receiver is present in the friend list of sender in
	 * HashMap.
	 */
	public static boolean feature1(HashMap<Integer, Person> people,
			Integer source, Integer destination) {
		if (people.containsKey(source) && people.containsKey(destination)) {
			return people.get(source).getFriends().contains(destination);
		} else {
			return false;
		}
	}

	/*
	 * Feature2 i.e. to check if sender and receiver are friends of friends is
	 * Implemented by checking if the friend list of sender and the friend list
	 * of receiver have any common element.
	 */
	public static boolean feature2(HashMap<Integer, Person> people,
			Integer source, Integer destination) {
		if (people.containsKey(source) && people.containsKey(destination)) {
			List<Integer> sourceEdgeList = people.get(source).getFriends();
			List<Integer> destinationEdgeList = people.get(destination)
					.getFriends();
			return !Collections.disjoint(sourceEdgeList, destinationEdgeList);
		} else {
			return false;
		}
	}

	/*
	 * Feature3 i.e. to check if sender and receiver are within 4 degree of
	 * separation or not. This is implemented by using Bidirectional Breadth
	 * First Search i.e. by doing two breadth-first searches one from the source
	 * and one from the destination and when the search collide it indicates we
	 * have found a path.
	 */
	public static boolean feature3(HashMap<Integer, Person> people,
			Integer source, Integer destination) {
		if (people.containsKey(source) && people.containsKey(destination)) {
			int degree = findDegreeBiBFS(people, source, destination);
			return (degree > 0 && degree <= 4);
		} else {
			return false;
		}
	}

	/*
	 * Feature4 gives the activity summary of every user in the network by
	 * keeping a running count of 6 types of metrics while going through the
	 * stream_input. We have a network structure before reading the stream_input
	 * data capturing all the connections now we can have stream_input file
	 * collected for a specific time of interest to capture user activity during
	 * that time.The 6 counts stored in Person object are:
	 * 1)NumOfTransactionsToFriends: The number of transactions in which a user
	 * sent money to his/her friends. 2)NumOfTransactionsToFriendsOfFriends: The
	 * number of transactions in which a user sent money to friend of his/her
	 * friends. 3)NumOfTransactionsToPeopleOutsideFourthDegree: The number of
	 * transactions in which a user sent money to someone outside of 4th degree
	 * of connections this according to me is an important metric because if
	 * someone with a high value of the same i.e. is constantly sending money
	 * outside of 4 degree of separation this might be a fraud user some
	 * research on net showed that most of the fraud users using apps like Venmo
	 * and Cash app buy things on sites like Craiglist from unknown people and
	 * send money and then cancel the transaction due to which although the
	 * seller i.e. the receiver gets an in app notification conforming receipt
	 * of funds but the actual money is not credited in the account.
	 * 4)NumOfTransactionsFromFriends: The number of transactions in which a
	 * user received money from his/her friends.
	 * 5)NumOfTransactionsFromFriendsOfFriends: The number of transactions in
	 * which a user received money from his/her friends of friends.
	 * 6)NumOfTransactionsFromPeopleOutsideFourthDegree: The number of
	 * transactions in which a user received money from someone outside of 4th
	 * degree of connections this according to me is an important metric because
	 * if someone is receiving money consistently outside of 4 degree of
	 * separation this might suggest a fraud user. These metrics can be used as
	 * features to create a training data set to build a classifier to classify
	 * users into fraud or not fraud. .
	 */
	public static void feature4(HashMap<Integer, Person> people, String file) {
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(file));
			writer.write("UserID,NumOfTransactionsToFriends,NumOfTransactionsToFriendsOfFriends,NumOfTransactionsToPeopleOutsideFourthDegree,"
					+ "NumOfTransactionsFromFriends,NumOfTransactionsFromFriendsOfFriends,NumOfTransactionsFromPeopleOutsideFourthDegree\n");
			for (Map.Entry<Integer, Person> entry : people.entrySet()) {
				Integer userId = entry.getKey();
				Person person = entry.getValue();
				writer.write(userId + "\t" + person.getSendToFriend() + "\t"
						+ person.getSendToFriendOfFriend() + "\t"
						+ person.getSendToOutsideFourthDegree() + "\t"
						+ person.getRecFromFriend() + "\t"
						+ person.getRecFromFriendOfFriend() + "\t"
						+ person.getRecOutsideFourthDegree() + "\n");
			}
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	 * This function reads through the stream_input and evaluates each
	 * transaction depending on feature1, feature2 and feature3 and classifies
	 * the transactions into trusted or unverified and writes it to the files in
	 * paymo_output directory.
	 */
	public static void fraudCheck(HashMap<Integer, Person> people,
			String streamFile, String outputFile1, String outputFile2,
			String outputFile3, String outputFile4) {
		Scanner reader = null;
		BufferedWriter writer1 = null, writer2 = null, writer3 = null;
		String line;
		String[] splitLine;
		int source, destination;
		// // Feature1 and Feature2 and Feature3
		try {

			reader = new Scanner(new FileReader(streamFile));
			writer1 = new BufferedWriter(new FileWriter(outputFile1));
			writer2 = new BufferedWriter(new FileWriter(outputFile2));
			writer3 = new BufferedWriter(new FileWriter(outputFile3));
			reader.nextLine();
			while (reader.hasNext()) {
				line = reader.useDelimiter("\n").next();
				splitLine = line.split(",");
				source = Integer.parseInt(splitLine[1].trim());
				destination = Integer.parseInt(splitLine[2].trim());
				// writer1.write(feature3(people, source, destination) + "\n");
				if (feature1(people, source, destination)) {
					writer1.write("trusted");
					writer1.newLine();

					writer2.write("trusted");
					writer2.newLine();

					writer3.write("trusted");
					writer3.newLine();

					people.get(source).countSTF();
					people.get(destination).countRFF();

				} else {
					writer1.write("unverified");
					writer1.newLine();

					if (feature2(people, source, destination)) {
						writer2.write("trusted");
						writer2.newLine();

						writer3.write("trusted");
						writer3.newLine();

						people.get(source).countSTFOF();
						people.get(destination).countRFFOF();

					} else {
						writer2.write("unverified");
						writer2.newLine();
						if (feature3(people, source, destination)) {
							writer3.write("trusted");
							writer3.newLine();

						} else {
							writer3.write("unverified");
							writer3.newLine();
							if (people.containsKey(source)
									&& people.containsKey(destination)) {
								people.get(source).countSTOFD();
								people.get(destination).countROFD();
							}
						}

					}
				}
			}
			reader.close();
			writer1.close();
			writer2.close();
			writer3.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		feature4(people, outputFile4);
	}

	public static LinkedList<Person> mergePaths(BFSData bfs1, BFSData bfs2,
			int connection) {
		PathNode end1 = bfs1.visited.get(connection); // end1 -> source
		PathNode end2 = bfs2.visited.get(connection); // end2 -> dest
		LinkedList<Person> pathOne = end1.collapse(false); // forward: source ->
															// connection
		LinkedList<Person> pathTwo = end2.collapse(true); // reverse: connection
															// -> dest

		pathTwo.removeFirst(); // removing connection
		pathOne.addAll(pathTwo); // add second path
		return pathOne;
	}

	/* Searches one level and return collision, if any. */
	public static Person searchLevel(HashMap<Integer, Person> people,
			BFSData primary, BFSData secondary) {
		/*
		 * We only want to search one level at a time. Count how many nodes are
		 * currently in the primary's level and only do that many nodes. We'll
		 * continue to add nodes to the end.
		 */
		int count = primary.toVisit.size();
		for (int i = 0; i < count; i++) {
			/* Pulling out first node. */
			PathNode pathNode = primary.toVisit.poll();
			int personId = pathNode.getPerson().getID();

			/* Checking if it is already been visited. */
			if (secondary.visited.containsKey(personId)) {
				return pathNode.getPerson();
			}

			/* Adding friends to queue. */
			Person person = pathNode.getPerson();
			ArrayList<Integer> friends = person.getFriends();
			for (int friendId : friends) {
				if (!primary.visited.containsKey(friendId)) {
					Person friend = people.get(friendId);
					PathNode next = new PathNode(friend, pathNode);
					primary.visited.put(friendId, next);
					primary.toVisit.add(next);
				}
			}
		}
		return null;
	}

	/* This function returns the degree of separation of the nodes */
	public static int findDegreeBiBFS(HashMap<Integer, Person> people,
			int source, int destination) {
		BFSData sourceData = new BFSData(people.get(source));
		BFSData destData = new BFSData(people.get(destination));

		while (!sourceData.isFinished() && !destData.isFinished()) {
			/* Searching out from source. */
			Person collision = searchLevel(people, sourceData, destData);
			if (collision != null) {
				return mergePaths(sourceData, destData, collision.getID())
						.size() - 1;
			}

			/* Searching out from destination. */
			collision = searchLevel(people, destData, sourceData);
			if (collision != null) {
				return mergePaths(sourceData, destData, collision.getID())
						.size() - 1;
			}
		}
		return 0;
	}

	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();
		HashMap<Integer, Person> people = new HashMap<Integer, Person>();
		createGraph(people, args[0]);
		long graphTime = System.currentTimeMillis();
		long graphCreationTime = graphTime - startTime;
		System.out.println("Time taken to create Network graph: "
				+ graphCreationTime / 1000 + "sec");
		fraudCheck(people, args[1], args[2], args[3], args[4], args[5]);
		long fraudTime = System.currentTimeMillis();
		long fraudCheckTime = fraudTime - startTime;
		System.out.println("Time taken to implement all the features: "
				+ fraudCheckTime / 1000 + "sec");
	}

}
