#Insight Data Engineering - Coding Challenge 
####Submitted by: Vaibhav Lella

# Table of Contents

1. [Challenge Summary] (README.md#challenge-summary)
2. [Details of Implementation] (README.md#details-of-implementation)
3. [Description of Data] (README.md#description-of-data)
4. [Description of Solution] (README.md#description-of-solution)

##Challenge Summary

Imagine you're a data engineer at a "digital wallet" company called PayMo that allows users to easily request and make payments to other PayMo users. The team at PayMo has decided they want to implement features to prevent fraudulent payment requests from untrusted users. 

###Feature 1
When anyone makes a payment to another user, they'll be notified if they've never made a transaction with that user before.

* "unverified: You've never had a transaction with this user before. Are you sure you would like to proceed with this payment?"

###Feature 2
The PayMo team is concerned that these warnings could be annoying because there are many users who haven't had transactions, but are still in similar social networks. 

For example, User A has never had a transaction with User B, but both User A and User B have made transactions with User C, so User B is considered a "friend of a friend" for User A.

For this reason, User A and User B should be able to pay each other without triggering a warning notification since they're "2nd degree" friends. 

<img src="./images/friend-of-a-friend1.png" width="500">

To account for this, PayMo would like you to also implement this feature. When users make a payment, they'll be notified of when they're not "a friend of a friend".

* "unverified: This user is not a friend or a "friend of a friend". Are you sure you would like to proceed with this payment?"


###Feature 3
More generally, PayMo would like to extend this feature to larger social networks. Implement a feature to warn users only when they're outside the "4th degree friends network".

<img src="./images/fourth-degree-friends2.png" width="600">

In the above diagram, payments have transpired between User

* A and B 
* B and C 
* C and D 
* D and E 
* E and F

Under this feature, if User A were to pay User E, there would be no warning since they are "4th degree friends". 

However, if User A were to pay User F, a warning would be triggered as their transaction is outside of the "4th-degree friends network."

(Note that if User A were to pay User C instead, there would be no warning as they are "2nd-degree" friends and within the "4th degree network") 

###Other considerations and optional features

It's critical that these features don't take too long to run. For example, if it took 5 seconds when you make a payment to check whether a user is in your network, that would ruin your user experience and wouldn't be acceptable.


While PayMo is a fictional company, the dataset is quite interesting -- it's inspired by a real social network, includes the time of transaction, and the messages come from real Venmo transactions -- so feel free to implement additional features that might be useful to prevent fraudulent payments.

##Details of implementation

[Back to Table of Contents] (README.md#table-of-contents)

With this coding challenge, you should demonstrate strong understanding of computer science fundamentals. We won't be wowed by your knowledge of various available software libraries but will be impressed by your ability to pick and use the best data structures and algorithms for the job. 

We're looking for clean, well-thought-out code that correctly implements the desired features in an optimized way.

We also want to see how you use your programming skills to solve business problems. At a minimum, you should implement the three required features but feel free to expand upon this challenge or add other features you think would prevent fraud and further business goals. Be sure to document these add-ons so we know to look for them. 

###Input

Ideally, payment data would come from a real-time, streaming API, but we don't want this challenge to focus on the relatively uninteresting "DevOps" of connecting to an API.

As a result, you may assume that collecting the payments has been done for you and the data resides in two comma-delimited files in the `paymo_input` directory. 

The first file, `batch_payment.txt`, contains past data that can be used to track users who have previously paid one another. These transactions should be used to build the initial state of the entire user network.

Data in the second file, `stream_payment.txt` should be used to determine whether there's a possibility of fraud and a warning should be triggered.

You should assume that each new line of `stream_payment.txt` corresponds to a new, valid PayMo payment record -- regardless of being 'unverified' -- and design your program to handle a text file with a large number of payments. 

###Output

Your code should process each line in `stream_payment.txt` and for each payment, output a line containing one of two words, `trusted` or `unverified`. 

`trusted` means the two users involved in the transaction have previously paid one another (when implementing Feature 1) or are part of the "friends network" (when implementing Feature 2 and 3).

`unverified` means the two users have not previously been involved in a transaction (when implementing Feature 1) or are outside of the "friends network" (when implementing Features 2 and 3)

The output should be written to a text file in the `paymo_output` directory. Because we are asking you to implement a minimum of three features, your program should output to at least three text files in the `paymo_output` directory. 

Each output file should be named after the applicable feature you implemented (i.e., `output1.txt`, `output2.txt` and `output3.txt`)

For example, if there were 5 lines of transactions in the `stream_payment.txt`, then the following `output1.txt` file for Feature 1 could look like this: 

	trusted
	trusted
	unverified
	unverified
	trusted


##Description of Data

[Back to Table of Contents] (README.md#table-of-contents)

The `batch_payment.txt` and `stream_payment.txt` input files are formatted the same way.

As you would expect of comma-separated-value files, the first line is the header. It contains the names of all of the fields in the payment record. In this case, the fields are 

* `time`: Timestamp for the payment 
* `id1`: ID of the user making the payment 
* `id2`: ID of the user receiving the payment 
* `amount`: Amount of the payment 
* `message`: Any message the payer wants to associate with the transaction

Following the header, you can assume each new line contains a single new PayMo payment record with each field delimited by a comma. In some cases, the field can contain Unicode as PayMo users are fond of placing emojis in their messages. For simplicity's sake, you can choose to ignore those emojis.

For example, the first 10 lines (including the header) of `batch_payment.txt` or `stream_payment.txt` could look like: 

	time, id1, id2, amount, message
	2016-11-02 09:49:29, 52575, 1120, 25.32, Spam
	2016-11-02 09:49:29, 47424, 5995, 19.45, Food for 🌽 😎
	2016-11-02 09:49:29, 76352, 64866, 14.99, Clothing
	2016-11-02 09:49:29, 20449, 1552, 13.48, LoveWins
	2016-11-02 09:49:29, 28505, 45177, 19.01, 🌞🍻🌲🏔🍆
	2016-11-02 09:49:29, 56157, 16725, 4.85, 5
	2016-11-02 09:49:29, 25036, 24692, 20.42, Electric
	2016-11-02 09:49:29, 70230, 59830, 19.33, Kale Salad
	2016-11-02 09:49:29, 63967, 3197, 38.09, Diner
	 
##Description of Solution
* First I created a graph by treating each person as a Node and letting an edge between two nodes indicate that the two users are friends i.e they had a transaction as given in batch_input. This was implemented by reading in id1 and id2 values from batch_input, then I created a HashMap with userID's as Keys and a Person object as Value which contains information about the user like his/her userID, an adjacency list of all the friends of the user.

* After constructing the graph based on batch_payment it gives the current state of the Paymo network then we process stream_input transactions and classify the transactions as trusted or unverified depending on whether they are friends(feature1) or friends of friends(feature2) or are within 4 degree of separation(feature3).
####There is a bit of ambiguity in the question due to which I am assuming that once I have constructed by initial state of network by reading in batch_payment, now while classifying payments in stream_payment I am not making changes to the graph already constructed instead keeping the graph structure constant and just classifying the new stream payments. Although I can implement this in my existing code with only a few changes to make the graph dynamic while reading stream payments.  

* Feature 1 i.e. to check if sender and receiver are friends is Implemented by checking if receiver is present in the friend list of sender in the HashMap.

* Feature 2 i.e. to check if sender and receiver are friends of friends is Implemented by checking if the friend list of sender and the friend list of receiver have any common element.

* Feature 3 i.e. to check if sender and receiver are within 4 degree of separation or not. This is implemented by using Bidirectional Breadth First Search i.e. by doing two breadth-first searches one from the source and one from the destination and when the search collide it indicates we have found a path.

* Feature 4(Extra Feature) gives the activity summary of every user in the network by keeping a running count of 6 types of metrics while going through the stream_input. We have a network structure before reading the stream_input data capturing all the connections now we can have stream_input file collected for a specific time of interest to capture user activity during that time.The 6 counts stored in Person object are:

   * `NumOfTransactionsToFriends`: The number of transactions in which a user sent money to his/her friends.
   * `NumOfTransactionsToFriendsOfFriends`:The number of transactions in which a user sent money to friend of his/her friends. 
   * `NumOfTransactionsToPeopleOutsideFourthDegree`: The number of transactions in which a user sent money to someone outside of 4th 		degree of connections this according to me is an important metric because if someone with a high value of the same i.e. is 		constantly sending money outside of 4 degree of separation this might be a fraud user some research on net showed that most of the 	   fraud users using apps like Venmo and Cash app buy things on sites like Craiglist from unknown people and
	send money and then cancel the transaction due to which although the seller i.e. the receiver gets an in app notification 		conforming receipt of funds but the actual money is not credited in the account.
    * `NumOfTransactionsFromFriends`: The number of transactions in which a user received money from his/her friends.
    * `NumOfTransactionsFromFriendsOfFriends`: The number of transactions in which a user received money from his/her friends of friends.
    * `NumOfTransactionsFromPeopleOutsideFourthDegree`: The number of transactions in which a user received money from someone outside of 	  4th degree of connections this according to me is an important metric because if someone is receiving money consistently outside 	   of 4 degree of separation this might suggest a fraud user. These metrics can be used as features to create a training data set to 	     build a classifier to classify users into fraud or not fraud.
    
