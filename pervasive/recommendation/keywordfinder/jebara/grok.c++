#include <iostream>
#include <fstream>
#include <string>
#include <map>
#include <stdlib.h>
#include <stdio.h>
#include <getopt.h>

using namespace std;
 
void Usage()
{
   cerr << "Usage: grok -f <topic_file> -o <out_name> -c" << endl <<
      "-f \t load topic description from file <topic_file> (default: topics)" 
	<<endl <<
      "\tTopic file should start with a topic count" << endl <<
      "-o \t dump data into files <out_name>.topics, <out_name>.counts and "
	<< endl <<
         " \t <out_name>.stats. "
	<<endl <<
      "-c \t dump only words with counts greater than 1" << endl <<
      "-h \t this message. " << endl;

   exit(1);
}

void loadtopic(int ntopics, int tindex, string topic, map<string,double*> &base)
{
   string word;
   ifstream ftopic(topic.c_str());
   while(1) 
   {
      ftopic >> word;
      if(ftopic.eof()) break;
      
      if (word.length()>2)
      {
	 map<string,double*>::iterator it = base.find(word);
	 if(it!=base.end())  // the word was there.
	    (*it).second[tindex]++;
	 else
	 {
	    base[word] = new double[ntopics];
	    for(int i = 0; i < ntopics; i++)
	       base[word][i] = 1;
	 }
      }
   }
}

void cleanmap(map<string,double*> &base)
{
    for(map<string,double*>::iterator it=base.begin(); it!=base.end(); ++it)
       delete[] (*it).second;
}

int main(int argc, char **argv)
{
    char topics[25600];
    char baseout[25600];
    char outtopics[25600];
    char outcounts[25600];
    char outstats[25600];

    ofstream fouttopics, 
             foutcounts, 
             foutstats;
    ifstream ftopics;
    
    signed char c;
    int dumpclean = 0;

    map<string, double*> base;

    strcpy(topics, "topics");
    strcpy(baseout, "model");

    while((c = getopt(argc, argv, "f:o:ch")) != -1)
    {
       switch(c)
       {
       case 'f':
          strcpy(topics, optarg);
	  break;
       case 'o':
          strcpy(baseout, optarg);
	  break;
       case 'c':
          dumpclean = 1;
	  break;
       case 'h':
          Usage();
	  break;
       default:
          Usage();
       }
    }
    
    ftopics.open(topics);
    if(!ftopics)
       Usage();
    
    sprintf(outtopics,  "%s.topics", baseout);
    sprintf(outcounts,  "%s.counts", baseout);
    sprintf(outstats ,  "%s.stats" , baseout);
    fouttopics.open(outtopics);
    if(!fouttopics)
       Usage();
    foutcounts.open(outcounts);
    if(!foutcounts)
       Usage();
    foutstats.open(outstats);
    if(!foutstats)
       Usage();
    
    string topic;

    int count = 0;
    while(1)
    {
       ftopics >> topic;
       if(ftopics.eof()) break;

       count++;
    };

    printf("Count=%d\n",count);

    ftopics.close();
    if(!count)
       Usage();

    ifstream ftopics2;

    ftopics2.open(topics);
    if(!ftopics2)
       Usage();

    for(int i = 0; i < count; i++)
    {
       ftopics2 >> topic;
       if(ftopics2.eof()) break;
       
       loadtopic(count, i, topic, base);
       fouttopics << topic << endl;
       printf("Topic=%d of %d\n",i,count);
    }

    fouttopics.close();

    double *classcount = new double[count];
    memset(classcount, 0, count * sizeof(double));

    for(map<string,double*>::iterator it=base.begin(); it!=base.end(); ++it)
    {
       double sum = 1000000;
       if(dumpclean)
       {
	  sum = 0;
	  for(int i = 0; i < count; i++)
	     sum += (*it).second[i];
       }
       if(sum > count)
       {
	  foutstats << (*it).first << "\t";
	  for(int i = 0; i < count; i++)
	  {
	     foutstats << (*it).second[i] << "\t";
	     classcount[i] += (*it).second[i];
	  }
	  foutstats << endl;
       }
    }
    foutstats.close();

    for(int i = 0; i < count; i++)
       foutcounts << classcount[i] << endl;
    foutcounts.close();
    
    delete[] classcount;
    cleanmap(base);
}






















