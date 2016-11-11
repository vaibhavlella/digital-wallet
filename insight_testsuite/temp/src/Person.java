import java.util.ArrayList;

public class Person {
	private ArrayList<Integer> friends = new ArrayList<Integer>();
	private int personID;
	private int sendToFriend = 0;
	private int sendToFriendOfFriend = 0;
	private int sendToOutsideFourthDegree = 0;
	private int recFromFriend = 0;
	private int recFromFriendOfFriend = 0;
	private int recOutsideFourthDegree = 0;

	public void countSTF() {
		sendToFriend++;
	}

	public void countSTFOF() {
		sendToFriendOfFriend++;
	}

	public void countSTOFD() {
		sendToOutsideFourthDegree++;
	}

	public void countRFF() {
		recFromFriend++;
	}

	public void countRFFOF() {
		recFromFriendOfFriend++;
	}

	public void countROFD() {
		recOutsideFourthDegree++;
	}

	public int getSendToFriend() {
		return sendToFriend;
	}

	public int getSendToFriendOfFriend() {
		return sendToFriendOfFriend;
	}

	public int getSendToOutsideFourthDegree() {
		return sendToOutsideFourthDegree;
	}

	public int getRecFromFriend() {
		return recFromFriend;
	}

	public int getRecFromFriendOfFriend() {
		return recFromFriendOfFriend;
	}

	public int getRecOutsideFourthDegree() {
		return recOutsideFourthDegree;
	}

	public ArrayList<Integer> getFriends() {
		return friends;
	}

	public int getID() {
		return personID;
	}

	public void addFriend(int id) {
		friends.add(id);
	}

	public Person(int id) {
		this.personID = id;
	}
}
