Scott Walker
============
Hello! I'm a software developer based in London. I have a lot of experience working in Agile, TDD environments. I really enjoy functional programming and have put together this Scala application as a way to practice techniques.

A few of the big government projects ([Carer's Allowance](https://github.com/Department-for-Work-and-Pensions/carers-allowance-prototype) and [DVLA](https://github.com/dvla)) are in the process of being open sourced.

You can find out more about the other projects I've worked on through my [![View Scott Walker's profile on LinkedIn](https://static.licdn.com/scds/common/u/img/webpromo/btn_profile_greytxt_80x15.png)](http://uk.linkedin.com/in/scottwalkerlondon)

    
Mind
====

Build status for master branch [![Build Status](https://travis-ci.org/scottkwalker/Mind.svg?branch=master)](https://travis-ci.org/scottkwalker/Mind)

Code coverage for master branch [![Coverage Status](https://coveralls.io/repos/scottkwalker/Mind/badge.png)](https://coveralls.io/r/scottkwalker/Mind)

Latest version [![GitHub version](https://badge.fury.io/gh/scottkwalker%2FMind.svg)](http://badge.fury.io/gh/scottkwalker%2FMind)

This is a Scala hobby project. It will consist of:

* one RESTful micro-service that will generate a table of legal moves in a programming language according to whatever rules you give it
* one micro-service that will call the first micro-service to get a list of legal moves and then use AI to pick the next move. I want to experiment with algorithms such as Ant Colony Optimisation (ACO).

It doesn't yet have the RESTful routes as there is plenty of work to do in the back end first.

Development prerequisites
-----------------------
1.  JDK 1.7.51 or 1.8 must be installed

2.  Install SBT.  The [current documentation][install-sbt] suggests:

    Mac: `brew install sbt`

3.  If you are using Java 7 then you should increase the 'permanent generation space' requirements for SBT. Note: PermGen is managed automatically in Java 8.

    Mac: Create the file `~/.sbtconfig` with the following content:

        SBT_OPTS="-XX:+UseConcMarkSweepGC -XX:+CMSClassUnloadingEnabled -XX:PermSize=256M -XX:MaxPermSize=2048M"

Web framework
-------------
I am using [Play framework](http://www.playframework.com/documentation/2.3.x/Home) because:

* I am familiar with the framework from using it for projects for DWP Carer's Allowance and the DVLA.
* JSON writing and reading is done in what I find to be a very intuitive way ([JSON macro inception](http://www.playframework.com/documentation/2.2.3/ScalaJsonInception)).
* It offers the tools to create a rich front end.
* Although Spray has higher throughput it is very different to write the routes and I don't require throughput at the level.

Continuous Integration
----------------------
I am using [Travis CI](https://travis-ci.org/scottkwalker) as my build server because:

* It offers incredibly fast setup with a Github account.
* It is hosted in the cloud (unlike my previous Jenkins server that was running as a local instance).
* It automatically builds branches.
* It automatically builds forks!
* It is free.

Code coverage
-------------
When the Continuous Integration build runs, the tests will gather coverage statistics and post the results online to [Coveralls](https://coveralls.io).

Offline I run the sbt plugin for [Scoverage](https://github.com/scoverage/sbt-scoverage). From the sbt console run 'scoverage:test' to make it run the tests and output statistics to html files.

Previously I was using Jacoco offline, but the problems are:

* It only records line-coverage. For example, when operating on lists we may chain several operations together on a single line. If our tests all exit in the first few operations and never reach the final operation then this will be recorded as a false positive.
* It gave many false-negatives when running on parts of the Play framework such as the reverse routing. This may be because it runs against the Java compiled code and is unaware of how to track the Scala.

Version control
---------------

I am using Git with Github because:

* I am familiar with Github from using it on several projects.
* I prefer using Git for version control as it works well with TDD's short iterations of coding. Also Git is great for local commits when I am working from a train with no 4G signal.

[install-sbt]: http://www.scala-sbt.org/release/docs/Getting-Started/Setup.html#installing-sbt "Install SBT"