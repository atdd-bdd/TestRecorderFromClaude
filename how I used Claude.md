# Using Claude Code to create an application.

I had feature files that I had written to create an application that kept track of manually run tests.   The application included a database.   This is an example of using BDD to drive development. The human created application is here:  [GitHub - atdd-bdd/TestRecorder: Record manually run tests](https://github.com/atdd-bdd/TestRecorder)    

In this version, I used Claude Code to create an AI generated program.  The prompts were the same feature files for the human created program.  Here are some notes on what I experienced while using Claude.   

A litte note on the design, which is implied in the Configuration file.   There is providers for Date/Time and Runner (the person running the test).   They are defaulted to test doubles which return values specified in Background.   In production, they use the system clock and the user identity.     

#### Issue 1 - The database.

I failed to tell Claude that a hsqldb was being used for storage.   It set up many of the step definitions to take a context which refered to a test double for the database.   I said a context was unnecearry, then it came up with a state object, which was also unnecessary.   It finally settled on a TestService that keeps track of the whether test doubles or real values are being used.  

#### Issue 2 Test failures

I needed to add a path to maven.   Then it was able to run the tests and see the failures.  It noted the failures and made changes to make them pass.   The explanation seemed reasonable.   It noted the missing step definitions and added them. 

#### Issue 3 -The UI

It had a partial UI that showed the tests, but no buttons for running a test, adding a test, and so forth.   I said do it and it went on it's merry way.   However, it then had to pause when a rate limit was exceeeded for my plan.  It did complete the UI when it had more tokens available. 

#### Issue 4 - The Tests

The feature files are the tests.   So I could see whether it tried to change a test in order to make it pass.  It did once.  I did not examine in detail the step definitions to see if everything was being checked.   I could make up some scenarios where the expected values were changed and see if they failed.  I'm not sure how it would handle expected failure of tests.   

#### Issue 5 - The UI Tests

The feature file had some manual tests (noted with @manual). I asked it to create a test runner with @manual tags.  It had no problem doing so.  There was an issue with how it dealt with the test doubles for these manual tests.  But with additional prompting, it finally fixed those problems.   It explained why the issue was occuring.   

I had to add a couple of steps to the manual tests.   I wanted it to show a dialog box that displayed the data that the manual tested should enter into the application.   It initially used a modal dialog box, but switched it to modeless when I asked.

I asked it to create automated UI tests from the manual tests.  It used Robot from swing . That seemed to work fine.   

#### Issue 6 - The Compiler

Claude decided to switch to Java 11, even though the lastest Java was set in the project file for IntelliJ.   It did correct some code so that everything would compile under Java 11.  

#### Performance

I was doing this on an older laptop.  I'm presuming that much of the processing is done in the cloud and only the commands running locally.   It spent much of the time in "Thinking" mode (with a number of other synonyms displayed).   It displayed reasonable prompts for the next steps I might ask it to do.    I'll try doing it on a souped-up desktop and see if that makes any difference.  



#### Notes

The feature files did not have the extended data statements that describe each of the fields in each step table.  I think the fields were pretty straightforward, especially since the domain terms each had scenarios.  

I started this project and then paused working on it for a month or so while on vacation.   So it may look like it took a long while.  

#### Overall

I learned that my feature files have a few more steps in order to do things that I would have done in the step definitions.  However that clarity might help them more readable for humans.   Since the scenarios were the specifications/tests, it did not seem to have problems with the business logic.   
