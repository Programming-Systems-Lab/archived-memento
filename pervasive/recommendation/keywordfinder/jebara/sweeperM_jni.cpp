#include <string.h>
#include <stdlib.h>
#include <stdio.h>
#include <time.h>
#include <sys/types.h>
#include <netinet/in.h>
#include <netdb.h>
#include <fstream>
#include <strstream>
#include <termios.h>
#include <unistd.h>
#include <iostream>
#include <map.h>

extern "C" {
#include "quickmatrix.cpp"
}
#include <math.h>

// JC(2/28/2003):Added headers
#include <vector>
#include "psl_memento_pervasive_recommendation_keywordfinder_jebara_impKeywordFinder.h"
using namespace std;

#define  MAXLINE  512

// JC(2/28/2003):Add this function to parse incoming string data.
vector<string> *tokenizeString(string const str, string const delim);

/* JC(2/26/2003): These are existing variables, I didn't add them. Just adding 
 * some comments as note to self--> THese are used in the calculations, 
 * and set to some default value in case they are not set on the command line.  
 */  
double st1 = 0.5;
double st2 = 0.5;
double tr1 = 0.5;
double tr2 = 0.5;
double col1 = 0.5;
double col2 = 0.5;
double col3 = 0.5;

int      width    = 200;
int      height   = 200;
double   decay    = 0.9;
int      cTotal   = 0;  // JC(2/26/2003):The number of topics that the program must discern among. (aka how many training sets were passed in the tFile cmd line arg?)

char    *cNames[MAXLINE];
char    *cQuestions[MAXLINE];


char     tFile[MAXLINE];
char     qFile[MAXLINE];
char     cFile[MAXLINE];
char     sFile[MAXLINE];

Vector   cCounts;
Vector   logWeights;
Vector   probs;
Matrix   graph1;
Matrix   graph2;

map<string,double>   tab; // JC(2/26/2003):Data populated by loadStats() 
map<string,double *> classes;// JC(2/26/2003):Data populated by loadStats() 

void
loadStats(char *sFile, map<string,double *> &classes,map<string,double> &tab )
{
  FILE   *fp;
  char    line[MAXLINE];
  char    b2[MAXLINE];
  char    b3[MAXLINE];
  double *p;
  int     c;
  int     totwords = 0;

  if ((fp = fopen(sFile,"r"))==NULL)
    {
      fprintf(stdout,"I can't open %s for input.\n",sFile);
      exit(0);
    }
   while (fgets(line, MAXLINE, fp) != NULL)
    {
      if (strlen(line)>1)
	{
	  istrstream ist(line,strlen(line));
 	  ist>>b2;
	  // JC(2/27/2003):Not sure what this next line is for. Seems to have no impact so commenting it out. 
 	  //map<string,double *>::iterator it = classes.find(b2); 
 	  classes[b2] = (double *) malloc(cTotal*sizeof(double));
 	  p           = classes[b2];
 	  for (c=0; c<cTotal; c++)
 	    {
 	      ist>>b3;
 	      p[c]  = log(atof(b3)) - log(cCounts->d[c]);
 	    }
	  //JC(2/27/2003):Not sure what this next line is for.Seems to have no impact (after compiling with it commented out). 
 	  //map<string, double>::iterator it2 = tab.find(b2);
	  //JC(2/27/2003):Every word is given an entry in the history, where the number of occurances is initialized to 0.  
 	  tab[b2] = 0.0;
 	}
      totwords++; //JC(2/27/2003):This variables isn't used either... maybe just for debugging?
     }
   fclose(fp);
}

void
loadCounts(char *cFile)
{
  FILE *fp;
  char  line[MAXLINE];
   if ((fp = fopen(cFile,"r"))==NULL)
    {
      fprintf(stdout,"I can't open %s for input.\n",cFile);
      exit(0);
    }

  int i = 0;
  for (i=0; i<cTotal; i++)
    {
      fgets(line, MAXLINE, fp);
      cCounts->d[i]    = atof(line);
      logWeights->d[i] = log(cCounts->d[i]);
    }
  fclose(fp);
}

void
loadTopics(char *tFile)
{
  FILE *fp;
  char  line[MAXLINE];

  if ((fp = fopen(tFile,"r"))==NULL)
    {
      fprintf(stdout,"I can't open %s for input.\n",tFile);
      exit(0);
    }

  cTotal = 0;
  // fgets(line,MAXLINE,fp);
  while (fgets(line, MAXLINE, fp) != NULL)
    {
      if (strlen(line)>1)
	{
	  cNames[cTotal] = (char *) malloc(MAXLINE*sizeof(char));
	  strcpy(cNames[cTotal],line);
	  cTotal++;
 	}
     }
   fclose(fp);
}



void
loadQuestions(char *qFile)
{
   FILE *fp;
   char  line[MAXLINE];

   if ((fp = fopen(qFile,"r"))==NULL)
     {
       fprintf(stdout,"I can't open %s for input.\n",qFile);
       exit(0);
     }

   int i;
   for (i=0; i<cTotal; i++)
     {
       fgets(line, MAXLINE, fp);
       int ll = strlen(line);
       line[ll-1] = 0; // get rid of trailing \n

       cQuestions[i] = (char *) malloc(MAXLINE*sizeof(char));
       strcpy(cQuestions[i],line);
     }
   fclose(fp);
}


//JC(2/28/2003):Passing in just 1 string instead of argc/argv.  
bool parse_args (string const args)
{
   int i;

   strcpy(tFile, "model.topics");
   strcpy(qFile, "model.questions");
   strcpy(cFile, "model.counts");
   strcpy(sFile, "model.stats");

   vector<string> *words = tokenizeString(args, " "); 
   // Set the command line arguments
   string currArgName;
   char *currArgValue; 
   for(vector<string>::iterator itWords = words->begin(); 
       itWords != words->end(); 
       itWords++) {

     currArgName = (*itWords);
     itWords++;
     currArgValue = (char *)((*itWords).c_str());

     if (currArgName == "-topics") {
       strcpy(tFile, currArgValue);
     }else if (currArgName == "-questions") {
       strcpy(qFile, currArgValue);
     }else if (currArgName == "-counts") {
       strcpy(cFile, currArgValue);
     }else if (currArgName == "-stats") {
       strcpy(sFile, currArgValue);
     }else if (currArgName == "-decay") {
       decay = atof(currArgValue);
     }else if (currArgName == "-st1") {
       st1 = atof(currArgValue);
     }else if (currArgName == "-st2") {
       st2 = atof(currArgValue);
     }else if (currArgName == "-tr1") {
       tr1 = atof(currArgValue);
     }else if (currArgName == "-tr2") {
       tr2 = atof(currArgValue);
     }else if (currArgName == "-col1") {
       col1 = atof(currArgValue);
     }else if (currArgName == "-col2") {
       col2 = atof(currArgValue);
     }else if (currArgName == "-col3") {
       col3 = atof(currArgValue);
     }else{
       //       cout << currArgName << " " << currArgValue << endl;

       cout << "found something unexpected" << endl; 
       delete words;
       return false; // did not parse successfully.
     }
   }
   delete words;
   return true;
 }

// DUMMY MAIN, to make this program compile.
int main(int argc, char **argv)
{
  return 0;
} 


// NEW FUNCTION
JNIEXPORT jboolean JNICALL Java_psl_memento_pervasive_recommendation_keywordfinder_jebara_impKeywordFinder_init
  (JNIEnv *env, jobject obj, jstring str)
{
  // &&& Parse argv  from string to integer

  // Parse Arguments
  const char *args = env->GetStringUTFChars(str, 0);

  //  printf("%s", args);

  if(!parse_args(args)) {
    return JNI_FALSE;
  }

  // Load the topics
  loadTopics(tFile); //JC(2/26/2003):value of tFile comes from cmd line arg: topic file 
  // JC(2/26/2003):The topic file contains the list of files that hold training set for a topic.
  // JC(2/26/2003):Loading topics also sets the value of cTotal

  loadQuestions(qFile); //JC(2/26/2003):value of qFile comes from cmd line arg: questions file 
  /* JC(2/26/2003):Question file contains the list of questions to return for a topic. 
   * If there are 2 files listed in tFile, then there should be 2 questions in qFile. Looks
   * like they correspond by position. E.g. if the program decides that the current conversation
   * contains the words from the first training set, it'll return the first question in the qFile. 
   * (This is the simple explanation. Of course there's a lot of math involved in how the topic 
   * is determined. But the return value, aka keyword returned, is contained in the qFile)
   */

  // Start probability vectors and matrices
  logWeights = VectorCreate(cTotal); // JC(2/26/2003):cTotal is the num of lines in the topics file
  cCounts    = VectorCreate(cTotal); // VectorCreates makes a new uninitialized vector of size cTotal
  probs      = VectorCreate(cTotal);
  graph1     = MatrixCreate(width,cTotal);// JC(2/26/2003):MatrixCreate makes a new unintialized matrix of the given dimensions.
  graph2     = MatrixCreate(width,cTotal);
  VectorSet((Vector) graph1,0.0); // JC(2/26/2003):Initializes all elements in the Vector 0.0 
  VectorSet((Vector) graph2,0.0);

  // Load Rest of Data
  loadCounts(cFile); //JC(2/26/2003):Fills in values for the vectors logWeights and cCounts 
  loadStats(sFile,classes,tab); //JC(2/27/2003):sFile has 3 columns on every line: a word and 2 numbers.
  //JC(2/27/2003): Looks like it's because the stats file is generated from 2 training sets. 
  // Each column represents the number of occurrances of the words in each set.
  // tab was initialized to zero. I think that means tab will be used to keep track of 
  // history of words in the input data. classes has a list of all words in all training sets,
  // and a count of occurance for each word in each training set.

  env->ReleaseStringUTFChars(str, args);

  return JNI_TRUE;
}

JNIEXPORT void JNICALL 
Java_psl_memento_pervasive_recommendation_keywordfinder_jebara_impKeywordFinder_resetConversationHistory
(JNIEnv *, jobject)
{
	cout << "NATIVE: RESET WAS INVOKED" << endl;
	for(map<string,double>::iterator it2=tab.begin(); it2!=tab.end(); ++it2)
	{
	  (*it2).second = 0;
	}
}

// ||||||| NEW FUNCTION
/* JC(2/28/2003):Parameter is a string. Possibly contains multiple words separated by spaces */
JNIEXPORT void JNICALL Java_psl_memento_pervasive_recommendation_keywordfinder_jebara_impKeywordFinder_addConversationWord
  (JNIEnv *env, jobject obj, jstring str)
{
  // **********************************************************************************
  // JC(2/26/2003):The REAL processing begins....
  int     c; // JC(2/26/2003):This is used for for-loops.
  double  N; // JC(2/26/2003):Hold the second field of an element in tab (defined below)  
  double *p; // JC(2/26/2003):Holds the second field of an element in classes(defined below)

  const char *wordSequence = env->GetStringUTFChars(str, 0);
  //  cout << "NATIVE CODE RECEIVES : " << wordSequence << "*" << endl;
  vector<string> *words = tokenizeString(wordSequence, " "); 
  string s;
  string question;
  for(vector<string>::iterator itWord = words->begin();
      itWord != words->end();
      itWord++) {
      s = (*itWord);
      if(!s.length()) break;

      if (s.length()>2)
	{
	  map<string,double>::iterator it = tab.find(s); // <-- JC(2/27/2003):Check the history of occurances for the current word.
	  if(it!=tab.end()) // JC(2/28/2003):If word is found in the history
	    {
	      // The words was there, so change the counts
	      // Decrease all others by a decay ratio
	      // JC(2/27/2003): Actually... it loops through the entire history file and decay
	      // without checking if the one being decayed is the current word... 
	      // Paper indicates that this is correct. Every word's history is decayed. 
	      double tot = 0.0;
	      for(map<string,double>::iterator it2=tab.begin(); it2!=tab.end(); ++it2)
		{
		  (*it2).second *= decay;
		  tot           += (*it2).second;
		}

	      // JC(2/27/2003):Increase the count on the word we just saw. Why didn't it just do this in the loop?  What's tot??? Not used anywhere else...
	      (*it).second += 1.0;
	      tot += 1.0;

	      // Recompute the class probabilities
	      VectorMove(probs,logWeights); //JC(2/27/2003): On first iteration of loop, neither vector has any data.
	      map<string,double>::iterator it3=tab.begin();
	      map<string,double *>::iterator it4=classes.begin();
	      // JC(2/27/2003): Why don't all the loops get combined? Seems like we're looping through tab unnecessarily too many times...
	      for( ; it3!=tab.end() ; )
		{
		  N = (*it3).second; //JC(2/27/2003):the history of the word in the conversation
		  p = (*it4).second; //JC(2/27/2003):the statistics of the word in the training set... 
		  for (c=0; c<cTotal; c++) probs->d[c] += N*p[c];
		  it3++;
		  it4++;
		}

	      // Print out for Yuri's Calcuation
	      //VectorPrint(stdout,probs); fprintf(stdout,"\n");
	      fflush(stdout);

	      // CAREFUL!!! COULD UNDERFLOW... try comupting the max and subtracting it out...
	      double biggy  = probs->d[0];
	      int    biggyI = 0;
	      for (c=1; c<cTotal; c++) if (biggy<probs->d[c]) // JC(2/27/2003):The most importnat part here: Find the biggest probability (most likely topic).
	      {
		 biggy  = probs->d[c];
		 biggyI = c;
	      }
	      // JC(2/27/2003): No... subtraction actually starts here... Another loop??? WHY??
	      for (c=0; c<cTotal; c++) probs->d[c] -= biggy;
	      // JC(2/27/2003): End of subtracting out the max?

	      for (c=0; c<cTotal; c++) probs->d[c] = exp(probs->d[c]); // JC(2/27/2003):Take every probability to e^(prob)
	      VectorScale(probs,1.0/VectorSum(probs)); // JC(2/27/2003):Multiply every element in prob. by the sum of its inverse? 
	      VectorMove((Vector) graph1, (Vector) graph2);
	      memcpy(graph2->d,&graph1->d2[1][0],(width-1)*cTotal*sizeof(double));
	      for (c=0;c<cTotal;c++) graph2->d2[width-1][c] = probs->d[c];
	      question = cQuestions[biggyI];
	    } // end if word in history 
	} // end if string is greater than 1 char
  } // end for loop

   // JC(2/28/2003):clean up memory
  //  printf("11111111111111\n");
    delete words; 
  //  printf("22222222222222\n");

  // Release memory used to hold input params
  env->ReleaseStringUTFChars(str, wordSequence);

  //  printf("33333333333333\n");

  //Return the keyword found using a callback method in Java
  jclass cls = env->GetObjectClass(obj);
  jmethodID mid = env->GetMethodID(cls, "setKeyword", "(Ljava/lang/String;)V");
  if (mid == 0) {
    return;
  }

  //  printf("44444444444444\n");

  int len = strlen(question.c_str());
  // wchar_t *buf = 0;
  //  cout << question << endl;
  // printf("-%s-$d-", question.c_str(), len);
  jstring jQuestion = 0;
  if(len > 0) {
    //    printf("4aaaaaaaa1\n");
    //len = mbstowcs(NULL, question.c_str(), MB_CUR_MAX);
    //    printf("4aaaaaaaa2\n");
    //buf = (wchar_t *)malloc(len*2 + 1);
    //    printf("4aaaaaaaa3\n");
    //mbstowcs(buf, question.c_str(), len);
    //    printf("4aaaaaaaa4\n");
    //jQuestion = env->NewString((jchar *)buf, len*2 + 1);
    //    printf("4aaaaaaaa5\n");
    //    env->CallVoidMethod(obj, mid, jQuestion);
    //    printf("4bbbbbbbb\n");
    //free(buf);    
    jQuestion = env->NewStringUTF(question.c_str()); // this makes things easier
        env->CallVoidMethod(obj, mid, jQuestion);
    }
  //  printf("5555555555555\n");

}


JNIEXPORT void JNICALL Java_psl_memento_pervasive_recommendation_keywordfinder_jebara_impKeywordFinder_cleanUp
  (JNIEnv *, jobject)
{

  /* This method does not work, we need to fix this - Julia?
  VectorFree(cCounts);
  VectorFree(logWeights);
  VectorFree(probs);
  MatrixFree(graph1);
  MatrixFree(graph2); */
}

//JC(2/28/2003): Add this file to tokenize incoming string data 
vector<string> *tokenizeString(string const str, string const delim) 
{
  vector<string> *tokens;
  tokens = new vector<string>();
  char *charDelim = (char *)delim.c_str();
  char *strToken = strtok((char *)str.c_str(), charDelim);
  while(strToken != NULL) {
    tokens->push_back(strToken);
    strToken = strtok(NULL, charDelim);
  }
  return tokens;
} 
