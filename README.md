Yanfeng Jin (Tony) and Uriel Ulloa
All group members were present and contributing during all work on this project
We have neither given nor received any unauthorized aid in this assignment. 

SentAnalysis.java and SentAnalysisBest.java are required to run the program.
No known bugs. Even though the accuracy is fairly low for both classifiers. 

For the original classifier, we tested it on amazon reviews on a laptop charger and movie reviews. Here are three examples where it failed: 

“I really love the long cord on this adapter. It works as well as the original that came with the computer. It does not get hot to the touch like some replacements I have had in the past. Highly recommend.”
Incorrectly classified as negative. This might be because the sentence “It does not get hot to the touch like some replacements I have had in the past”, which contains words that might be considered as negative. 

“I am now trying to find words to describe this movie for an hour. I couldn't. You've seen it, or you haven't. It's monumental and outrageously good.”
Incorrectly classified as negative. It might be difficult for the classifier because it has words like “couldn’t”, “haven’t”, “outrageously”. Without the context, the classifier might not be able to tell if it is positive or not. 

“Received on time. Works great.”
Incorrectly classified as negative. This sentence might be difficult to classify because it is really short and doesn’t have enough features. 

We tested the accuracy and the precision of the two classifiers:

Original classifier:
Accuracy: 0.5665207636002502
Positive Precision: 0.8852173913043478
Negative Precision: 0.5288334978611385


Best classifier:
Accuracy: 0.5804980321477177
Positive Precision: 0.9010819165378671
Negative Precision: 0.5371993987975952

The best classifier does slightly better than the original classifier, even though the total accuracy is still not satisfying (but above chance). Our system has a fairly good positive precision, but when it comes to negative precision, the number falls to about 50%. During our testing, we’ve also discovered that our classifier gives a lot of false negatives. We think one reason that it doesn’t perform well is that we converted everything into lowercase. However, it’s likely that people tend to write certain sentences in uppercase, especially in negative reviews. Therefore, some words in upper cases were labeled negative, even though the words themselves don’t have a negative connotation (it’s the fact that the words were in upper case that made it negative). In order to improve the system, we can add the amount of capitalization as another feature. We can also use features like document length, punctuations (which we ignored as well) to further improve the system. 
